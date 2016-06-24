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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.DirectoryScanner;

/**
 * The settings of Graphipedia.
 *
 */
public class GraphipediaSettings {
	
	/**
	 * The suffix of the name of the XML file containing the whole Wikipedia. 
	 */
	public static final String WIKIPEDIA_XML_FILE = "pages-articles.xml.bz2";
	
	/**
	 * The suffix of the name of the file that contains the cross-language links.
	 */
	public static final String WIKIPEDIA_CROSSLINKS_FILE = "langlinks.sql.gz"; 
	
	/**
	 * The suffix of the name of the file that contains the geotags associated to spatial entities.
	 */
	public static final String WIKIPEDIA_GEOTAGS_FILE = "geo_tags.sql.gz";

	/**
	 * The files that must be in the Wikipedia editions directories.
	 */
	public final static String[] WIKIPEDIA_EDITIONS_INPUT_FILES = 
			new String[]{WIKIPEDIA_XML_FILE, 
					WIKIPEDIA_CROSSLINKS_FILE, WIKIPEDIA_GEOTAGS_FILE}; 

	
	/**
	 * The codes of the languages of the Wikipedia editions to import.
	 */
	private List<String> languages;

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
	 * @param neo4jDir The directory where the Neo4j database is written.
	 */
	public GraphipediaSettings(File neo4jDir) {
		this.wikipediaEditions = new HashMap<String, File>();
		this.languages = new ArrayList<String>();
		this.neo4jDir = neo4jDir;
	}
	
	/**
	 * Returns the languages of the Wikipedia editions to import to Neo4j.
	 * @return The languages of the Wikipedia editions to import to Neo4j.
	 */
	public String[] languages() {
		String[] languages = new String[this.languages.size()];
		int i = 0;
		for ( String language : this.languages )
			languages[i++] = language;
		return languages;
	}
	
	/**
	 * Adds a new language to the settings.
	 * @param language The code of the language to add.
	 */
	public void addLanguage(String language) {
		this.languages.add(language);
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
	 * Returns the file containing the geotags associated to the Wikipedia pages
	 * that describe spatial entities in a specified language edition.
	 * @param language The code of the language of the specified Wikipedia edition. 
	 * @return The file that contains the geo-tags of the spatial entities.
	 */
	public File getGeotagsFile(String language) {
		return getWikipediaEditionFile(language,WIKIPEDIA_GEOTAGS_FILE);
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


//	/**
//	 * Performs some checks on the Neo4j directory.
//	 */
//	private void checkNeo4jDirectory() {
//		if ( neo4jDir.exists() && !neo4jDir.isDirectory() ) {
//			logger.severe(this.neo4jDir + " exists and is not a directory\n");
//			System.exit(-1);
//		}
//		else
//			if ( neo4jDir.exists() && neo4jDir.isDirectory() ) {
//				logger.severe("The directory " + this.neo4jDir + " already exists.");
//				System.exit(-1);
//			}
//			else
//				if ( !neo4jDir.exists() &&  !neo4jDir.mkdir()) {
//					logger.severe("Unable to create directory " + this.neo4jDir + " in the working directory. Check the permissions and retry.");
//					System.exit(-1);
//				}
//	}
//	
//	/**
//	 * Performs some checks on the directories that contain the Wikipedia editions to import.
//	 */
//	private void checkWikipediaEditionsDirectories() {
//		for ( String lang : languages ) {
//			File editionDir = new File(lang);
//			if ( !editionDir.exists() || !editionDir.isDirectory() ) {
//				logger.severe("directory " + editionDir + " does not exist or is not a directory");
//				System.exit(-1);
//			}
//			checkWikipediaEditionFiles(editionDir);
//			this.wikipediaEditions.put(lang, editionDir);
//		}
//	}
//
//	/**
//	 * Checks the existence of the necessary input files in the given directory that contains 
//	 * a specific Wikipedia edition.
//	 * @param editionDir A directory containing a specific Wikipedia edition.
//	 */
//	private void checkWikipediaEditionFiles(File editionDir) {
//		for ( String fileName : WIKIPEDIA_EDITIONS_INPUT_FILES ) {
//			DirectoryScanner scanner = new DirectoryScanner();
//			scanner.setIncludes(new String[]{"*"+fileName});
//			scanner.setBasedir(editionDir);
//			scanner.setCaseSensitive(false);
//			scanner.scan();
//			String[] files = scanner.getIncludedFiles();
//			if ( files.length == 0 ) {
//				logger.severe("No file *" + fileName + " found in directory " + editionDir.getAbsolutePath());
//				System.exit(-1);
//			}
//			else
//				if ( files.length > 1 ) {
//					logger.severe("Multiple files *" + fileName + " found in directory " + editionDir.getAbsolutePath());
//					System.exit(-1);
//				}	
//		}
//
//	}
//	
}
