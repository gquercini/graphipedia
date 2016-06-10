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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.graphipedia.dataimport.wikipedia.Link;
import org.graphipedia.dataimport.wikipedia.Namespace;
import org.graphipedia.dataimport.wikipedia.parser.IntermediateXmlFileTags;
import org.graphipedia.dataimport.wikipedia.parser.WikiTextParser;
import org.graphipedia.dataimport.wikipedia.parser.XmlFileTags;

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
	 * The file where the names of the namespaces are saved so that they can be retrieved later
	 * (to import cross-language links, geographic coordinates)...
	 */
	private File namespaceFile;

	/**
	 * Creates a new {@code LinkExtractor}.
	 * @param writer The intermediate XML file created by this {@code LinkEntractor}.
	 * @param logger The logger used to record the progress of the extraction.
	 * @param settings The settings of the import.
	 * @param language The code of the language of the Wikipedia edition for which the links are 
	 * being extracted.
	 */
	public LinkExtractor(XMLStreamWriter writer, Logger logger, DataImportSettings settings, String language) {
		super(Arrays.asList(XmlFileTags.page.toString(), XmlFileTags.title.toString(), 
				XmlFileTags.text.toString(), XmlFileTags.id.toString()), 
				Arrays.asList(XmlFileTags.redirect.toString(), XmlFileTags.namespace.toString()));
		this.writer = writer;
		this.wikiTextParser = new WikiTextParser();
		this.title = null;
		this.text = null;
		this.id = null;
		this.attributeValues = new ArrayList<String>();
		this.pageCounter = new ProgressCounter(logger);
		this.namespaceFile = new File(settings.wikipediaEditionDirectory(language), Namespace.NAMESPACE_FILE);
		if (namespaceFile.exists())
			namespaceFile.delete();
		try {
			namespaceFile.createNewFile();
		} catch (IOException e) {
			logger.severe("Error while creating the file " + namespaceFile.getAbsolutePath());
			e.printStackTrace();
			System.exit(-1);
		}
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
	protected void handleElement(String element, String value) {
		if (XmlFileTags.page.toString().equals(element)) {
			if (wikiTextParser.isValidPage(title)) {
				try {
					if ( attributeValues.size() > 0 ) // we have a redirect page
						writeRedirectPage(title, id, attributeValues.get(0));
					else
						writePage(title, id, text); /// regular Wikipedia page.
				} catch (XMLStreamException streamException) {
					throw new RuntimeException(streamException);
				}
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
	}
	
	@Override
	protected void handleElement(String element, String value,
			List<String> attributeValues) {
		// Parsing a namespace name and local name
		if ( XmlFileTags.namespace.toString().equals(element) ) {
			int namespaceId = Integer.parseInt(attributeValues.get(0));
			Namespace namespace = new Namespace(namespaceId, value);
			if ( namespace.isMain() || namespace.isCategory() ) {
				wikiTextParser.addNamespace(namespace);
				try {
					namespace.writeNamespaceToFile(namespaceFile);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		else
		if ( XmlFileTags.redirect.toString().equals(element) ) 
			this.attributeValues = attributeValues;
		
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
		writer.writeCharacters(Integer.toString(wikiTextParser.wikipediaPageNamespace(title)));
		writer.writeEndElement();
		
		writer.writeStartElement(IntermediateXmlFileTags.link.toString());
		writer.writeStartElement(IntermediateXmlFileTags.linkTitle.toString()); // title of target redirect page
		writer.writeCharacters(targetRedirect);
		writer.writeEndElement();
		writer.writeEndElement();
		
		writer.writeEndElement(); // end page

		pageCounter.increment();
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
		
		writer.writeStartElement(IntermediateXmlFileTags.namespace.toString());
		writer.writeCharacters(Integer.toString(wikiTextParser.wikipediaPageNamespace(title)));
		writer.writeEndElement();

		Set<Link> links = wikiTextParser.parse(title, text);
		for (Link link : links )  {
			writer.writeStartElement(IntermediateXmlFileTags.link.toString()); // begin link

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
		pageCounter.increment();
	}
}
