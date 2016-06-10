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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.tools.ant.DirectoryScanner;

/**
 * This class lists all the settings of Graphipedia.
 *
 */
public class DataImportSettings {
	
	/**
	 * The property in the configuration file that indicates the 
	 * codes of the Wikipedia editions to import.
	 */
	private static final String LANGUAGES = "languages";
	
	/**
	 * The property in the configuration file that indicates
	 * the directory of the Neo4j database where the Wikipedia language editions are imported.
	 */
	private static final String NEO4J_DIR = "neo4jdir";
	
	/**
	 * The suffix of the name of the XML file containing the whole Wikipedia. 
	 */
	private static final String WIKIPEDIA_XML_FILE = "pages-articles.xml.bz2";
	
	/**
	 * The suffix of the name of the file that contains the cross-language links.
	 */
	private static final String WIKIPEDIA_CROSSLINKS_FILE = "langlinks.sql.gz"; 

	/**
	 * The files that must be in the Wikipedia editions directories.
	 */
	private final static String[] WIKIPEDIA_EDITIONS_INPUT_FILES = 
			new String[]{WIKIPEDIA_XML_FILE, 
					WIKIPEDIA_CROSSLINKS_FILE}; 

	/**
	 * The Graphipedia logger.
	 */
	private Logger logger;

	/**
	 * The codes of the languages of the Wikipedia editions to import.
	 */
	private String[] languages;

	/**
	 * The directory of the Neo4j database were Wikipedia is to be imported.
	 */
	private File neo4jDir;

	/**
	 * Map between each language and the directory that contains the files of the Wikipedia
	 * edition in that language. 
	 */
	private Map<String, File> wikipediaEditions;

	/**
	 * Creates a new instance of Graphipedia settings.
	 * @param logger The Graphipedia logger.
	 */
	public DataImportSettings(Logger logger) {
		this.logger = logger;
		this.wikipediaEditions = new HashMap<String, File>();
	}

	/**
	 * Loads the Graphipedia settings from the given input file.
	 * @param settingsFile The file containing the settings of Graphipedia.
	 * @throws IOException when something goes wrong while reading the settings file.
	 */
	public void loadSettings(File settingsFile) throws IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream(settingsFile);
		prop.load(input);
		this.languages = prop.getProperty(LANGUAGES).split(",");
		for ( int i = 0; i < languages.length; i += 1 )
			this.languages[i] = this.languages[i].toLowerCase();
		this.neo4jDir = new File(prop.getProperty(NEO4J_DIR));
		input.close();
		checkNeo4jDirectory();
		checkWikipediaEditionsDirectories();
	}
	
	
	/**
	 * Returns the languages of the Wikipedia editions to import to Neo4j.
	 * @return The languages of the Wikipedia editions to import to Neo4j.
	 */
	public String[] languages() {
		return this.languages;
	}
	
	/**
	 * Returns the directory of the Neo4j database were Wikipedia is to be imported.
	 * @return The directory of the Neo4j database were Wikipedia is to be imported.
	 */
	public File neo4jDir() {
		return this.neo4jDir;
	}
	
	/**
	 * Returns the directory that contains the files necessary to import a specific Wikipedia
	 * language edition.
	 * @param language The code of a language (e.g., 'en' for English)
	 * @return The directory that contains the files necessary to import the Wikipedia
	 * edition in the specified language. If the directory does not exists, it returns {@code null}.
	 */
	public File wikipediaEditionDirectory(String language) {
		return wikipediaEditions.get(language);
	}

	/**
	 * Performs some checks on the directories that contain the Wikipedia editions to import.
	 */
	private void checkWikipediaEditionsDirectories() {
		for ( String lang : languages ) {
			File editionDir = new File(lang);
			if ( !editionDir.exists() || !editionDir.isDirectory() ) {
				logger.severe("directory " + editionDir + " does not exist or is not a directory");
				System.exit(-1);
			}
			checkWikipediaEditionFiles(editionDir);
			this.wikipediaEditions.put(lang, editionDir);
		}
	}

	/**
	 * Checks the existence of the necessary input files in the given directory that contains 
	 * a specific Wikipedia edition.
	 * @param editionDir A directory containing a specific Wikipedia edition.
	 */
	private void checkWikipediaEditionFiles(File editionDir) {
		for ( String fileName : WIKIPEDIA_EDITIONS_INPUT_FILES ) {
			DirectoryScanner scanner = new DirectoryScanner();
			scanner.setIncludes(new String[]{"*"+fileName});
			scanner.setBasedir(editionDir);
			scanner.setCaseSensitive(false);
			scanner.scan();
			String[] files = scanner.getIncludedFiles();
			if ( files.length == 0 ) {
				logger.severe("No file *" + fileName + " found in directory " + editionDir.getAbsolutePath());
				System.exit(-1);
			}
			else
				if ( files.length > 1 ) {
					logger.severe("Multiple files *" + fileName + " found in directory " + editionDir.getAbsolutePath());
					System.exit(-1);
				}	
		}

	}
	
	/**
	 * Returns the XML file that contains the whole Wikipedia edition in the specified language.
	 * @param language The language code of the Wikipedia edition.
	 * @return The XML file that contains the whole Wikipedia edition in the specified language.
	 */
	public File getWikipediaXmlFile(String language) {
		return getWikipediaEditionFile(language, WIKIPEDIA_XML_FILE);
	}
	
	
	/**
	 * Returns the file with the cross-language links of the Wikipedia edition
	 * in the specified language.
	 * @param language The code of the language of the specified edition.
	 * @return The file with the cross-language links of the Wikipedia edition
	 * in the specified language.
	 */
	public File getCrossLinkFile(String language) {
		return getWikipediaEditionFile(language, WIKIPEDIA_CROSSLINKS_FILE);
	}
	
	/**
	 * Returns the file of the given Wikipedia language edition with the given name suffix. 
	 * @param language The language code of the Wikipedia edition.
	 * @param fileNameSuffix The suffix of the name of the file to return.
	 * @return The file of the given Wikipedia language edition with the given name suffix.
	 */
	private File getWikipediaEditionFile(String language, String fileNameSuffix) {
		File wikipediaDirectory = this.wikipediaEditionDirectory(language);
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[]{"*"+fileNameSuffix});
		scanner.setBasedir(wikipediaDirectory);
		scanner.setCaseSensitive(false);
		scanner.scan();
		String[] files = scanner.getIncludedFiles(); // we are sure we have only one file here because we already called checkWikipediaEditionFiles
		return new File(wikipediaDirectory, files[0]);
	}


	/**
	 * Performs some checks on the Neo4j directory.
	 */
	private void checkNeo4jDirectory() {
		if ( neo4jDir.exists() && !neo4jDir.isDirectory() ) {
			logger.severe(this.neo4jDir + " exists and is not a directory\n");
			System.exit(-1);
		}
		else
			if ( neo4jDir.exists() && neo4jDir.isDirectory() ) {
				logger.warning(this.neo4jDir + " already exists and the new Wikipedia editions will be added to this directory. Proceed? [Y/N]");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				try {
					if (!br.readLine().equalsIgnoreCase("Y"))
						System.exit(-1);
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
				if ( !neo4jDir.exists() &&  !neo4jDir.mkdir()) {
					logger.severe("Unable to create directory " + this.neo4jDir + " in the working directory. Check the permissions and retry.");
					System.exit(-1);
				}
	}
}
