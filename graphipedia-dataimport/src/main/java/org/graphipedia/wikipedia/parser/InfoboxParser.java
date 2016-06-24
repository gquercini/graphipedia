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


import org.graphipedia.wikipedia.Infobox;
import org.graphipedia.wikipedia.InfoboxTemplates;

/**
 * A parser for the infobox of a Wikipedia page.
 *
 */
public class InfoboxParser {

	/**
	 * The templates used to include infoboxes in a Wikipedia page.
	 */
	private InfoboxTemplates it;

	/**
	 * Constructor.
	 * @param it The templates used to include infoboxes in a Wikipedia page.
	 */
	public InfoboxParser(InfoboxTemplates it) {
		this.it = it;
	}

	/**
	 * Parses the given text of a Wikipedia page and returns its infobox, if any.
	 * 
	 * This code is taken from the class {@code WikiTextParser} of the project <a href="https://code.google.com/p/wikixmlj/">wikixmlj</a>
	 * 
	 * @param text The wiki code of a Wikipedia page.
	 * @return The infobox of the Wikipedia page, if any, or {@code null} if no infobox exists.
	 * 
	 */
	public Infobox parse(String text) {
		int startPos = -1;
		int endPos = -1;
		while( (startPos = text.indexOf("{{", endPos + 1)) >=0 ) {
			int bracketCount = 2;
			endPos = startPos + "{{".length();
			for(; endPos < text.length(); endPos++) {
				switch(text.charAt(endPos)) {
				case '}':
					bracketCount--;
					break;
				case '{':
					bracketCount++;
					break;
				default:
				}
				if(bracketCount == 0) break;
			}
			if(endPos >= text.length())
				break;
			String template = text.substring(startPos, endPos+1);
			int barIndex = template.indexOf("|");
			if ( barIndex >= 0) {
				String templateName = template.substring(2, barIndex).trim();
				if ( !it.isInfoboxTemplate(templateName) ) 
					continue;
				return new Infobox(template, startPos, endPos + 1);
			}
		}
		return null;
	}
}
