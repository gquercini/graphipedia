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
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.codehaus.stax2.XMLOutputFactory2;

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
	private DataImportSettings settings;

	/**
	 * The code of the language of the Wikipedia edition being currently imported (e.g., en).
	 */
	private String language;

	/**
	 * Creates a new thread to extract a specific Wikipedia language edition.
	 * @param settings The general settings of Graphipedia.
	 * @param language The code of the language of the Wikipedia edition being imported. 
	 */
	public ExtractLinks(DataImportSettings settings, String language) {
		this.settings = settings;
		this.language = language;
		this.logger = LoggerFactory.createLogger("Link Extractor (" + this.language.toUpperCase() + ")");
	}

	@Override
	public void run() {
		logger.info("Parsing pages and extracting links...");
		long startTime = System.currentTimeMillis();
		XMLOutputFactory outputFactory = XMLOutputFactory2.newInstance();
		File outputFile = new File(settings.wikipediaEditionDirectory(language), TEMPORARY_LINK_FILE); 

		try {
			FileOutputStream fout = new FileOutputStream(outputFile.getAbsolutePath());
		    BufferedOutputStream bos = new BufferedOutputStream(fout);
		    CompressorOutputStream output = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, bos);
		    XMLStreamWriter writer = outputFactory.createXMLStreamWriter(output, "UTF-8");
			writer.writeStartDocument();
			writer.writeStartElement("d");

			LinkExtractor linkExtractor = new LinkExtractor(writer, logger, settings, language);
			linkExtractor.parse(settings.getWikipediaXmlFile(language).getAbsolutePath());

			writer.writeEndElement();
			writer.writeEndDocument();
			output.close();
			fout.close();
			bos.close();
			writer.close();
			long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
			logger.info(String.format("%d pages parsed in %d seconds.\n", linkExtractor.getPageCount(), elapsedSeconds));
		}
		catch(Exception e) {
			logger.severe("Error while parsing the XML file ");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
