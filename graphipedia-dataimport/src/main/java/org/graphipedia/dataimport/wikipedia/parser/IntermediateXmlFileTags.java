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
package org.graphipedia.dataimport.wikipedia.parser;

import org.graphipedia.dataimport.LinkExtractor;

/**
 * Enumerates all the tags used in the intermediate XML file that is generated
 * by the {@link LinkExtractor} to store the Wikipedia pages.
 *
 */
public enum IntermediateXmlFileTags {

	/**
	 * Root tag, the one that includes all the elements in the XML file.
	 */
	root("d"),

	/**
	 * Tag to specify the information about a page.
	 */
	page("p"), 

	/**
	 * Tag delimiting the text of a page.
	 */
	title("t"), 

	/**
	 * Tag to specify the identifier of a Wikipedia article.
	 */
	id("i"),
	
	/**
	 * Tag to indicate whether a page is a redirect.
	 */
	redirect("rdr"),
	
	/**
	 * Tag to indicate whether a page is a disambiguation.
	 */
	disambig("dis"),
	
	/**
	 * The namespace of a page (its identifier).
	 */
	namespace("nms"),

	/**
	 * Tag delimiting the regular link to a page.
	 */
	regularlink("rl"),
	
	/**
	 * tag delimiting the disambiguation link to a page
	 */
	dislink("dl"),

	/**
	 * Tag delimiting the title of a linked page.
	 */
	linkTitle("lt"),

	/**
	 * Subtag of link, indicating the anchor text of a link
	 */
	anchor("a"),

	/**
	 * Subtag of link, indicating the offset of the link wrt to the beginning of the page
	 * (number of characters from the beginning of the page before the link)
	 */
	offset("of"),

	/**
	 * Subtag of link, indicating the rank of the link, that is the number of links occurring before
	 * the link.
	 */
	rank("r"),

	/**
	 * Subtag of link, indicating whether the link occurs in the infobox
	 */
	infobox("ib"),

	/**
	 * Subtag of link, indicating whether the link occurs in the introduction
	 */
	intro("in"),

	/**
	 * Subtag link, indicating the number of occurrences of a link.
	 */
	occ("oc");


	/**
	 * The text associated to a tag.
	 */
	private String text;

	/**
	 * Constructor.
	 * @param text The text associated to a tag. 
	 */
	private IntermediateXmlFileTags(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return this.text;
	}


}
