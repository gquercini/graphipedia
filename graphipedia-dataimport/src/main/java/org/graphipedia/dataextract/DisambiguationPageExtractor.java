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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.graphipedia.GraphipediaSettings;
import org.graphipedia.progress.CheckPoint;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.wikipedia.DisambiguationPages;

/**
 * This thread obtains the list of the titles of all disambiguation pages 
 * in a Wikipedia language edition.
 *
 */
public class DisambiguationPageExtractor extends Thread {

	/**
	 * The logger of this class.
	 */
	private Logger logger;

	/**
	 * The list of all disambiguation pages extracted. 
	 */
	private DisambiguationPages disambiguationPages;

	/**
	 * The code of the language of the Wikipedia edition in which the 
	 * disambiguation pages are searched.
	 */
	private String language;

	/**
	 * The name of the category that contains all the disambiguation pages.
	 */
	private String rootCategory;

	/**
	 * The checkpoint information of Graphipedia.
	 */
	private CheckPoint checkpoint;

	/**
	 * Constructor.
	 * @param settings The settings of the import.
	 * @param language The code of the language of the Wikipedia edition in which the
	 * disambiguation pages are searched.
	 * @param rootCategory The name of the category that contains all the disambiguation pages.
	 * @param checkpoint The checkpoint information of Graphipedia.
	 */
	public DisambiguationPageExtractor(GraphipediaSettings settings, String language, String rootCategory, CheckPoint checkpoint) {
		this.disambiguationPages = new DisambiguationPages(settings.wikipediaEditionDirectory(language));
		this.language = language;
		this.rootCategory = rootCategory;
		this.logger = LoggerFactory.createLogger("Disambig Extractor (" + this.language.toUpperCase() + ")");
		this.checkpoint = checkpoint;
	}

	@Override
	public void run() {
		logger.info("Extracting disambiguation pages...");
		if ( this.rootCategory == null ) {
			logger.warning("No root category for disambiguation pages. Skipping...");
			return;
		}
		File dpFile = this.disambiguationPages.getDisambiguationPagesFile();
		if (checkpoint.isDisambiguationExtracted(this.language)) {
			try {
				this.disambiguationPages.load(dpFile);
			} catch (IOException e) {
				logger.severe("Error while reading file " + dpFile.getAbsolutePath());
				e.printStackTrace();
				System.exit(-1);
			}
		}
		else {
			try {
				this.disambiguationPages.load(language, rootCategory, dpFile);
			} catch (Exception e) {
				logger.severe("Error while obtaining the disambiguation pages");
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				checkpoint.addDisambiguationExtracted(language, true);
			} catch (IOException e) {
				logger.severe("Error while saving the checkpoint to file");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		logger.info("Done");
	}

	/**
	 * Returns the list of the disambiguation pages extracted.
	 * @return The list of the disambiguation pages extracted.
	 */
	public DisambiguationPages disambiguationPages() {
		return this.disambiguationPages;
	}

}
