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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.codehaus.stax2.XMLOutputFactory2;
import org.graphipedia.CheckPoint;
import org.graphipedia.GraphipediaSettings;
import org.graphipedia.LoggerFactory;
import org.graphipedia.dataimport.wikipedia.DisambiguationPages;
import org.graphipedia.dataimport.wikipedia.InfoboxTemplates;
import org.graphipedia.dataimport.wikipedia.Namespaces;

/**
 * This thread parses the XML file containing a Wikipedia edition in a specific language and creates an 
 * intermediate XML file with all the links between Wikipedia pages. 
 *
 */
public class ExtractLinks extends Thread {

	/**
	 * The name of the temporary file that is created by this thread and used as input to the thread that imports the
	 * Wikipedia graph to Neo4j.
	 */
	public static final String TEMPORARY_LINK_FILE = "temporary-link-file.xml.bz2";

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
	 * The disambiguation pages.
	 */
	private DisambiguationPages dp;

	/**
	 * The infobox templates.
	 */
	private InfoboxTemplates it;

	/**
	 * The namespaces.
	 */
	private Namespaces ns;
	
	/**
	 * The checkpoint information of Graphipedia.
	 */
	private CheckPoint checkpoint;

	/**
	 * Creates a new thread to extract a specific Wikipedia language edition.
	 * @param settings The general settings of Graphipedia.
	 * @param language The code of the language of the Wikipedia edition being imported. 
	 * @param dp The disambiguation pages.
	 * @param it The infobox templates.
	 * @param ns The namespaces.
	 * @param checkpoint The checkpoint information of Graphipedia.
	 */
	public ExtractLinks(GraphipediaSettings settings, String language, 
			DisambiguationPages dp, InfoboxTemplates it, Namespaces ns, CheckPoint checkpoint) {
		this.settings = settings;
		this.language = language;
		this.logger = LoggerFactory.createLogger("Link Extractor (" + this.language.toUpperCase() + ")");
		this.dp = dp;
		this.it = it;
		this.ns = ns;
		this.checkpoint = checkpoint;
	}

	@Override
	public void run() {
		logger.info("Parsing pages and extracting links...");
		long startTime = System.currentTimeMillis();
		XMLOutputFactory outputFactory = XMLOutputFactory2.newInstance();
		File outputFile = new File(settings.wikipediaEditionDirectory(language), TEMPORARY_LINK_FILE); 
		if (checkpoint.isLinksExtracted(this.language)) {
			logger.info("Using pages and links from a previous computation");
			return;
		}

		try {
			FileOutputStream fout = new FileOutputStream(outputFile.getAbsolutePath());
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			CompressorOutputStream output = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, bos);
			XMLStreamWriter writer = outputFactory.createXMLStreamWriter(output, "UTF-8");
			writer.writeStartDocument();
			writer.writeStartElement("d");

			LinkExtractor linkExtractor = new LinkExtractor(writer, logger, settings, language, dp, it, ns);
			linkExtractor.parse(settings.getWikipediaXmlFile(language).getAbsolutePath());

			writer.writeEndElement();
			writer.writeEndDocument();
			output.close();
			fout.close();
			bos.close();
			writer.close();
			long elapsed = System.currentTimeMillis() - startTime;
			logger.info(String.format("%d pages parsed in " + ReadableTime.readableTime(elapsed) + "\n", linkExtractor.getPageCount()));
		}
		catch(Exception e) {
			logger.severe("Error while parsing the XML file ");
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			checkpoint.addLinksExtracted(this.language, true);
		} catch (IOException e) {
			logger.severe("Error while saving the checkpoint to file");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
