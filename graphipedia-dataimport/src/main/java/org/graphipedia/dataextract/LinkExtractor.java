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
package org.graphipedia.dataextract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.graphipedia.GraphipediaSettings;
import org.graphipedia.progress.ProgressCounter;
import org.graphipedia.wikipedia.DisambiguationPages;
import org.graphipedia.wikipedia.InfoboxTemplates;
import org.graphipedia.wikipedia.Link;
import org.graphipedia.wikipedia.Namespace;
import org.graphipedia.wikipedia.Namespaces;
import org.graphipedia.wikipedia.parser.IntermediateXmlFileTags;
import org.graphipedia.wikipedia.parser.SimpleStaxParser;
import org.graphipedia.wikipedia.parser.WikiTextParser;
import org.graphipedia.wikipedia.parser.XmlFileTags;

/**
 * This class parses the XML file that contains a Wikipedia language editions and extracts all the 
 * links between the Wikipedia pages.
 * The pages that do not belong to the main namespace or to the category namespace are ignored. 
 *
 */
public class LinkExtractor extends SimpleStaxParser {

	/**
	 * The stream used to write the output file.
	 */
	private final XMLStreamWriter writer;

	/**
	 * The parser for the textual content of a Wikipedia page. 
	 */
	private WikiTextParser wikiTextParser;

	/** 
	 * A counter used to track the progress of this extractor.
	 */
	private ProgressCounter pageCounter;

	/**
	 * The title of the page that is being currently parsed from the input file.
	 */
	private String title;

	/**
	 * The text (the wiki code) of the page that is being currently parsed from the input file.
	 */
	private String text;

	/**
	 * The identifier of the page that is being currently parsed from the input file.
	 */
	private String id;

	/**
	 * The list of attribute values associated to a XML tag.
	 */
	private List<String> attributeValues;

	/**
	 * The disambiguation pages.
	 */
	private DisambiguationPages dp;

	/**
	 * The namespaces.
	 */
	private Namespaces ns;

	/**
	 * Creates a new {@code LinkExtractor}.
	 * @param writer The intermediate XML file created by this {@code LinkEntractor}.
	 * @param logger The logger used to record the progress of the extraction.
	 * @param settings The settings of the import.
	 * @param language The code of the language of the Wikipedia edition for which the links are 
	 * being extracted.
	 * @param dp The disambiguation pages.
	 * @param it The infobox templates.
	 * @param ns The namespaces.
	 */
	public LinkExtractor(XMLStreamWriter writer, Logger logger, GraphipediaSettings settings, 
			String language, DisambiguationPages dp, InfoboxTemplates it, Namespaces ns) {
		super(Arrays.asList(XmlFileTags.page.toString(), XmlFileTags.title.toString(), 
				XmlFileTags.text.toString(), XmlFileTags.id.toString()), 
				Arrays.asList(XmlFileTags.redirect.toString()));
		this.writer = writer;
		this.wikiTextParser = new WikiTextParser(ns, it, dp);
		this.title = null;
		this.text = null;
		this.id = null;
		this.attributeValues = new ArrayList<String>();
		this.pageCounter = new ProgressCounter(logger);
		logger.info("Extracting the Wikipedia pages...");
		this.dp = dp;
		this.ns = ns;
	}

	/**
	 * Returns the number of pages that have already been parsed from the input file.
	 * 
	 * @return The number of pages that have already been parsed from the input file.
	 */
	public int getPageCount() {
		return pageCounter.getCount();
	}

	@Override
	protected boolean handleElement(String element, String value) throws XMLStreamException {
		if (XmlFileTags.page.toString().equals(element)) {
			Namespace pageNamespace = ns.wikipediaPageNamespace(title); 
			if ( pageNamespace.id() == Namespace.CATEGORY || 
					pageNamespace.id() == Namespace.MAIN ) {
				if ( attributeValues.size() > 0 ) // we have a redirect page
					writeRedirectPage(title, id, attributeValues.get(0));
				else
					writePage(title, id, text); /// regular Wikipedia page.
			}
			title = null;
			text = null;
			id = null;
			attributeValues = new ArrayList<String>();
		} else if (XmlFileTags.title.toString().equals(element)) {
			title = value;
		} else if (XmlFileTags.text.toString().equals(element)) {
			text = value;
		} else if (XmlFileTags.id.toString().equals(element)) {
			if (id == null) // there are multiple ids that are specified in the input file, the first is the one associated with the page, the others with the revisions...
				id = value;
		}
		return true;
	}

	@Override
	protected boolean handleElement(String element, String value,
			List<String> attributeValues) throws XMLStreamException {
		if ( XmlFileTags.redirect.toString().equals(element) ) 
			this.attributeValues = attributeValues;
		return true;
	}

