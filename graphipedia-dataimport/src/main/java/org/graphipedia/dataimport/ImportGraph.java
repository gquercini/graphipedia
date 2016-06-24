//
// Copyright (c) 2012 Mirko Nasato
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.graphipedia.GraphipediaSettings;
import org.graphipedia.dataextract.ExtractLinks;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.progress.ReadableTime;
import org.graphipedia.wikipedia.Geotags;
import org.graphipedia.wikipedia.Page;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * This thread imports a Wikipedia language edition as a graph into a Neo4j database.
 * The input to this thread is the temporary link file created by the thread {@link ExtractLinks}.  
 *
 */
public class ImportGraph extends Thread {
	
	/**
	 * The logger of this class.
	 */
	private Logger logger; 
    
   
    /**
     * The code of the language of the Wikipedia edition being imported.
     */
    private String language;
    
    /**
     * The temporary link file created by the thread {@link ExtractLinks}.
     */
    private File temporaryLinkFile;
    
    /**
	 * The Neo4j object that is used to quickly add nodes and links to a graph.
	 */
    private final BatchInserter inserter;
    
    /**
     * An index of the nodes of the graph.
     */
    private final Map<String, Page> inMemoryIndex;
    
    /**
     * The geotags associated to pages that describe spatial entities.
     */
    private final Map<String, Geotags> geotags;

    /**
     * Creates a new thread.
     * 
     * @param inserter The Neo4j object that is used to quickly add nodes and links to a graph.
     * @param settings The settings of Graphipedia.
     * @param language The code of the language of the Wikipedia edition being imported.
     * @param geotags The geotags associated to Wikipedia pages that describe spatial entities.
     */
    public ImportGraph(BatchInserter inserter, GraphipediaSettings settings, String language, Map<String, Geotags> geotags) {
    	this.language = language;
    	this.inserter = inserter;
        inMemoryIndex = new HashMap<String, Page>();
        this.logger = LoggerFactory.createLogger("Graph import [" + this.language.toUpperCase() + "]");
        this.temporaryLinkFile = new File(settings.wikipediaEditionDirectory(language), ExtractLinks.TEMPORARY_LINK_FILE);
        this.geotags = geotags;
    }

    @Override
    public void run() {
        try {
			createNodes();
		} catch (Exception e) {
			logger.severe("Error while creating the nodes " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
        try {
			createLinks();
		} catch (Exception e) {
			logger.severe("Error while creating the links " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
        setAttributeNodes();
      
    }

    /**
     * Creates the nodes of the graph. Each node corresponds to a Wikipedia page.
     * @throws Exception when something goes wrong.
     */
    public void createNodes() throws Exception {
        logger.info("Importing pages...");
        NodeCreator nodeCreator = new NodeCreator(inserter, inMemoryIndex, language, logger, geotags); 
        long startTime = System.currentTimeMillis();
        nodeCreator.parse(temporaryLinkFile.getAbsolutePath());
        long elapsed = System.currentTimeMillis() - startTime;
        logger.info(String.format("%d pages imported in "+ ReadableTime.readableTime(elapsed) +"\n", nodeCreator.getPageCount()));
    }

    /**
     * Creates the links of the graph.
     * @throws Exception when something goes wrong.
     */
    public void createLinks() throws Exception {
    	logger.info("Importing links...");
        LinkCreator linkCreator = new LinkCreator(inserter, inMemoryIndex, logger);
        long startTime = System.currentTimeMillis();
        linkCreator.parse(temporaryLinkFile.getAbsolutePath());
        long elapsed = System.currentTimeMillis() - startTime;
        logger.info(String.format("%d links imported in " + ReadableTime.readableTime(elapsed) + "\n", linkCreator.getLinkCount()));
    }
    
    /**
     * Sets the values of the attributes of the nodes.
     */
    private void setAttributeNodes() {
    	logger.info("Setting the attributes of the nodes...");
    	AttributeNodeUpdater updater = new AttributeNodeUpdater(inserter, inMemoryIndex, logger);
    	long startTime = System.currentTimeMillis();
    	updater.update();
    	long elapsed = System.currentTimeMillis() - startTime;
    	logger.info(String.format("%d nodes updated in " + ReadableTime.readableTime(elapsed) + "\n", updater.getNodeCount()));
    }

}
