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
package org.graphipedia.dataextract;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.graphipedia.GraphipediaSettings;
import org.graphipedia.dataimport.NodeAttribute;
import org.graphipedia.dataimport.NodeLabel;
import org.graphipedia.progress.CheckPoint;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.progress.ProgressCounter;
import org.graphipedia.progress.ReadableTime;
import org.graphipedia.wikipedia.Namespace;
import org.graphipedia.wikipedia.Namespaces;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;


/**
 * This thread extracts the cross-language links of a Wikipedia language edition from the 
 * SQL dump file of cross-language links.
 * The output is a CSV file where each line contains a link as two identifiers of nodes in
 * Neo4j separated by a comma.
 */
public class ExtractCrossLinks extends Thread {

	/**
	 * The output file name.
	 */
	public static final String OUTPUT_FILE_NAME = "cross-links.csv";

	/**
	 * Pattern to extract a set of cross-language links from the SQL file that contains the cross-language links.   
	 */
	private static final Pattern LINKS_PATTERN = Pattern.compile("INSERT INTO `langlinks` VALUES (.+)");

	/**
	 * Pattern to extract a cross-language link from the SQL file that contains the cross-language links.   
	 */
	private static final Pattern LINK_PATTERN = Pattern.compile("\\((.+?),'(.*?)','(.*?)'\\)");

	/**
	 * The logger of this thread.
	 */
	private Logger logger;

	/**
	 * The settings of the import.
	 */
	private GraphipediaSettings settings;


	/**
	 * The codes of the languages of the Wikipedia editions to import.
	 */
	private Set<String> languages;

	/**
	 * The namespaces in the Wikipedia editions to import.
	 */
	private Map<String, Namespaces> namespaces;

	/**
	 * The code of the language of the current Wikipedia edition.  
	 */
	private String currentLanguage;

	/**
	 * Tracks the progress of the import.
	 */
	private ProgressCounter linkCounter;

	/**
	 * The connection to the Neo4j database.
	 */
	private GraphDatabaseService graphDb;
	
	/**
	 * The checkpoint information of Graphipedia.
	 */
	private CheckPoint checkpoint;

	/**
	 * Creates a new cross-link importer with the specified parameters. 
	 * @param graphDb Connection to the Neo4j database to read data (useful to get the neo4j identifier of a node, based
	 * on the wiki id of the corresponding article.)
	 * @param settings The settings of the import.
	 * @param languages The codes of the languages of the Wikipedia editions to import. 
	 * @param namespaces The namespaces of the Wikipedia edition languages to import.
	 * @param currentLanguage An index in the array {@code languages}, indicating the current Wikipedia language edition.  
	 * @param checkpoint The checkpoint information of Graphipedia.
	 */
	public ExtractCrossLinks(GraphDatabaseService graphDb, GraphipediaSettings settings, String[] languages, 
			Namespaces[] namespaces, int currentLanguage, CheckPoint checkpoint) {
		this.graphDb = graphDb;
		this.settings = settings;
		this.languages = new HashSet<String>();
		this.namespaces = new HashMap<String, Namespaces>();
		for (  int i = 0; i < languages.length ; i += 1) {
			this.languages.add(languages[i]);
			this.namespaces.put(languages[i], namespaces[i]);
		}

		this.currentLanguage = languages[currentLanguage];
		logger = LoggerFactory.createLogger("Extract crosslinks  (" + this.currentLanguage.toUpperCase() + ")");
		linkCounter = new ProgressCounter(logger);
		this.checkpoint = checkpoint;
	}

	@Override
	public void run() {
		File outputFile = new File(settings.wikipediaEditionDirectory(currentLanguage), OUTPUT_FILE_NAME);
		if ( checkpoint.isCrossLinksExtracted(this.currentLanguage) ) {
			logger.info("Using the cross-links from a previous computation");
			return;
		}
		long startTime = System.currentTimeMillis();
		try ( Transaction tx = graphDb.beginTx() ) {
			String inputFile = settings.getCrossLinkFile(currentLanguage).getAbsolutePath();
			try {
				FileInputStream fin = new FileInputStream(inputFile);
				BufferedInputStream bis = new BufferedInputStream(fin);
				CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
				parse(input, outputFile);
				fin.close();
				bis.close();
				input.close();
			}
			catch(Exception e) {
				logger.severe("Error while reading file " + inputFile);
				e.printStackTrace();
				System.exit(-1);
			}
			tx.success();
		}
		try {
			checkpoint.addCrossLinksExtracted(this.currentLanguage, true);
		} catch (IOException e) {
			logger.severe("Error while saving the checkpoint to file");
			e.printStackTrace();
			System.exit(-1);
		}
		settings.getCrossLinkFile(currentLanguage).delete();
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info(String.format("%d links extracted in "+ ReadableTime.readableTime(elapsed) +"\n", linkCounter.getCount()));
	}

