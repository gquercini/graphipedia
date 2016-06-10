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
 * A Wikipedia page.
 *
 */
public class Page {
	
	/**
	 * The identifier of the node in the Neo4j database that corresponds to this page.
	 */
	private long neo4jId;
	
	/**
	 * Whether this page is a redirect.
	 */
	private boolean redirect;
	
	/**
	 * The namespace of this page. One of the constants
	 * in class {@link Namespace}.
	 */
	private int namespace;
	
	/**
	 * Creates a new {@code Page}.
	 * @param neo4jId The identifier of the node in the Neo4j database that corresponds to the page.
	 * @param redirect Whether the page is a redirect.
	 * @param namespace The namespace of the page (one of the constants defined in class {@link Namespace}).
	 */
	public Page(long neo4jId, boolean redirect, int namespace) {
		this.neo4jId = neo4jId;
		this.redirect = redirect;
		this.namespace = namespace;
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
	 * Returns whether this page is a redirect.
	 * @return {@code true} if this page is a redirect, {@code false} otherwise.
	 */
	public boolean redirect() {
		return redirect;
	}
	
	/**
	 * Sets whether this page is a redirect.
	 * @param redirect {@code true} if this page is a redirect, {@code false} otherwise.
	 */
	public void redirect(boolean redirect) {
		this.redirect = redirect;
	}
	
	/**
	 * Returns the namespace of this page.
	 * @return The namespace of this page.
	 */
	public int namespace() {
		return this.namespace;
	}

	/**
	 * Sets the namespace of this page.
	 * @param namespace The namespace of this page.
	 */
	public void namespace(int namespace) {
		this.namespace = namespace;
	}
}
