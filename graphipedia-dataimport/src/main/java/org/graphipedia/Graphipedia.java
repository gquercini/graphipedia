//
// Copyright (c) 2016 Gianluca Quercini
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
// THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
// OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
// ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.
//
package org.graphipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.graphipedia.dataimport.ImportWikipedia;
import org.graphipedia.download.WikipediaEdition;
import org.graphipedia.progress.CheckPoint;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.progress.ReadableTime;

/**
 * Main class of Graphipedia.
 * Graphipedia automatically downloads the necessary dump files of selected Wikipedia editions and imports 
 * them to a Neo4j database.
 * The data is downloaded to directory {@code ROOT_DIR}, that is created by Graphipedia itself.
 * Note that if this directory already exists (from a previous computation) Graphipedia will flag an error and 
 * will stop.  
 */
public class Graphipedia {

	/**
	 * Name of the directory that Graphipedia creates in the working directory to put all the necessary files to import
	 * the Wikipedia editions.
	 */
	public static final File ROOT_DIR = new File("graphipedia-data");
	
	/**
	 * The directory where the files of the Neo4j database are written.
	 */
	public static final File NEO4J_DIR = new File(ROOT_DIR, "neo4j-db");

	/**
	 * The logger of Graphipedia.
	 */
	private Logger logger;

	/**
	 * The checkpoint information that is maintained by Graphipedia so as some operations do not need
	 * to be performed from scratch in case a previous computation fails.
	 */
	private CheckPoint checkpoint;
	
	/**
	 * The settings of Graphipedia.
	 */
	private GraphipediaSettings settings;

	/**
	 * Creates a new instance of Graphipedia.
	 */
	public Graphipedia() {
		this.logger = LoggerFactory.createLogger("Graphipedia");
		this.checkpoint = new CheckPoint(ROOT_DIR);
		this.settings = new GraphipediaSettings(NEO4J_DIR);
		logger.info("Graphipedia started");
	}

	/**
	 * Entry point of the program.
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		Graphipedia self = new Graphipedia();
		self.rootDirectory();
		self.loadCheckpoint();
		self.downloadEditions();
		self.importEditions();
	}
	
	/**
	 * Loads the checkpoint information from file. 
	 */
	private void loadCheckpoint() {
		try {
			this.checkpoint.load();

		} catch (IOException e) {
			logger.severe("Error while reading the checkpoint file " + this.checkpoint.getCheckPointFile().getAbsolutePath());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Creates the root directory of Wikipedia, if it does not exist yet, and the
	 * Neo4j directory as a subdirectory of the root directory.
	 */
	private void rootDirectory() {
		boolean success = false;
		if ( !ROOT_DIR.exists() ) {
			success = ROOT_DIR.mkdir();
			if ( !success ) {
				logger.severe("Failed to create directory " + ROOT_DIR.getAbsolutePath());
				System.exit(-1);
			}
			success = NEO4J_DIR.mkdir();
			if (!success) {
				logger.severe("Failed to create directory " + NEO4J_DIR.getAbsolutePath());
				System.exit(-1);
			}
		}
		else
		{
			NEO4J_DIR.delete();
			success = NEO4J_DIR.mkdir();
			if (!success) {
				logger.severe("Failed to create directory " + NEO4J_DIR.getAbsolutePath());
				System.exit(-1);
			}
		}	
	}
	
	/**
	 * Downloads the dump files of all the Wikipedia editions.
	 */
	private void downloadEditions() {
		long startTime = System.currentTimeMillis();
		List<WikipediaEdition> wikipediaEditions = wikipediaEditions();
		int nbEditions = wikipediaEditions.size();
		int current = 0;
		for ( WikipediaEdition edition : wikipediaEditions ) {
			current += 1;
			String language = edition.language();
			String languageCode = edition.languageCode();
			String languageLocal = edition.languageLocal();
			long editionStartTime = System.currentTimeMillis();
			logger.info("Downloading the Wikipedia in " + language + " (" + languageLocal + ", " + languageCode.toUpperCase() + "), " + current + "/" + nbEditions);
			if ( !edition.download(checkpoint, logger, new File(ROOT_DIR, languageCode) ) )
				System.exit(-1);
			long editionElapsed = System.currentTimeMillis() - editionStartTime;
			settings.addLanguage(languageCode);
			logger.info("Downloaded the Wikipedia in " + language + " (" + languageLocal + ", " + languageCode.toUpperCase() + ") in " + ReadableTime.readableTime(editionElapsed) );
		}
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info("\n" + nbEditions + " Wikipedia editions downloaded in " + ReadableTime.readableTime(elapsed) + "\n\n");
	}
	
	/**
	 * Import the Wikipedia editions to Neo4j
	 */
	private void importEditions() {
		logger.info("Start importing the Wikipedia editions to Neo4j");
		long start = System.currentTimeMillis();
		ImportWikipedia wikipediaImport = new ImportWikipedia(settings, checkpoint, logger);
		wikipediaImport.start();
		try {
			wikipediaImport.join();
		} catch (InterruptedException e) {
			logger.severe("Something wrong with the threads. Should not happen");
			e.printStackTrace();
			System.exit(-1);
		}
		long elapsed = System.currentTimeMillis() - start;
		logger.info("Wikipedia imported in " + ReadableTime.readableTime(elapsed) + "\n");
	}
	
	/**
	 * Loads the list of Wikipedia editions from file.
	 * @return The list of Wikipedia editions to import.
	 */
	private List<WikipediaEdition> wikipediaEditions() {
		List<WikipediaEdition> wikipediaEditions = new ArrayList<WikipediaEdition>();
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			BufferedReader bd = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("wikipedias.csv")));
			String line;
			while( (line = bd.readLine()) != null ) {
				String[] values = line.split("\t");
				String language = values[0];
				String languageLocal = values[1];
				String languageCode = values[2];
				wikipediaEditions.add(new WikipediaEdition(language, languageLocal, languageCode));
			}
			bd.close();
		}
		catch(IOException e) {
			logger.severe("Error while reading the file listing all the Wikipedia editions to import");
			e.printStackTrace();
			System.exit(-1);
		}
		return wikipediaEditions;
	}
	
}

