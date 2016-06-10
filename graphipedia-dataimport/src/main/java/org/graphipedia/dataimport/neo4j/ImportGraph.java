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
package org.graphipedia.dataimport.neo4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.graphipedia.dataimport.ExtractLinks;
import org.graphipedia.dataimport.DataImportSettings;
import org.graphipedia.dataimport.LoggerFactory;
import org.graphipedia.dataimport.wikipedia.Page;
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
     * Creates a new thread.
     * 
     * @param inserter The Neo4j object that is used to quickly add nodes and links to a graph.
     * @param settings The settings of Graphipedia.
     * @param language The code of the language of the Wikipedia edition being imported.
     */
    public ImportGraph(BatchInserter inserter, DataImportSettings settings, String language) {
    	this.language = language;
    	this.inserter = inserter;
        inMemoryIndex = new HashMap<String, Page>();
        this.logger = LoggerFactory.createLogger("Graph import [" + this.language.toUpperCase() + "]");
        this.temporaryLinkFile = new File(settings.wikipediaEditionDirectory(language), ExtractLinks.TEMPORARY_LINK_FILE);
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
       // this.temporaryLinkFile.delete();
    }

    /**
     * Creates the nodes of the graph. Each node corresponds to a Wikipedia page.
     * @throws Exception when something goes wrong.
     */
    public void createNodes() throws Exception {
        logger.info("Importing pages...");
        NodeCreator nodeCreator = new NodeCreator(inserter, inMemoryIndex, language, logger);
        long startTime = System.currentTimeMillis();
        nodeCreator.parse(temporaryLinkFile.getAbsolutePath());
        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        logger.info(String.format("%d pages imported in %d seconds.\n", nodeCreator.getPageCount(), elapsedSeconds));
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
        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        logger.info(String.format("%d links imported in %d seconds\n", linkCreator.getLinkCount(), elapsedSeconds));
    }

}
