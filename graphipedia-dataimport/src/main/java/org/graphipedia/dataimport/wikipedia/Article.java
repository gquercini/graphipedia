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
package org.graphipedia.dataimport.wikipedia;

/**
 * A Wikipedia article, that is a page in the main namespace.
 *
 */
public class Article extends Page {
	
	/**
	 * The number of articles to which this article links.
	 */
	private int outdegree;
	
	/**
	 * The number of articles that link to this article.
	 */
	private int indegree;
	
	/**
	 * The geotags associated with this article, if any.
	 */
	private Geotags geotags;
	
	/**
	 * Creates a new article.
	 * @param title The title of the new article.
	 * @param lang The code of the language of the Wikipedia edition of the new article.
	 * @param wikiid The identifier of the new article in its Wikipedia edition. 
	 * @param neo4jId The identifier of the node in the Neo4j database that corresponds to the article.
	 * @param redirect Whether the article is a redirect.
	 */
	public Article(String title, String lang, String wikiid, long neo4jId, boolean redirect) {
		super(title, lang, wikiid, neo4jId, redirect);
		this.outdegree = this.indegree = 0;
		this.geotags = null;
	}
	
	/**
	 * Creates a new article.
	 *  @param title The title of the new article.
	 * @param lang The code of the language of the Wikipedia edition of the new article.
	 * @param wikiid The identifier of the new article in its Wikipedia edition. 
	 * @param neo4jId The identifier of the node in the Neo4j database that corresponds to the article.
	 * @param redirect Whether the article is a redirect.
	 * @param geotags The geotags associated with this article.
	 */
	public Article(String title, String lang, String wikiid, long neo4jId, boolean redirect, Geotags geotags) {
		this(title, lang, wikiid, neo4jId, redirect);
		this.geotags = geotags;
	}
	
	/**
	 * Returns the number of articles to which this article links.
	 * @return The number of articles to which this article links.
	 */
	public int outdegree() {
		return this.outdegree;
	}
	
	/**
	 * Returns the number of articles that link to this article.
	 * @return The number of articles that link to this article.
	 */
	public int indegree() {
		return this.indegree;
	}
	
	
	/**
	 * Increments the number of articles to which this article links.
	 */
	public void incrementOutdegree() {
		this.outdegree += 1;
	}
	
	/**
	 * Return the geotags associated with this article, if any.
	 * @return The geotags associated with this article, if any, {@code null} otherwise
	 */
	public Geotags Geotags() {
		return this.geotags;
	}
	
	/**
	 * Increments the number of articles that link to this article.
	 */
	public void incrementIndegree() {
		this.indegree += 1;
	}
	
	@Override
	public boolean isArticle() {
		return true;
	}

	@Override
	public boolean isCategory() {
		return false;
	}

}
