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
package org.graphipedia.wikipedia.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.codehaus.stax2.XMLInputFactory2;

/**
 * A simple parser of a XML file.
 *
 */
public abstract class SimpleStaxParser {
	
	/**
	 * Object used to get the content of a XML file.
	 */
	private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory2.newInstance();

	/**
	 * Elements in the XML file that are extracted.
	 */
	private final List<String> interestingElements;

	/**
	 * Elements with attributes in the XML file that are extracted.
	 */
	private final List<String> interestingElementsWithAttributes;

	/**
	 * Creates a new parser.
	 * @param interestingElements The list of the elements in the XML file to parse that need to be considered.
	 * @param interestingElementsWithAttributes The list of the elements in the XML file to parse that need to be considered and have attributes.
	 */
	public SimpleStaxParser(List<String> interestingElements, List<String> interestingElementsWithAttributes) {
		this.interestingElements = interestingElements;
		this.interestingElementsWithAttributes = interestingElementsWithAttributes;
	}

	/**
	 * Handles an element of a XML file.
	 * @param element The element (XML tag).
	 * @param value The value of the element.
	 * @return {@code true} if parsing should be continued, {@code false otherwise}.
	 * @throws XMLStreamException when some XML-related error occurs.
	 */
	protected abstract boolean handleElement(String element, String value) throws XMLStreamException;

	/**
	 * Handles an element of a XML file with attributes.
	 * @param element The element (XML tag).
	 * @param value The value of the element.
	 * @param attributeValues The values of the attributes of the given {@code element}.
	 * @return {@code true} if parsing should be continued, {@code false otherwise}.
	 * @throws XMLStreamException when some XML-related error occurs.
	 */
	protected abstract boolean handleElement(String element, String value, List<String> attributeValues) throws XMLStreamException;

	/**
	 * Parses a XML file.
	 * @param fileName The name of the XML file to parse.
	 * @throws IOException when something goes wrong while reading the input file.
	 * @throws XMLStreamException when something goes wrong while parsing the XML file.
	 * @throws CompressorException when something goes wrong while opening the XML file (compressed file).
	 */
	public void parse(String fileName) throws IOException, XMLStreamException, CompressorException {
		
		FileInputStream fin = new FileInputStream(fileName);
	    BufferedInputStream bis = new BufferedInputStream(fin);
	    CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
	    parse(input);
	    fin.close();
	    bis.close();
	    input.close();
	}

	/**
	 * Parses a XML file. 
	 * @param inputStream The input XML stream.
	 * @throws IOException when something goes wrong while reading the input file.
	 * @throws XMLStreamException when something goes wrong while parsing the XML file.
	 */
	private void parse(InputStream inputStream) throws IOException, XMLStreamException {
		XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(inputStream, "UTF-8");
		try {
			parseElements(reader);
		} finally {
			reader.close();
			inputStream.close();
		}
	}

	/**
	 * Parses the elements in the XML file.
	 * @param reader The XML stream.
	 * @throws XMLStreamException when something goes wrong while parsing the XML file.
	 */
	private void parseElements(XMLStreamReader reader) throws XMLStreamException {
		LinkedList<String> elementStack = new LinkedList<String>();
		StringBuilder textBuffer = new StringBuilder();
		List<String> attributeValues = new ArrayList<String>();

		while (reader.hasNext()) {
			switch (reader.next()) {
			case XMLEvent.START_ELEMENT:
				String startElement = reader.getName().getLocalPart();
				elementStack.push(startElement);
				attributeValues = new ArrayList<String>();
				if ( isInterestingWithAttributes(startElement) ) {
					int noAttributes = reader.getAttributeCount();
					for ( int i = 0; i < noAttributes; i += 1 )
						attributeValues.add(reader.getAttributeValue(i));
				}
				textBuffer.setLength(0);
				break;
			case XMLEvent.END_ELEMENT:
				String element = elementStack.pop();
				if ( isInterestingWithAttributes(element) ) {
					if ( !handleElement(element, textBuffer.toString().trim(), attributeValues) )
						return;
				}
				else
					if (isInteresting(element)) {
						if ( !handleElement(element, textBuffer.toString().trim()) )
							return;
					}
				break;
			case XMLEvent.CHARACTERS:
				if (isInteresting(elementStack.peek())) {
					textBuffer.append(reader.getText());
				}
				break;
			}
		}
	}

	/**
	 * Returns whether a XML element has to be considered and has attributes.
	 * @param element A XML element.
	 * @return {@code true} if the given {@code element} has to be considered and has attributes, {@code false} otherwise.
	 */
	private boolean isInterestingWithAttributes(String element) {
		return this.interestingElementsWithAttributes.contains(element);
	}

	/**
	 * Returns whether a XML element has to be considered but has not attributes. 
	 * @param element A XML element.
	 * @return {@code true} if the given {@code element} has to be considered, {@code false} otherwise.
	 */
	private boolean isInteresting(String element) {
		return interestingElements.contains(element) || isInterestingWithAttributes(element);
	}


}
