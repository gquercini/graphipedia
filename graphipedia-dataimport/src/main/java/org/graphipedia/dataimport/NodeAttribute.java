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

/**
 * Enumerates the attributes of a node in Neo4j.
 * Each node corresponds to a Wikipedia page.
 */
public enum NodeAttribute {
	
	/**
	 * The title of the page corresponding to a node.
	 */
	title,
	
	/**
	 * The language of the page corresponding to a node.
	 */
	lang,
	
	/**
	 * The identifier in Wikipedia of the page corresponding to a node.
	 */
	wikiid,
	
	/**
	 * The number of articles to which an article links.
	 */
	outdegree,
	
	/**
	 * The number of articles that are linked by an article.
	 */
	indegree,
	
	/**
	 * The number of categories that are parent of an article or a category.
	 */
	parents,
	
	/**
	 * The number of categories that are children of a category.
	 */
	children,
	
	/**
	 * The number of articles that a category contains.
	 */
	size,
	
	/**
	 * The globe of a spatial entity, if the article is about a spatial entity.
	 */
	globe,
	
	/**
	 * The latitude of a spatial entity, if the article is about a spatial entity.
	 */
	latitude,
	
	/**
	 * The longitude of a spatial entity, if the article is about a spatial entity.
	 */
	longitude,
	
	/**
	 * The type of a spatial entity, if the article is about a spatial entity.
	 */
	type
	

}
