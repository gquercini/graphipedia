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

import org.graphipedia.wikipedia.Introduction;

/**
 * A parser of the introduction of a Wikipedia page.
 *
 */
public class IntroductionParser {

	/**
	 * The way the header of a section is specified in the wiki text of a Wikipedia page.
	 */
	private static String SECTION_HEAD = "==";

	/**
	 * Parses the wiki text of a Wikipedia page and returns its introduction.
	 * @param text The text of a Wikipedia page.
	 * @return The introduction of the Wikipedia page.
	 */
	public Introduction parse(String text) {
		int startIntro = 0;
		int endIntro = text.indexOf(SECTION_HEAD); // the end of the introduction is the beginning of the second section.
		if (endIntro <= 0) // The whole article is just one section or empty. This happens mostly for stubs.
			return null;
		return new Introduction(text.substring(startIntro, endIntro), startIntro, endIntro);
	}


}
