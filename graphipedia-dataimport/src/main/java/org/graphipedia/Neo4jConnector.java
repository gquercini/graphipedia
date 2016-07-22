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

import java.io.IOException;
import java.util.logging.Logger;

import org.graphipedia.dataimport.NodeAttribute;
import org.graphipedia.dataimport.NodeLabel;
import org.graphipedia.progress.ReadableTime;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

/**
 * This class contains the necessary methods to connect to 
 * the Neo4j database.
 *
 */
public class Neo4jConnector {
	
	/**
	 * The logger of Graphipedia.
	 */
	private final Logger logger;
	
	/**
	 * The settings of Graphipedia.
	 */
	private final GraphipediaSettings settings;
	
	/**
	 * Constructor.
	 * @param settings The settings of Graphipedia.
	 * @param logger The logger of Graphipedia.
	 */
	public Neo4jConnector(GraphipediaSettings settings, Logger logger) {
		this.settings = settings;
		this.logger = logger;
	}
	
	/**
	 * Opens a connection to the Neo4j database to import data.
	 * Indexes will be created upon shutdown.
	 * @return The connection to the Neo4j database.
	 * 
	 */
	public BatchInserter connectToNeo4jInserter() {
		BatchInserter inserter = null;

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
		return inserter;
	}
	
	/**
	 * Disconnects from the Neo4j database after importing data.
	 * @param inserter The connection to shut down.
	 */
	public void disconnectFromNeo4jInserter(BatchInserter inserter) { 
		long startTime = System.currentTimeMillis();
		logger.info("Disconnecting from the Neo4j database " + settings.neo4jDir() + " and creating indexes (this might take a while)...");
		inserter.shutdown();
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info("Disconnected from the Neo4j database " + settings.neo4jDir() + " in " + ReadableTime.readableTime(elapsed));
	}
	
	/**
	 * Connects to the Neo4j database to read data.
	 * @return The connection to the database.
	 */
	public GraphDatabaseService connectToNeo4jReader() {
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(settings.neo4jDir());
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
		return graphDb;
	}
	
	/**
	 * Closes the connection to the Neo4j database that is used to read data..
	 * @param graphDb The connection to shut down.
	 */
	public void disconnectFromNeo4jReader(GraphDatabaseService graphDb) {
		graphDb.shutdown();
	}

	/**
	 * Opens a connection to the Neo4j database to import data.
	 * No indexes will be created upon shutdown.
	 * @return The connection to the database.
	 * 
	 */
	public BatchInserter connectToNeo4jInserterNoIndexes() {
		BatchInserter inserter = null;
		logger.info("Connecting to the Neo4j database " + settings.neo4jDir().getAbsolutePath() + " ... ");
		try {
			inserter = BatchInserters.inserter(settings.neo4jDir());
		} catch (IOException e) {
			logger.severe("Error while connecting to the Neo4j database.\n" + e.getMessage());
		}
		logger.info("Connected to the Neo4j database " + settings.neo4jDir());
		return inserter;
	}

}
