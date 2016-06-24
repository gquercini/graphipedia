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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.graphipedia.CheckPoint;
import org.graphipedia.GraphipediaSettings;
import org.graphipedia.dataimport.neo4j.ImportCrossLinks;
import org.graphipedia.dataimport.neo4j.ImportGraph;
import org.graphipedia.dataimport.neo4j.NodeAttribute;
import org.graphipedia.dataimport.neo4j.NodeLabel;
import org.graphipedia.dataimport.wikipedia.Namespaces;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

/**
 * This thread imports the specified Wikipedia editions into a Neo4j database.
 * 
 * The input to Graphipedia is a set of directories, each containing the files 
 * corresponding to a Wikipedia language edition (as of a certain date). 
 * The name of a directory is the language code of the corresponding Wikipedia edition.
 * Therefore, the directory named "en" contains the files of the English Wikipedia.
 * Each directory must contain the following files that can be downloaded from   
 * <a href="https://dumps.wikimedia.org">Wikimedia</a>:
 * <ul>
 * <li> *-pages-articles.xml.bz2: this file contains the whole content of Wikipedia.
 * <li> *-langlinks.sql.gz : This file lists the cross-language links, connecting articles describing the same concept across different language editions.
 * <li> *-geo_tags.sql.gz: This file lists the geographic coordinates associated to Wikipedia articles describing spatial entities.
 * </ul>
 * 
 */
public class ImportWikipedia extends Thread {

	/**
	 * The connection to the Neo4j database used to read data. 
	 */
	private GraphDatabaseService graphDb;

	/**
	 * The connection to the Neo4j database used to import data.
	 */
	private BatchInserter inserter;

	/**
	 * The logger of the program.
	 */
	private Logger logger;

	/**
	 * The settings of Graphipedia.
	 */
	private GraphipediaSettings settings;

	/**
	 * The checkpoint information of Graphipedia.
	 */
	private CheckPoint checkpoint;

	/**
	 * The root categories of the disambiguation pages across all Wikipedia language editions.
	 */
	private Map<String, String> dpRootCategories;

	/**
	 * The root categories of the infobox templates across all Wikipedia language editions.
	 */
	private Map<String, String> itRootCategories;

	/**
	 * For each Wikipedia language edition to import, the list of the namespaces (each namespace has a name that is language-dependent).
	 */
	private Namespaces[] namespaces;

	/**
	 * Creates a new instance of this thread.
	 * @param settings The settings of Graphipedia.
	 * @param checkpoint The checkpoint information of Graphipedia.
	 * @param logger The logger of Graphipedia. 
	 */
	public ImportWikipedia(GraphipediaSettings settings, CheckPoint checkpoint, Logger logger) {
		this.logger = logger;
		this.settings = settings;
		this.checkpoint = checkpoint;
		this.dpRootCategories = new HashMap<String, String>();
		this.itRootCategories = new HashMap<String, String>();
	}

	@Override
	public void run() {
		try {
			loadResources();
		} catch (IOException e) {
			logger.severe("Error while reading the resources. This should not happen");
			e.printStackTrace();
			System.exit(-1);
		}
		connectToNeo4jInserter();
		importWikipediaEditions();
		disconnectFromNeo4jInserter();
		importCrossLinks(); 
		
	}

