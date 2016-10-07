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
package org.graphipedia.wikipedia.parser;

import java.util.Set;

import org.graphipedia.wikipedia.Link;

/**
 * The result of the {@code WikitextParser}.
 *
 */
public class WikiTextParserResult {
	
	/**
	 * The set of links in the page parsed.
	 */
	private Set<Link> links;
	
	/**
	 * The name of the infobox, if any, of the page parsed.
	 */
	private String infoboxName;
	
	/**
	 * Creates a new result.
	 * @param links The set of links in the page parsed.
	 */
	public WikiTextParserResult(Set<Link> links) {
		this(links, null);
	}
	
	/**
	 * Creates a new result.
	 * @param links The set of links in the page parsed.
	 * @param infoboxName The name of the infobox in the page parsed.
	 */
	public WikiTextParserResult(Set<Link> links, String infoboxName) {
		this.links = links;
		this.infoboxName = infoboxName;
	}
	
	/**
	 * Returns the set of links in  the page parsed.
	 * @return The set of links in  the page parsed.
	 */
	public Set<Link> links() {
		return this.links;
	}
	
	/**
	 * Returns the name of the infobox, if any, in the page parsed
	 * @return The name of the infobox, if any, in the page parsed, {@code null} otherwise.
	 */
	public String infoboxName() {
		return this.infoboxName;
	}
	

}
