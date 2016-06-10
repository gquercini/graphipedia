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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphipedia.dataimport.wikipedia.Infobox;
import org.graphipedia.dataimport.wikipedia.Introduction;
import org.graphipedia.dataimport.wikipedia.Link;
import org.graphipedia.dataimport.wikipedia.Namespace;

/**
 * Parser of the wiki code of a Wikipedia page.
 * Used to extract the links from a page and associated metadata.
 * The links to pages that do not belong to the set of allowed namespaces are ignored.
 * 
 */
public class WikiTextParser {
	
	/**
	 * Representation of a link in the wiki code of a Wikipedia page.
	 */
	private static final Pattern LINK_PATTERN = Pattern.compile("\\[\\[(.+?)\\]\\]");

	/** 
	 * The list of the allowed namespaces.
	 */
	private ArrayList<Namespace> namespaces;

	/**
	 * Creates a new {@code WikiTextParser}. 
	 */
	public WikiTextParser() {
		this.namespaces = new ArrayList<Namespace>();
	}
	
	/**
	 * Adds a new namespace to the list of allowed namespaces. 
	 * @param namespace A namespace.
	 */
	public void addNamespace(Namespace namespace) {
		this.namespaces.add(namespace);
	}
	
	/**
	 * Parses the text of a Wikipedia page and extracts the links and associated metadata.
	 * 
	 * @param title The title of a Wikipedia page.
	 * @param text The text of a Wikipedia page (the Wiki code).
	 * @return The information extracted from the given {@code text}.
	 */
	public Set<Link> parse(String title, String text) {
		Infobox infobox = (new InfoboxParser()).parse(text);
		text = stripTemplates(text);
		Introduction intro = (new IntroductionParser()).parse(text); 
		return parseLinks(title, text, infobox, intro);
	}
	
	/**
	 * Strips references to templates from the text. The references to templates are between double curly brackets. 
	 * @param text The text of a Wikipedia page.
	 * @return The given text of a Wikipedia page stripped of the references to the templates.
	 */
	private String stripTemplates(String text) {
		int startPos = -1;
		while( (startPos = text.indexOf("{{")) >=0 ) {
			int bracketCount = 2;
			int endPos = startPos + "{{".length();
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
			String toBeRemoved = "";
			if(endPos+1 >= text.length()) 
				toBeRemoved = text.substring(startPos);
			else
				toBeRemoved = text.substring(startPos, endPos+1);
			text = text.replace(toBeRemoved, "");
		}

		return text;
	}
	

	/**
	 * Parses the wiki text of a Wikipedia page and extracts all links.
	 * 
	 * @param pageTitle The title of the page from which the links are extracted. 
	 * @param text The text of a Wikipedia page.
	 * @param infobox The infobox of a Wikipedia page.
	 * @param intro THe introduction of a Wikipedia page.
	 * @return The set of links extracted from the given {@code text}. 
	 */
	private Set<Link> parseLinks(String pageTitle, String text, Infobox infobox, Introduction intro) {
		Map<String, Link> links = new HashMap<String, Link>();
		if (text != null) {
			Matcher matcher = LINK_PATTERN.matcher(text);
			int linkCounter = 0;
			while (matcher.find()) {
				// We found a link. We have to check that the linked page is an article or belongs to a namespace that we want.
				linkCounter += 1;
				String title = matcher.group(1);
				title = title.substring(0, 1).toUpperCase() + title.substring(1); // Make the first character uppercase
				if ( title.equals(pageTitle) )
					continue;
				if (isValidPage(title)) {
					
					String[] anchor = checkAnchorText(title);
					int offset = matcher.start();
					
					if ( links.containsKey(anchor[0]) ) {
						Link existingLink = links.get(anchor[0]);
						if ( anchor[1] != null )
							existingLink.addAnchor(anchor[1]);
						existingLink.addOccurrence();
						if (infobox != null && offset >= infobox.startIndex() && offset <= infobox.endIndex())
							existingLink.infobox(true);
						if (intro != null && offset >= intro.startIndex() && offset <= intro.endIndex())
							existingLink.intro(true);
						
					}
					else
					{
						Link newLink = new Link(pageTitle, anchor[0], offset, linkCounter, 
								infobox != null && offset >= infobox.startIndex() && offset <= infobox.endIndex(), intro != null && offset >= intro.startIndex() && offset <= intro.endIndex());
						if ( anchor[1] != null )
							newLink.addAnchor(anchor[1]);
						links.put(anchor[0], newLink);
					}
				}
			}
		}
		return new HashSet<Link>(links.values());
	}

	
	/**
	 * Checks whether the link is associated with an anchor text.
	 * @param link The link.
	 * @return An array, where the first element is the title of the target page of the link and the second element is the anchor text.
	 * If the link is not associated with an anchor text, then the second element is {@code null}. 
	 */
	private String[] checkAnchorText(String link) {
		if (link.contains("|")) 
			return new String[]{link.substring(0, link.lastIndexOf('|')), link.substring(link.lastIndexOf('|') + 1)};

		return new String[]{link, null};
	}

	
	/**
	 * Returns whether the Wikipedia page with the given title is valid, meaning that it belongs to an allowed namespace.
	 * @param title The title of a Wikipedia page.
	 * @return {@code true} if the Wikipedia page with the given {@code title} is valid, {@code false} otherwise
	 */
	public boolean isValidPage(String title) {
		return wikipediaPageNamespace(title) != Namespace.ANY;
	}
	
	/**
	 * Returns the namespace of a given Wikipedia page.
	 * 
	 * @param title The title of a Wikipedia page.
	 * @return The namespace (one of the constants of class {@link Namespace}) of the Wikipedia page with the given {@code title}.
	 */
	public int wikipediaPageNamespace(String title) {
		String name = Namespace.wikipediaPageNamespace(title);
		if ( name.length() == 0 ) // the page belongs to the main namespace (it is an article)
			return Namespace.MAIN;
		for( Namespace namespace : namespaces )  
			if ( namespace.name().equals(name) )
				return namespace.id();
		return Namespace.ANY;
	}

}