	/**
	 * Loads the resources, namely the disambiguation pages root categories and the infobox templates root categories.
	 * @throws IOException when something goes wrong while reading the resources.
	 */
	private void loadResources() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		loadResource(classLoader.getResourceAsStream("dp-root-categories.csv"), this.dpRootCategories);
		loadResource(classLoader.getResourceAsStream("it-root-categories.csv"), this.itRootCategories);
	}

	/**
	 * Loads a resource.
	 * @param file The file containing the resource.
	 * @param destination Wheere the destination is loaded.
	 * @throws IOException when something goes wrong while reading the resource.
	 */
	private void loadResource(InputStream file, Map<String, String> destination) throws IOException {
		BufferedReader bd = new BufferedReader(new InputStreamReader(file));
		String line;
		while( (line = bd.readLine()) != null ) { 
			String[] values = line.split("\t");
			destination.put(values[0], values[1]);
		}
		bd.close();

	}

	/**
	 * Opens a connection to the Neo4j database to import data.
	 * Indexes will be created upon shutdown.
	 * 
	 */
	private void connectToNeo4jInserter() {

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

		inserter.createDeferredSchemaIndex(NodeLabel.Disambig).on(NodeAttribute.title.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Disambig).on(NodeAttribute.lang.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Disambig).on(NodeAttribute.wikiid.toString()).create();

		inserter.createDeferredSchemaIndex(NodeLabel.Category).on(NodeAttribute.title.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Category).on(NodeAttribute.lang.toString()).create();
		inserter.createDeferredSchemaIndex(NodeLabel.Category).on(NodeAttribute.wikiid.toString()).create();

		logger.info("Connected to the Neo4j database " + settings.neo4jDir());
	}

	/**
	 * Connects to the Neo4j database to read data.
	 */
	private void connectToNeo4jReader() {
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(settings.neo4jDir());
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );

	}

	/**
	 * Closes the connection to the Neo4j database that is used to read data..
	 */
	private void disconnectFromNeo4jReader() {
		this.graphDb.shutdown();
	}

	/**
	 * Opens a connection to the Neo4j database to import data.
	 * No indexes will be created upon shutdown.
	 * 
	 */
	private void connectToNeo4jInserterNoIndexes() {
		logger.info("Connecting to the Neo4j database " + settings.neo4jDir().getAbsolutePath() + " ... ");
		try {
			inserter = BatchInserters.inserter(settings.neo4jDir());
		} catch (IOException e) {
			logger.severe("Error while connecting to the Neo4j database.\n" + e.getMessage());
		}
		logger.info("Connected to the Neo4j database " + settings.neo4jDir());
	}

	/**
	 * Disconnects from the Neo4j database after importing data.
	 */
	private void disconnectFromNeo4jInserter() { 
		long startTime = System.currentTimeMillis();
		logger.info("Disconnecting from the Neo4j database " + settings.neo4jDir() + " and creating indexes (this might take a while)...");
		inserter.shutdown();
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info("Disconnected from the Neo4j database " + settings.neo4jDir() + " in " + ReadableTime.readableTime(elapsed));
	}

	/**
	 * Imports the Wikipedia language editions specified in the Graphipedia settings.
	 */
	private void importWikipediaEditions() {
		try {
			String[] languages = settings.languages();
			this.namespaces = new Namespaces[languages.length];
			ImportGraph importer = null; 
			for ( int i = 0; i < languages.length; i += 1 ) {
				DisambiguationPageExtractor dpExtractor = 
						new DisambiguationPageExtractor(settings, languages[i], dpRootCategories.get(languages[i]), checkpoint);
				dpExtractor.start();
				ExtractNamespaces nsExtractor = new ExtractNamespaces(settings, languages[i]);
				nsExtractor.start();
				InfoboxTemplatesExtractor itExtractor = 
						new InfoboxTemplatesExtractor(settings, languages[i], itRootCategories.get(languages[i]), checkpoint);
				itExtractor.start();
				dpExtractor.join();
				nsExtractor.join();
				itExtractor.join();
				ExtractLinks extractor = new ExtractLinks(settings, languages[i], 
						dpExtractor.disambiguationPages(), itExtractor.infoboxTemplates(), nsExtractor.namespaces(), checkpoint);
				ExtractGeoTags geotagsExtractor = new ExtractGeoTags(settings, languages[i]);
				extractor.start();
				geotagsExtractor.start();
				this.namespaces[i] = nsExtractor.namespaces();
				extractor.join();
				geotagsExtractor.join();
				if ( importer != null )
					importer.join();
				importer = new ImportGraph(inserter, settings, languages[i], geotagsExtractor.getGeoTags());
				importer.start();
			}
			if (importer != null) // wait for the last importer to complete.
				importer.join();
		}
		catch(InterruptedException e) {
			logger.severe("Something wrong with the threads. Should not happen");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Imports the cross-language links.
	 */
	private void importCrossLinks() {
		try {
			logger.info("Import cross-language links");
			String[] languages = settings.languages();
			connectToNeo4jReader();
			for ( int i = 0; i < languages.length; i +=5 ) {
				int j = Math.min(5, languages.length - i);
				ExtractCrossLinks[] extractors = new ExtractCrossLinks[j];
				for ( int k = 0; k < extractors.length; k += 1 ) {
					extractors[k] = new ExtractCrossLinks(this.graphDb, settings, languages, namespaces, i + k, checkpoint);
					extractors[k].start();
				}
				for ( int k = 0; k < extractors.length; k += 1 ) 
					extractors[k].join();
			}
			disconnectFromNeo4jReader();
			connectToNeo4jInserterNoIndexes();
			for ( int i = 0; i < languages.length; i +=1 ) {
				ImportCrossLinks importer = new ImportCrossLinks(settings, inserter, languages[i]);
				importer.start();
				importer.join();
			}
			disconnectFromNeo4jInserter();
		}
		catch(InterruptedException e) {
			logger.severe("Something wrong with the threads. Should not happen");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
