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
package org.graphipedia.dataextract;

import java.util.Arrays;
import java.util.List;

import org.graphipedia.wikipedia.Namespace;
import org.graphipedia.wikipedia.Namespaces;
import org.graphipedia.wikipedia.parser.SimpleStaxParser;
import org.graphipedia.wikipedia.parser.XmlFileTags;

/**
 * This class extracts the namespaces from the XML file that contains a Wikipedia language edition.
 *
 */
public class NamespaceExtractor extends SimpleStaxParser {
	
	/**
	 * The namespaces of a Wikipedia language edition.
	 */
	private Namespaces namespaces;

	/**
	 * Constructor.
	 */
	public NamespaceExtractor() {
		super(Arrays.asList(XmlFileTags.namespaces.toString()), Arrays.asList(XmlFileTags.namespace.toString()));
		this.namespaces = new Namespaces();
		
	}

	@Override
	protected boolean handleElement(String element, String value) {
		if ( XmlFileTags.namespaces.toString().equals(element) ) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean handleElement(String element, String value, List<String> attributeValues) {
		if ( XmlFileTags.namespace.toString().equals(element) ) {
			int namespaceId = Integer.parseInt(attributeValues.get(0));
			Namespace namespace = new Namespace(namespaceId, value);
			this.namespaces.add(namespace);
		}
		return true;
	
	}
	
	/**
	 * Returns the namespaces extracted.
	 * @return The namespaces extracted.
	 */
	public Namespaces namespaces() {
		return namespaces;
	}

}
