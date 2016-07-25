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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.graphipedia.GraphipediaSettings;
import org.graphipedia.progress.CheckPoint;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.wikipedia.Namespace;
import org.graphipedia.wikipedia.Namespaces;

/**
 * This thread extracts from the Wikipedia XML file the names of all the namespaces in a Wikipedia language edition.
 *
 */
public class ExtractNamespaces extends Thread {

	/**
	 * Name of the file where the namespaces are saved after being extracted from the Wikipedia XML file.
	 */
	public static final String NAMESPACE_FILE = "namespaces.csv";

	/**
	 * The file where the namespaces are stored, if already extracted in a previous computation.
	 */
	private File namespaceFile;

	/**
	 * The checkpoint of Graphipedia.
	 */
	private CheckPoint checkPoint;

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
	 * @param namespaceFile The file that contains the namespaces, if already extracted in a previous computation
	 * @param checkPoint The checkpoint of Graphipedia.
	 */
	public ExtractNamespaces(GraphipediaSettings settings, String language, String loggerMessageSuffix, 
			File namespaceFile, CheckPoint checkPoint) {
		this.namespaceFile = namespaceFile; 
		this.checkPoint = checkPoint;
		this.namespaces = new Namespaces();
		this.settings = settings;
		this.language = language;
		this.logger = LoggerFactory.createLogger("Namespace Extractor (" + loggerMessageSuffix + ")");
	}

	@Override
	public void run() {		
		logger.info("Extract namespaces....");
		// Loading the namespaces from a previous computation.
		if ( this.checkPoint.isNamespacesExtracted(language) ) {
			try {
			this.namespaces = new Namespaces();
			BufferedReader bd = new BufferedReader(new FileReader(namespaceFile));
			String line;
			while( (line = bd.readLine()) != null ) {
				String[] values = line.split("\t");
				if ( values.length == 1 )
					namespaces.add(new Namespace(Integer.parseInt(values[0]), ""));
				else
					namespaces.add(new Namespace(Integer.parseInt(values[0]), values[1]));
			}
			bd.close();
			}
			catch (IOException e) {
				logger.severe("Error while retrieving the namespaces");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		else {
			NamespaceExtractor extractor = new NamespaceExtractor();
			try {
				extractor.parse(settings.getWikipediaXmlFile(language).getAbsolutePath());
				this.namespaces = extractor.namespaces();
				BufferedWriter bw = new BufferedWriter(new FileWriter(this.namespaceFile));
				for ( Namespace namespace : this.namespaces ) 
					bw.write(namespace.id() + "\t" + namespace.title() + "\n");
				bw.close();
			} catch (Exception e) {
				logger.severe("Error while parsing the XML file");
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				this.checkPoint.addNamespacesExtracted(language, true);
			} catch (IOException e) {
				logger.info("Error while saving the checkpoint file");
				e.printStackTrace();
				System.exit(-1);
			}
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
