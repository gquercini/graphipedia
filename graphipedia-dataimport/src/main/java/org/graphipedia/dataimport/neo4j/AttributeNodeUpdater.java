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
package org.graphipedia.dataimport.neo4j;

import java.util.Map;
import java.util.logging.Logger;

import org.graphipedia.dataimport.ProgressCounter;
import org.graphipedia.dataimport.wikipedia.Article;
import org.graphipedia.dataimport.wikipedia.Category;
import org.graphipedia.dataimport.wikipedia.Geotags;
import org.graphipedia.dataimport.wikipedia.Page;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * This class sets the values of the attributes of the nodes in the Neo4j database. 
 *
 */
public class AttributeNodeUpdater {
	
	/**
	 * The Neo4j object that is used to quickly insert nodes and links to a Neo4j database.
	 */
	private final BatchInserter inserter;

	/**
	 * An index of the nodes. 
	 */
	private final Map<String, Page> inMemoryIndex;

	/**
	 * Counts the number of Wikipedia nodes updated.
	 */
	private final ProgressCounter nodeCounter;
	
	/**
	 * Creates a new {@code NodeUpdater}.
	 * @param inserter The connection to Neo4j database.
	 * @param inMemoryIndex The index of the nodes.
	 * @param logger he logger used to record the progress of the update.
	 */
	public AttributeNodeUpdater(BatchInserter inserter, Map<String, Page> inMemoryIndex, Logger logger) {
		this.inserter = inserter;
		this.inMemoryIndex = inMemoryIndex;
		this.nodeCounter = new ProgressCounter(logger);
	}
	
	/**
	 * Updates the values of attributes of all nodes.
	 */
	public void update() {
		for ( Page page : inMemoryIndex.values() ) {
			long nodeId = page.neo4jId();
			Map<String, Object> attributes = null; 
			if ( page.isArticle() ) {
				Article article = (Article)page;
				attributes = MapUtil.map(NodeAttribute.title.name(), article.title(),
						NodeAttribute.lang.name(), article.lang(),
						NodeAttribute.wikiid.name(), article.wikiid(),
						NodeAttribute.outdegree.name(), article.outdegree(), 
						NodeAttribute.indegree.name(), article.indegree(), 
						NodeAttribute.parents.name(), article.parents());
				Geotags geotags = article.Geotags(); 
				if (geotags != null) {
					String globe = geotags.globe();
					double latitude = geotags.latitude();
					double longitude = geotags.longitude();
					String type = geotags.type();
					if ( globe != null )
						attributes.put(NodeAttribute.globe.name(), globe);
					attributes.put(NodeAttribute.latitude.name(), latitude);
					attributes.put(NodeAttribute.longitude.name(), longitude);
					if ( type != null )
						attributes.put(NodeAttribute.type.name(), type);
				}
			}
			else if ( page.isCategory() ) {
				Category category = (Category)page;
				attributes = MapUtil.map(NodeAttribute.title.name(), category.title(),
						NodeAttribute.lang.name(), category.lang(),
						NodeAttribute.wikiid.name(), category.wikiid(),
						NodeAttribute.size.name(), category.size(), 
						NodeAttribute.children.name(), category.children(), 
						NodeAttribute.parents.name(), category.parents()); 
			}
			else 
				continue;
			inserter.setNodeProperties(nodeId, attributes);
			this.nodeCounter.increment("Node attributes");
		}
	}
	
	/**
	 * Returns the number of the nodes updated.
	 * @return The number of the nodes updated.
	 */
	public int getNodeCount() {
		return this.nodeCounter.getCount();
	}

}
