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

import java.util.logging.Logger;

import org.graphipedia.GraphipediaSettings;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.wikipedia.Namespaces;

/**
 * This thread extracts from the Wikipedia XML file the names of all the namespaces in a Wikipedia language edition.
 *
 */
public class ExtractNamespaces extends Thread {
	
	/**
	 * The namespaces in a Wikipedia language edition.
	 */
	private Namespaces namespaces;

	/**
	 * The logger of this class.
	 */
	private Logger logger; 

	/**
	 * The settings of the program.
	 */
	private GraphipediaSettings settings;

	/**
	 * The code of the language of the Wikipedia edition being currently imported (e.g., en).
	 */
	private String language;

	/**
	 * Creates a new thread to extract the namespaces of a Wikipedia language edition.
	 * @param settings The general settings of Graphipedia.
	 * @param language The code of the language of the Wikipedia edition being imported. 
	 * @param loggerMessageSuffix A suffix apppended to all the messages of the logger.
	 */
	public ExtractNamespaces(GraphipediaSettings settings, String language, String loggerMessageSuffix) {
		this.namespaces = new Namespaces();
		this.settings = settings;
		this.language = language;
		this.logger = LoggerFactory.createLogger("Namespace Extractor (" + loggerMessageSuffix + ")");
	}
	
	@Override
	public void run() {
		logger.info("Extract namespaces....");
		NamespaceExtractor extractor = new NamespaceExtractor();
		try {
			extractor.parse(settings.getWikipediaXmlFile(language).getAbsolutePath());
			this.namespaces = extractor.namespaces();
		} catch (Exception e) {
			logger.severe("Error while parsing the XML file");
			e.printStackTrace();
			System.exit(-1);
		} 
		logger.info("Done!");
	}
	
	/**
	 * Returns the namespaces extracted.
	 * @return The namespaces extracted.
	 */
	public Namespaces namespaces() {
		return this.namespaces;
	}

}
