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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.graphipedia.progress.ProgressCounter;
import org.graphipedia.wikipedia.Article;
import org.graphipedia.wikipedia.Category;
import org.graphipedia.wikipedia.Page;
import org.graphipedia.wikipedia.parser.IntermediateXmlFileTags;
import org.graphipedia.wikipedia.parser.SimpleStaxParser;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * Creates the links between the Wikipedia pages in the Neo4j database.
 *
 */
public class LinkCreator extends SimpleStaxParser {

	/**
	 * The connection to the Neo4j database where the links are imported.
	 */
	private final BatchInserter inserter;

	/**
	 * The in-memory index of the nodes (already imported in the Neo4j database). 
	 */
	private final Map<String, Page> inMemoryIndex;

	/**
	 * Tracks the number of links that are imported.
	 */
	private ProgressCounter linkCounter;

	/**
	 * The Wikipedia page that is the source node of a link .
	 */
	private Page sourceNode;

	/**
	 * The Wikipedia page that is the target node of a link.
	 */
	private Page targetNode;

	/**
	 * The anchor texts of a link. 
	 */
	private List<String> anchors;

	/**
	 * The offset of a link.
	 */
	private int offset;

	/**
	 * The rank of a link.
	 */
	private int rank;

	/**
	 * Whether a link occurs in the infobox of a Wikipedia page.
	 */
	private boolean infobox;

	/**
	 * Whether a link occurs in the introduction of a Wikipedia page.
	 */
	private boolean intro;

	/**
	 * The number of occurrences of a link in a Wikipedia page.
	 */
	private int occurrences;

	/**
	 * Creates a new {@code LinkCreator}.
	 * @param inserter The connection to the Neo4j database.
	 * @param inMemoryIndex The in-memory index of the nodes that have already been imported to the Neo4j database.
	 * @param logger The logger used to track the progress of the link creation.
	 */
	public LinkCreator(BatchInserter inserter,  Map<String, Page> inMemoryIndex, Logger logger) {
		super(Arrays.asList(IntermediateXmlFileTags.page.toString(), IntermediateXmlFileTags.title.toString(), 
				IntermediateXmlFileTags.regularlink.toString(), IntermediateXmlFileTags.dislink.toString(), 
				IntermediateXmlFileTags.linkTitle.toString(), 
				IntermediateXmlFileTags.anchor.toString(),
				IntermediateXmlFileTags.offset.toString(), IntermediateXmlFileTags.rank.toString(), IntermediateXmlFileTags.infobox.toString(),
				IntermediateXmlFileTags.intro.toString(), IntermediateXmlFileTags.occ.toString()),
				Arrays.asList(""));
		this.inserter = inserter;
		this.inMemoryIndex = inMemoryIndex;
		this.linkCounter = new ProgressCounter(logger);
		initializePage();
	}

	/**
	 * Auxiliary function to initialize all the fields of this class used to store information
	 * about the links of a page. 
	 */
	private void initializePage() {
		this.sourceNode = null;
		initializeLinkAttributes();
	}

	/**
	 * Auxiliary function to initialize all the fields of this class used to store information about 
	 * the attributes of a link.
	 */
	private void initializeLinkAttributes() {
		this.targetNode = null;
		this.anchors = new ArrayList<String>();
		this.offset = -1;
		this.rank = -1;
		this.infobox = false;
		this.intro = false;
		this.occurrences = 0;
	}

	/**
	 * Returns the number of links imported.
	 * @return The number of links imported to the Neo4j database.
	 */
	public int getLinkCount() {
		return linkCounter.getCount();
	}


	@Override
	protected boolean handleElement(String element, String value) {
		if (IntermediateXmlFileTags.page.toString().equals(element)) 
			initializePage();
		else
			if (IntermediateXmlFileTags.title.toString().equals(element))  {
				this.sourceNode = inMemoryIndex.get(value);
			}
			else if (IntermediateXmlFileTags.linkTitle.toString().equals(element)) {
				this.targetNode = inMemoryIndex.get(value);
			}
			else if (IntermediateXmlFileTags.anchor.toString().equals(element))
				this.anchors.add(value);
			else if (IntermediateXmlFileTags.offset.toString().equals(element))
				this.offset = Integer.parseInt(value);
			else if (IntermediateXmlFileTags.rank.toString().equals(element))
				this.rank = Integer.parseInt(value);
			else if (IntermediateXmlFileTags.infobox.toString().equals(element))
				this.infobox = true;
			else if (IntermediateXmlFileTags.intro.toString().equals(element))
				this.intro = true;
			else if (IntermediateXmlFileTags.occ.toString().equals(element))
				this.occurrences = Integer.parseInt(value);
			else if (IntermediateXmlFileTags.regularlink.toString().equals(element)) {
				createLink(this.sourceNode, this.targetNode, false);
				initializeLinkAttributes();
			}
			else if (IntermediateXmlFileTags.dislink.toString().equals(element)) {
				createLink(this.sourceNode, this.targetNode, true);
				initializeLinkAttributes();
			}
		return true;	
	}

	/**
	 * Creates a link between two nodes corresponding to two Wikipedia pages.
	 * @param sourceNode The Wikipedia page that is the source node of the link.
	 * @param targetNode The Wikipedia page that is the target node of the link.
	 * @param disambig Whether the new link is a disambiguation link.
	 */
	private void createLink(Page sourceNode, Page targetNode, boolean disambig) {	
		if (targetNode == null)
			return;
		Map<String, Object> attributes = MapUtil.map(LinkAttribute.offset.name(), this.offset, LinkAttribute.rank.name(), this.rank,
				LinkAttribute.occurrences.name(), this.occurrences);
		if ( this.anchors.size() > 0 )
			attributes.put(LinkAttribute.anchors.name(), this.anchors.toArray(new String[this.anchors.size()]));
		if ( this.infobox )
			attributes.put(LinkAttribute.infobox.name(), this.infobox);
		if ( this.intro )
			attributes.put(LinkAttribute.intro.name(), this.intro);
		if ( disambig )
			attributes.put(LinkAttribute.disambig.name(), true);
		if ( sourceNode.redirect() )
			inserter.createRelationship(sourceNode.neo4jId(), targetNode.neo4jId(), LinkType.redirectTo, attributes);
		else if ( sourceNode.isArticle() && targetNode.isCategory() ) {
			inserter.createRelationship(sourceNode.neo4jId(), targetNode.neo4jId(), LinkType.belongTo, attributes);
			sourceNode.incrementParents();
			((Category)targetNode).incrementSize();
		}
		else if ( sourceNode.isCategory() && targetNode.isCategory() ) {
			inserter.createRelationship(sourceNode.neo4jId(), targetNode.neo4jId(), LinkType.childOf, attributes);
			sourceNode.incrementParents();
			((Category)targetNode).incrementChildren();
		}
		else if ( sourceNode.isArticle() && targetNode.isArticle() ) {
			inserter.createRelationship(sourceNode.neo4jId(), targetNode.neo4jId(), LinkType.link, attributes);
			((Article)sourceNode).incrementOutdegree();
			((Article)targetNode).incrementIndegree();
		}
		else
			return;
		linkCounter.increment("Creating links");

	}

	@Override
	protected boolean handleElement(String element, String value, List<String> attributeValues) {
		return true;
	}
}
