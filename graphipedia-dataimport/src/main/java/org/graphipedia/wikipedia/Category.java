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
 * A category in Wikipedia.
 *
 */
public class Category extends Page {
	
	/**
	 * The number of articles that this category contains.
	 */
	private int size;
	
	/**
	 * The number of categories that are children of this category.
	 */
	private int children;

	/**
	 * Creates a new category.
	 * @param title The title of the new category.
	 * @param lang The code of the language of the Wikipedia edition of the new category.
	 * @param wikiid The identifier of the new category in its Wikipedia edition.
	 * @param neo4jId The identifier of the node in the Neo4j database that corresponds to the category.
	 * @param redirect Whether the category is a redirect.
	 */
	public Category(String title, String lang, String wikiid, long neo4jId, boolean redirect) {
		super(title, lang, wikiid, neo4jId, redirect);
		this.size = this.children = 0; 
	}
	
	/**
	 * Returns the number of articles that this category contains.
	 * @return The number of articles that this category contains.
	 */
	public int size() {
		return this.size;
	}
	
	
	/**
	 * Returns the number of categories that are children of this category.
	 * @return The number of categories that are children of this category.
	 */
	public int children() {
		return this.children;
	}
	
	/**
	 * Increments the number of articles that this category contains.
	 */
	public void incrementSize() {
		this.size += 1;
	}
	
	/**
	 * Increments the number of children that this category contains.
	 */
	public void incrementChildren() {
		this.children += 1;
	}
	
	@Override
	public boolean isArticle() {
		return false;
	}

	@Override
	public boolean isCategory() {
		return true;
	}
}
