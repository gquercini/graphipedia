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

import org.neo4j.graphdb.Label;

/**
 * Labels of the nodes in the Neo4j database.
 *
 */
public enum NodeLabel implements Label {
	
	/**
	 * Label associated to a node corresponding to a Wikipedia article 
	 * (page in the main namespace).
	 */
	Article,
	
	/**
	 * Label associated to a node corresponding to a Wikipedia redirect page. 
	 */
	Redirect,
	
	/**
	 * Label associated to a node corresponding to a Wikipedia disambiguation page.
	 */
	Disambig,
	
	/**
	 * Label associated to a node corresponding to a Wikipedia
	 * category.
	 */
	Category 
}
