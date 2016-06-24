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
package org.graphipedia.wikipedia;

/**
 * A Wikipedia page.
 *
 */
public abstract class Page {
	
	/**
	 * The title of this page.
	 */
	private String title;
	
	/**
	 * The code of the language of the Wikipedia edition containing this page.
	 */
	private String lang;
	
	/**
	 * The identfier of this page in its Wikipedia edition.
	 */
	private String wikiid;
	
	/**
	 * The identifier of the node in the Neo4j database that corresponds to this page.
	 */
	private long neo4jId;
	
	/**
	 * The number of categories that are parents of this page.
	 */
	private int parents;
	
	/**
	 * Whether this article is a redirect.
	 */
	private boolean redirect;
	
	
	/**
	 * Creates a new {@code Page}.
	 * @param title The title of the new page.
	 * @param lang The code of the language of the Wikipedia edition of the new page.
	 * @param wikiid The identifier of the new page in its Wikipedia edition.
	 * @param neo4jId The identifier of the node in the Neo4j database that corresponds to the page.
	 * @param redirect Whether this page is a redirect.
	 */
	protected Page(String title, String lang, String wikiid, long neo4jId, boolean redirect) {
		this.title = title;
		this.lang = lang;
		this.wikiid = wikiid;
		this.neo4jId = neo4jId;
		this.redirect = redirect;
		this.parents = 0;
	}
	
	
	/**
	 * Returns the title of this page.
	 * @return The title of this page.
	 */
	public String title() {
		return title;
	}
	
	/**
	 * Returns the code of the language of the Wikipedia edition of this page.
	 * @return The code of the language of the Wikipedia edition of this page.
	 */
	public String lang() {
		return lang;
	}
	
	/**
	 * Returns the identifier of this page in the Wikipedia edition of this page.
	 * @return The identifier of this page in the Wikipedia edition of this page.
	 */
	public String wikiid() {
		return this.wikiid;
	}
	
	/**
	 * Returns the number of categories that are parents of this page.
	 * @return The number of categories that are parents of this page.
	 */
	public int parents() {
		return this.parents;
	}
	
	/**
	 * Increments the number of parents of this page.
	 */
	public void incrementParents() {
		this.parents += 1;
	}

	/**
	 * Sets the identifier in the Neo4j database of the node corresponding to this page.
	 *  
	 * @param neo4jId The identifier in the Neo4j database of the node corresponding to this page.
	 */
	public void neo4jId(long neo4jId) {
		this.neo4jId = neo4jId;
	}
	
	/**
	 * Returns the identifier in the Neo4j database of the node corresponding to this page.
	 * @return The identifier in the Neo4j database of the node corresponding to this page.
	 */
	public long neo4jId() {
		return this.neo4jId;
	}
	
	/**
	 * Sets whether this page is a redirect.
	 * @param redirect {@code true} it this page is a redirect, {@code false} otherwise.
	 */
	public void redirect(boolean redirect) {
		this.redirect = redirect;
	}
	
	/**
	 * Returns whether this page is a redirect.
	 * @return {@code true} if this page is a redirect, {@code false} otherwise.
	 */
	public boolean redirect() {
		return this.redirect;
	}
	
	
	/**
	 * Returns whether this page is an article (in the main namespace).
	 * @return {@code true} if this page is an article, {@code false} otherwise.
	 */
	public abstract boolean isArticle();
	
	/**
	 * Returns whether this page is a category.
	 * @return {@code true} if this page is a category, {@code false} otherwise.
	 */
	public abstract boolean isCategory();
}