	/**
	 * Parse the SQL file that contains the cross-language links of a Wikipedia language edition
	 * and outputs the cross-links in a CSV file that is stored
	 * in the directory corresponding to that edition.
	 * 
	 * @param inputStream The input stream.
	 * @param outputFile The output file.
	 * @throws Exception when something goes wrong while reading/writing files.
	 */
	private void parse(InputStream inputStream, File outputFile) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedReader bd  = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = "";
		while( (line = bd.readLine()) != null ) {
			Matcher matcher = LINKS_PATTERN.matcher(line);
			if ( matcher.find() ) {
				String links = matcher.group(1);
				Matcher matcher1 = LINK_PATTERN.matcher(links);
				while(matcher1.find()) {
					String sourcePageWikiid = matcher1.group(1);
					String targetLang = matcher1.group(2);
					String targetPageTitle = matcher1.group(3);
					if ( !languages.contains(targetLang) )
						continue;
					if (targetPageTitle.length() == 1)
						targetPageTitle = targetPageTitle.toUpperCase();
					else if ( targetPageTitle.length() > 1 )
						targetPageTitle = targetPageTitle.substring(0, 1).toUpperCase() + targetPageTitle.substring(1);
					Namespace targetNamespace = this.namespaces.get(targetLang).wikipediaPageNamespace(targetPageTitle);
					if ( targetNamespace.id() == Namespace.MAIN || targetNamespace.id() == Namespace.CATEGORY ) {
						long sourcePageId = getNodeidByWikiid(sourcePageWikiid, currentLanguage);
						long targetPageId = getNodeIdByTitle(targetPageTitle, targetLang, targetNamespace);
						if( sourcePageId != -1 && targetPageId != -1 ) {
							bw.write(sourcePageId + "," + targetPageId + "\n");
							linkCounter.increment("Cross-links ");
						}
					}
				}

			}

		}
		inputStream.close();
		bd.close();
		bw.close();
	}


	/**
	 * Returns the Neo4j node  corresponding to the Wikipedia page with the specified identifier (identifier affected by Wikipedia)
	 * in the given Wikipedia language edition.
	 * @param wikiid The identifier affected by Wikipedia to a page.
	 * @param language The code of the language of the Wikipedia edition where the node is looked for.
	 * @return The identifier in Neo4j of the node that corresponds to the Wikipedia page with specified
	 * {@code wikiid} and {@code language}, if any, or {@code -1} otherwise.
	 */
	private long getNodeidByWikiid(String wikiid, String language) {
		ResourceIterator<Node> articles = graphDb.findNodes(NodeLabel.Article, NodeAttribute.wikiid.name(), wikiid);
		while(articles.hasNext()) {
			Node node = articles.next();
			if(((String)node.getProperty(NodeAttribute.lang.name())).equalsIgnoreCase(language))
				return node.getId();
		}

		ResourceIterator<Node> categories = graphDb.findNodes(NodeLabel.Category, NodeAttribute.wikiid.name(), wikiid);
		while(categories.hasNext()) {
			Node node = categories.next();
			if(((String)node.getProperty(NodeAttribute.lang.name())).equalsIgnoreCase(language))
				return node.getId();
		}
		return -1L;
	}

	/**
	 * Returns the Neo4j node corresponding to the Wikipedia page with specified title, language and namespace.
	 * @param title The title of a Wikipedia page.
	 * @param language The code of the language of a Wikipedia edition.
	 * @param namespace A wikipedia namespace.
	 * @return The identifier in Neo4j of the node that corresponds to the Wikipedia page with specified title, language and
	 * namespace, if any, {@code -1} otherwise.
	 */
	private long getNodeIdByTitle(String title, String language, Namespace namespace) {
		NodeLabel label = namespace.id() == Namespace.MAIN ? NodeLabel.Article : 
			namespace.id() == Namespace.CATEGORY ? NodeLabel.Category : null;
		if ( label == null )
			return -1L;
		ResourceIterator<Node> articles = graphDb.findNodes(label, NodeAttribute.title.name(), title);
		while(articles.hasNext()) {
			Node node = articles.next();
			if(((String)node.getProperty(NodeAttribute.lang.name())).equalsIgnoreCase(language))
				return node.getId();
		}
		return -1L;
	}


}