	/**
	 * Writes the information extracted from a Wikipedia redirect page to the output file.
	 * 
	 * @param title The title of the redirect page.
	 * @param id The identifier of the redirect page.
	 * @param targetRedirect The title of the Wikipedia page that is the target of the redirect.
	 * @throws XMLStreamException if something goes wrong while writing the information to the output file.
	 */
	private void writeRedirectPage(String title, String id, String targetRedirect) throws XMLStreamException {
		writer.writeStartElement(IntermediateXmlFileTags.page.toString());

		writer.writeStartElement(IntermediateXmlFileTags.title.toString());
		writer.writeCharacters(title);
		writer.writeEndElement();

		writer.writeStartElement(IntermediateXmlFileTags.id.toString());
		writer.writeCharacters(id);
		writer.writeEndElement();

		writer.writeStartElement(IntermediateXmlFileTags.redirect.toString());
		writer.writeCharacters("t");
		writer.writeEndElement();

		writer.writeStartElement(IntermediateXmlFileTags.namespace.toString());
		writer.writeCharacters(Integer.toString(ns.wikipediaPageNamespace(title).id()));
		writer.writeEndElement();

		writer.writeStartElement(IntermediateXmlFileTags.regularlink.toString());
		writer.writeStartElement(IntermediateXmlFileTags.linkTitle.toString()); // title of target redirect page
		writer.writeCharacters(targetRedirect);
		writer.writeEndElement();
		writer.writeEndElement();

		writer.writeEndElement(); // end page

		pageCounter.increment("Parsing pages");
	}


	/**
	 * Writes the information extracted from a Wikipedia page to the output file.
	 * 
	 * @param title The title of the page.
	 * @param id The identifier of the page.
	 * @param text The text of the page.
	 * @throws XMLStreamException if something goes wrong while writing the information to the output file.
	 */
	private void writePage(String title, String id, String text) throws XMLStreamException {
		writer.writeStartElement(IntermediateXmlFileTags.page.toString());

		writer.writeStartElement(IntermediateXmlFileTags.title.toString());
		writer.writeCharacters(title);
		writer.writeEndElement();

		writer.writeStartElement(IntermediateXmlFileTags.id.toString());
		writer.writeCharacters(id);
		writer.writeEndElement();

		if ( this.dp.isDisambiguationPage(title) ) {
			writer.writeStartElement(IntermediateXmlFileTags.disambig.toString());
			writer.writeCharacters("t");
			writer.writeEndElement();
		}

		writer.writeStartElement(IntermediateXmlFileTags.namespace.toString());
		writer.writeCharacters(Integer.toString(ns.wikipediaPageNamespace(title).id()));
		writer.writeEndElement();

		Set<Link> links = wikiTextParser.parse(title, text);
		for (Link link : links )  {
			if ( link.isRegularLink() )
				writer.writeStartElement(IntermediateXmlFileTags.regularlink.toString()); // begin link
			else
				writer.writeStartElement(IntermediateXmlFileTags.dislink.toString()); // begin link

			writer.writeStartElement(IntermediateXmlFileTags.linkTitle.toString()); // title of the target page of the link
			writer.writeCharacters(link.targetTitle());
			writer.writeEndElement();

			for ( String anchor : link.anchors() ) { // anchors
				writer.writeStartElement(IntermediateXmlFileTags.anchor.toString()); 
				writer.writeCharacters(anchor);
				writer.writeEndElement();
			}

			writer.writeStartElement(IntermediateXmlFileTags.rank.toString()); // rank of the link
			writer.writeCharacters(link.rank()+"");
			writer.writeEndElement();

			writer.writeStartElement(IntermediateXmlFileTags.offset.toString()); // offset of the link
			writer.writeCharacters(link.offset()+"");
			writer.writeEndElement();

			if (link.intro()) { // whether the link occurs in the intro
				writer.writeStartElement(IntermediateXmlFileTags.intro.toString()); 
				writer.writeCharacters("t");
				writer.writeEndElement();
			}

			if (link.infobox()) { // whether the link occurs in the infobox
				writer.writeStartElement(IntermediateXmlFileTags.infobox.toString()); 
				writer.writeCharacters("t");
				writer.writeEndElement();
			}

			writer.writeStartElement(IntermediateXmlFileTags.occ.toString()); // occurrences of the link
			writer.writeCharacters(link.occurrences()+"");
			writer.writeEndElement();

			writer.writeEndElement(); // end link
		}
		writer.writeEndElement(); // end page
		pageCounter.increment("Parsing pages");
	}
}
