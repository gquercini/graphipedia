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
package org.graphipedia.dataimport;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.graphipedia.dataimport.neo4j.ImportCrossLinks;
import org.graphipedia.dataimport.neo4j.ImportGraph;
import org.graphipedia.dataimport.neo4j.NodeAttribute;
import org.graphipedia.dataimport.neo4j.NodeLabel;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

/**
 * Main class. 
 * Graphipedia imports the specified Wikipedia edition into a Neo4j database.
 * 
 * The input to Graphipedia is a set of directories, each containing the files 
 * corresponding to a Wikipedia language edition (as of a certain date). 
 * The name of a directory is the language code of the corresponding Wikipedia edition.
 * Therefore, the directory named "en" contains the files of the English Wikipedia.
 * Each directory must contain the following files that can be downloaded from   
 * <a href="https://dumps.wikimedia.org">Wikimedia</a>:
 * <ul>
 * <li> *-pages-articles.xml.bz2: this file contains the whole content of Wikipedia.
 * <li> *-redirect.sql.gz: this file lists the titles of all the redirect pages.
 * <li> *-geo_tags.sql.gz: This file lists the geographic coordinates associated to Wikipedia articles describing spatial entities.
 * <li> *-langlinks.sql.gz : This file lists the cross-language links, connecting articles describing the same concept across different language editions.
 * </ul>
 * 
 * The command line argument of Graphipedia is the path to the file with all the settings. 
 * The configuration file must contain a line for each setting, namely:
 * <ul>
 * <li> languages. The list of codes of the languages of the Wikipedia editions to import (separated by a comma). To import the English, French and Italian Wikipedia,
 * specify: languages=en,fr,it
 * <li> neo4jdir. The directory of the Neo4j database.
 * <li> 
 * </ul>
 * 
 * Graphipedia will look in its working directory for directories named after the language codes specified in the configuration file.
 * If Graphidedia does not find these directories, an error is raised. 
 * 
 */
public class Graphipedia {

	/**
	 * The logger of the program.
	 */
	private Logger logger;

	/**
	 * The settings of Graphipedia.
	 */
	private DataImportSettings settings;

	/**
	 * The Neo4j object that is used to quickly add nodes and links to a graph.
	 */
	private BatchInserter inserter;

	/**
	 * Creates a new instance of Graphipedia.
	 * @param settingsFileName The name of the settings file. 
	 */
	public Graphipedia(String settingsFileName) {
		this.logger = LoggerFactory.createLogger("Graphipedia");
		File settingsFile = new File(settingsFileName);
		if ( !settingsFile.exists() || settingsFile.isDirectory() ) {
			logger.severe("The settings file " + settingsFileName + " does not exist or is a directory");
			System.exit(-1);
		}
		this.settings = new DataImportSettings(logger);
		try {
			this.settings.loadSettings(settingsFile);
		} catch (IOException e) {
			logger.severe("Error while reading the settings file : " + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Entry point of the program.
	 * @param args Command-line arguments.
	 * @throws InterruptedException when something goes wrong with the threads.
	 * This exception is not treated because it should not happen. 
	 * If it does, the stacktrace may give some indications as to the root cause.
	 */
	public static void main(String[] args) throws InterruptedException {

		if ( args.length != 1 ) {
			System.err.println("USAGE: java -jar Graphipedia.jar graphipedia-settings\n"
					+ "1) graphipedia-settings : Name of the file containing the settings of Graphipedia\n");
			System.exit(-1);
		}
		long startTime = System.currentTimeMillis();
		Graphipedia graphipedia = new Graphipedia(args[0]);
		graphipedia.connectToNeo4j();
		graphipedia.importWikipediaEditions();
		graphipedia.disconnectFromNeo4j();
		//graphipedia.importCrossLinks(); 
		long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
		graphipedia.logger.info(String.format("Wikipedia imported in %d seconds.\n", elapsedSeconds));
	}

	/**
	 * Connects to the Neo4j database where the Wikipedia is imported.
	 * 
	 */
	private void connectToNeo4j() {
		
		logger.info("Connecting to the Neo4j database " + settings.neo4jDir().getAbsolutePath() + " ... ");
		try {
			inserter = BatchInserters.inserter(settings.neo4jDir());
		} catch (IOException e) {
			logger.severe("Error while connecting to the Neo4j database.\n" + e.getMessage());
		}
		inserter.createDeferredSchemaIndex(NodeLabel.Article).on(NodeAttribute.title.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Article).on(NodeAttribute.lang.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Article).on(NodeAttribute.wikiid.toString()).create();
		
		inserter.createDeferredSchemaIndex(NodeLabel.Redirect).on(NodeAttribute.title.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Redirect).on(NodeAttribute.lang.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Redirect).on(NodeAttribute.wikiid.toString()).create();
		
		inserter.createDeferredSchemaIndex(NodeLabel.Category).on(NodeAttribute.title.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Category).on(NodeAttribute.lang.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Category).on(NodeAttribute.wikiid.toString()).create();
		
		logger.info("Connected to the Neo4j database " + settings.neo4jDir());
	}
	
	/**
	 * Connects to the Neo4j database without creating any index. 
	 */
	private void connectToNeo4jNoIndexes() {
		logger.info("Connecting to the Neo4j database " + settings.neo4jDir().getAbsolutePath() + " ... ");
		try {
			inserter = BatchInserters.inserter(settings.neo4jDir());
		} catch (IOException e) {
			logger.severe("Error while connecting to the Neo4j database.\n" + e.getMessage());
		}
		logger.info("Connected to the Neo4j database " + settings.neo4jDir());
	}
	
	/**
	 * Disconnects from the Neo4j database.
	 */
	private void disconnectFromNeo4j() { 
		logger.info("Disconnecting from the Neo4j database " + settings.neo4jDir() + " and creating indexes ...");
		inserter.shutdown();
		logger.info("Disconnected from the Neo4j database " + settings.neo4jDir());
	}

	/**
	 * Imports the Wikipedia language editions specified in the Graphipedia settings.. 
	 * @throws InterruptedException when something goes wrong with the threads.
	 */
	private void importWikipediaEditions() throws InterruptedException {
		String[] languages = settings.languages();
		for ( int i = 0; i < languages.length; i += 1 ) {
			//ExtractLinks extractor = new ExtractLinks(settings, languages[i]);
			//extractor.start();
			//extractor.join();
			ImportGraph importer = new ImportGraph(inserter, settings, languages[i]);
			importer.start();
			if  ( i == languages.length - 1) // if the last extractor has been launched, wait for the last importer to finish before shutting down the connection to the database.
				importer.join();
		}	
	}
	
	/**
	 * Imports the cross-language links.
	 * @throws InterruptedException when something goes wrong with the threads.
	 */
	private void importCrossLinks() throws InterruptedException {
		logger.info("\nImport cross-language links");
		connectToNeo4jNoIndexes();
		String[] languages = settings.languages();
		for ( int i = 0; i < languages.length; i +=1 ) {
			ExtractCrossLinks extractor = new ExtractCrossLinks(settings, languages, i);
			extractor.start();
			extractor.join();
			ImportCrossLinks importer = new ImportCrossLinks(settings, inserter, languages[i]);
			importer.start();
			if  ( i == languages.length - 1)
				importer.join();
		}
		disconnectFromNeo4j();
	}

}

























