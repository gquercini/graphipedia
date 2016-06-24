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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphipedia.dataimport.wikipedia.DisambiguationLink;
import org.graphipedia.dataimport.wikipedia.DisambiguationPages;
import org.graphipedia.dataimport.wikipedia.Infobox;
import org.graphipedia.dataimport.wikipedia.InfoboxTemplates;
import org.graphipedia.dataimport.wikipedia.Introduction;
import org.graphipedia.dataimport.wikipedia.Link;
import org.graphipedia.dataimport.wikipedia.Namespace;
import org.graphipedia.dataimport.wikipedia.Namespaces;
import org.graphipedia.dataimport.wikipedia.RegularLink;

/**
 * Parser of the wiki code of a Wikipedia page.
 * Used to extract the links from a page and associated metadata.
 * Only links to articles (pages in the main namespace) and categories are considered.
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
	private Namespaces ns;
	
	/**
	 * The infobox templates.
	 */
	private InfoboxTemplates it;
	
	/**
	 * The disambiguation pages.
	 */
	private DisambiguationPages dp;

	/**
	 * Creates a new {@code WikiTextParser}. 
	 * @param ns The list of namespaces.
	 * @param it The list of infobox templates.
	 * @param dp The list of disambiguation pages.
	 */
	public WikiTextParser(Namespaces ns, InfoboxTemplates it, DisambiguationPages dp) {
		this.ns = ns;
		this.it = it;
		this.dp = dp;
	}
	
	
	
	/**
	 * Parses the text of a Wikipedia page and extracts the links and associated metadata.
	 * 
	 * @param title The title of a Wikipedia page.
	 * @param text The text of a Wikipedia page (the Wiki code).
	 * @return The information extracted from the given {@code text}.
	 */
	public Set<Link> parse(String title, String text) {
		text = stripReferences(text);
		Infobox infobox = (new InfoboxParser(this.it)).parse(text);
		Introduction intro = (new IntroductionParser()).parse(text); 
		return parseLinks(title, text, infobox, intro);
	}
	
	/**
	 * Strips the references from the given text.
	 * References is text between <ref> ... </ref>.
	 * @param text The text a Wikipedia page.
	 * @return The {@code text} stripped of the references.
	 */
	private String stripReferences(String text) {
		int startPos = -1;
		while( (startPos = text.indexOf("<ref")) >= 0 ) {
			int ref = 1;
			int endPos = startPos + "<ref".length();
			while ( endPos < text.length() ) {
				switch(text.charAt(endPos)) {
				case '>' :
					if (text.charAt(endPos-1) == '/')
						ref--;
					endPos++;
					break;
				case '<' :
					if (endPos + 1 < text.length() && endPos + 10 < text.length() 
							&& text.substring(endPos + 1, endPos + 10).equals("ref name=")) {
						ref++;
						endPos += 10;
					}
					else if ( endPos + 1 < text.length() && endPos + 5 < text.length() && 
							text.substring(endPos + 1, endPos + 5).equals("ref>") ) {
						ref ++;
						endPos += 5;
					}
					else if ( endPos + 1 < text.length() && endPos + 6 < text.length() && 
							text.substring(endPos + 1, endPos + 6).equals("/ref>")  ) {
						ref--;
						endPos += 6;
					}
					else
						endPos += 1;
					break;
				default: endPos += 1;
				}
				if ( ref == 0 )
					break;
			} // end while
			String toBeRemoved = "";
			if(endPos+1 >= text.length()) 
				toBeRemoved = text.substring(startPos);
			else
				toBeRemoved = text.substring(startPos, endPos);
			text = text.replace(toBeRemoved, "");
		}
		return text;
	}
	
	/**
	 * Parses the wiki text of a Wikipedia page and extracts all links.
	 * 
	 * @param sourceTitle The title of the page from which the links are extracted. 
	 * @param text The text of a Wikipedia page.
	 * @param infobox The infobox of a Wikipedia page.
	 * @param intro THe introduction of a Wikipedia page.
	 * @return The set of links extracted from the given {@code text}. 
	 */
	private Set<Link> parseLinks(String sourceTitle, String text, Infobox infobox, Introduction intro) {
		Map<String, Link> links = new HashMap<String, Link>();
		if (text != null) {
			Matcher matcher = LINK_PATTERN.matcher(text);
			int linkCounter = 0;
			while (matcher.find()) {
				// We found a link. We have to check that the linked page is an article or belongs to a namespace that we want.
				linkCounter += 1;
				String targetTitle = matcher.group(1);
				if ( targetTitle.length() == 1 )
					targetTitle= targetTitle.toUpperCase();
				else if (targetTitle.length() > 1)
					targetTitle = targetTitle.substring(0, 1).toUpperCase() + targetTitle.substring(1); // Make the first character uppercase
				if ( targetTitle.equals(sourceTitle) )
					continue;
				String[] anchor = checkAnchorText(targetTitle);
				Namespace pageNamespace = ns.wikipediaPageNamespace(anchor[0]);
				if (pageNamespace.id() == Namespace.MAIN || 
						pageNamespace.id() == Namespace.CATEGORY) {
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
						Link newLink = null;
						if ( this.dp.isDisambiguationPage(sourceTitle) && 
								isDisambiguationLink(text, offset))
							newLink = new DisambiguationLink(sourceTitle, anchor[0], offset, linkCounter, 
									infobox != null && offset >= infobox.startIndex() && offset <= infobox.endIndex(), 
									intro != null && offset >= intro.startIndex() && offset <= intro.endIndex());
						else	
						newLink = new RegularLink(sourceTitle, anchor[0], offset, linkCounter, 
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
	 * Returns whether a link has to be considered a disambiguation link, that is one that leads from
	 * one disambiguation page to an article that explains a possible interpretation of the term 
	 * described by the disambiguation page.
	 * @param text The wiki code of a disambiguation page.
	 * @param offset The position in the text of the link.
	 * @return {@code true} whether the link, of which the {@code offset} is known, is a disambiguation link,
	 * {@code false} otherwise.
	 */
	private boolean isDisambiguationLink(String text, int offset) {
		int asteriskIndex = text.lastIndexOf("*", offset);
		if ( asteriskIndex < 0 )
			return false;
		String substr = text.substring(asteriskIndex + 1, offset);
		substr = substr.replace('\'', ' ');
		substr = substr.trim();
		return substr.length() == 0; 
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

}
