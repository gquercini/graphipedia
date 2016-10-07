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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.graphipedia.progress.ProgressCounter;
import org.graphipedia.wikipedia.Article;
import org.graphipedia.wikipedia.Category;
import org.graphipedia.wikipedia.Geotags;
import org.graphipedia.wikipedia.Namespace;
import org.graphipedia.wikipedia.Page;
import org.graphipedia.wikipedia.parser.IntermediateXmlFileTags;
import org.graphipedia.wikipedia.parser.SimpleStaxParser;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * 
 * Creates a node in the Neo4j database corresponding to a Wikipedia page.
 *
 */
public class NodeCreator extends SimpleStaxParser {

	/**
	 * The Neo4j object that is used to quickly insert nodes and links to a Neo4j database.
	 */
	private final BatchInserter inserter;

	/**
	 * An index of the nodes. 
	 */
	private final Map<String, Page> inMemoryIndex;
	
	/**
	 * The geotags associated with pages that describe spatial entities.
	 */
	private final Map<String, Geotags> geotags;

	/**
	 * The code of the language of the Wikipedia edition being currently imported.
	 */
	private final String language;

	/**
	 * The title of the Wikipedia page being currently imported.
	 */
	private String title;

	/**
	 * The identifier in Wikipedia of the Wikipedia page being currently imported.
	 */
	private String wikiId;

	/**
	 * Whether the Wikipedia page being currently imported is a redirect.
	 */
	private boolean redirect;
	
	/**
	 * Whether the Wikipedia page being currently imported is a disambiguation.
	 */
	private boolean disambig;

	/**
	 * The namespace of the Wikipedia page being currently imported.
	 */
	private int namespace;
	
	/**
	 * The name of the infobox, if any, of the Wikipedia page being imported.
	 */
	private String infoboxName;

	/**
	 * Counts the number of Wikipedia pages imported to the Neo4j database.
	 */
	private ProgressCounter pageCounter;

	/**
	 * Creates a new {@code NodeCreator}
	 * @param inserter The Neo4j database.
	 * @param inMemoryIndex The index of the nodes. 
	 * @param language The code of the language of the Wikipedia edition being currently imported.
	 * @param logger The logger used to record the progress of the node creation.
	 * @param geotags The geotags associated with the pages that describe spatial entities.
	 */
	public NodeCreator(BatchInserter inserter, Map<String, Page> inMemoryIndex, String language, Logger logger, Map<String, Geotags> geotags) {
		super(Arrays.asList(IntermediateXmlFileTags.page.toString(), IntermediateXmlFileTags.title.toString(), 
				IntermediateXmlFileTags.id.toString(), IntermediateXmlFileTags.redirect.toString(), IntermediateXmlFileTags.disambig.toString(),  
				IntermediateXmlFileTags.namespace.toString()), Arrays.asList(""));
		this.inserter = inserter;
		this.language = language;
		this.inMemoryIndex = inMemoryIndex;
		this.geotags = geotags;
		
		this.pageCounter = new ProgressCounter(logger);

		this.title = null;
		this.wikiId = null;
		this.redirect = false;
		this.disambig = false;
		this.infoboxName = null;
		this.namespace = Namespace.ANY;
	}

	/**
	 * Returns the number of pages imported to the Neo4j database. 
	 * @return The number of pages imported to the Neo4j database.
	 */
	public int getPageCount() {
		return pageCounter.getCount();
	}

	@Override
	protected boolean handleElement(String element, String value) {
		if (IntermediateXmlFileTags.page.toString().equals(element)) {
			createNode(this.title, this.wikiId, this.redirect, this.namespace, this.infoboxName);
			this.title = null;
			this.wikiId = null;
			this.redirect = false;
			this.disambig = false;
			this.infoboxName = null;
			this.namespace = Namespace.ANY;
		} else if (IntermediateXmlFileTags.title.toString().equals(element)) 
			this.title = value;
		else if (IntermediateXmlFileTags.id.toString().equals(element)) 
			this.wikiId = value;
		else if (IntermediateXmlFileTags.namespace.toString().equals(element)) 
			this.namespace = Integer.parseInt(value);
		else if (IntermediateXmlFileTags.redirect.toString().equals(element)) 
			this.redirect = true;
		else if (IntermediateXmlFileTags.disambig.toString().equals(element))
			this.disambig = true;
		else if (IntermediateXmlFileTags.infoboxName.toString().equals(element))
			this.infoboxName = value;
		
		return true;
	}

	/**
	 * Creates a node in the Neo4j database, corresponding to a Wikipedia page.
	 * @param title The title of a Wikipedia page.
	 * @param wikiId The identifier in Wikipedia of a Wikipedia page.
	 * @param redirect Whether the Wikipedia page corresponding to the node being created is a redirect.
	 * @param namespace The namespace of the Wikipedia page.
	 * @param infoboxName The name of the infobox of the Wikipedia page.
	 */
	private void createNode(String title, String wikiId, boolean redirect, int namespace, String infoboxName) {
		long nodeId = -1;
		Page newPage = null;
		if ( namespace == Namespace.MAIN ) {
			if ( redirect )
				nodeId = inserter.createNode(null, NodeLabel.Article, NodeLabel.Redirect);
			else if ( disambig )
				nodeId = inserter.createNode(null, NodeLabel.Article, NodeLabel.Disambig);
			else
				nodeId = inserter.createNode(null, NodeLabel.Article);
			if ( geotags.containsKey(wikiId) )
				newPage = new Article(title, language, wikiId, nodeId, redirect, geotags.get(wikiId), infoboxName);
			else
				newPage = new Article(title, language, wikiId, nodeId, redirect, infoboxName);
		}
		else
			if ( namespace == Namespace.CATEGORY ) {
				if ( redirect )
					nodeId = inserter.createNode(null, NodeLabel.Category, NodeLabel.Redirect);
				else if (disambig)
					nodeId = inserter.createNode(null, NodeLabel.Category, NodeLabel.Disambig);
				else
					nodeId = inserter.createNode(null, NodeLabel.Category);
				newPage = new Category(title, language, wikiId, nodeId, redirect);
			}
			else
				return;
		inMemoryIndex.put(title, newPage);
		pageCounter.increment("Creating nodes");
	}


	@Override
	protected boolean handleElement(String element, String value, List<String> attributeValues) {
		return true;
	}

}
