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
package org.graphipedia.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.graphipedia.CheckPoint;
import org.graphipedia.GraphipediaSettings;
import org.graphipedia.dataimport.ReadableTime;

/**
 * A Wikipedia language edition
 *
 */
public class WikipediaEdition {
	/**
	 * The base URL of the web page that contains the dump files of a Wikipedia language edition.
	 */
	public final static String DUMP_BASE_URL = "https://dumps.wikimedia.org/";

	/**
	 * The language of this Wikipedia edition (English name).
	 */
	private String language;

	/**
	 * The language of this Wikipedia edition (Locale name).
	 */
	private String languageLocal;

	/**
	 * The code of the language of this Wikipedia edition.
	 */
	private String languageCode;

	/**
	 * Creates a new {@code WikipediaEdition}.
	 * @param language The language of the Wikipedia edition (English name).
	 * @param languageLocal The language of the Wikipedia edition (Local name).
	 * @param languageCode The code of the language of the Wikipedia edition.
	 */
	public WikipediaEdition(String language, String languageLocal, String languageCode) {
		this.language = language;
		this.languageLocal = languageLocal;
		this.languageCode = languageCode;
	}

	/**
	 * Returns the language of this Wikipedia edition (in English).
	 * @return The language of this Wikipedia edition (in English).
	 */
	public String language() {
		return this.language;
	}

	/**
	 * Returns the code of the language of this Wikipedia edition.
	 * @return The code of the language of this Wikipedia edition.
	 */
	public String languageCode() {
		return this.languageCode;
	}

	/**
	 * Returns the language of this Wikipedia edition (local name). 
	 * @return The language of this Wikipedia edition (local name).
	 */
	public String languageLocal() {
		return this.languageLocal;
	}

	/**
	 * Returns the URL of the Webpage that lists the dumps of this Wikipedia edition.
	 * @return The URL of the Webpage that lists the dumps of this Wikipedia edition.
	 */
	public String dumpUrl() {
		return DUMP_BASE_URL + wikiCodeName() +"/";
	}

	/**
	 * Returns the code name of this Wikipedia edition (e.g., enwiki for the English Wikipedia.)
	 * @return The code name of this Wikipedia edition.
	 */
	public String wikiCodeName() {
		return this.languageCode + "wiki";
	}

	/**
	 * Returns the URL of the Web page that contains the dump files of this Wikipedia edition as of a 
	 * specific date.
	 * @param date A date (in the form YYYYMMDD)
	 * @return The URL of the Web page that contains the dump files of this Wikipedia edition as of a 
	 * specific date.
	 */
	public String dumpUrl(String date) {
		return dumpUrl() + date + "/";
	}


	/**
	 * Returns the URL of the Wikipedia XML file of this Wikipedia edition, as of a specific date.
	 * @param date A date (in the form YYYYMMDD)
	 * @return The URL of the Wikipedia XML file of this Wikipedia edition, as of a specific date.
	 */
	public String xmlFileUrl(String date) {
		return dumpUrl(date) + this.languageCode + "wiki-" + date + "-" + GraphipediaSettings.WIKIPEDIA_XML_FILE;
	}

	/**
	 * Returns the URL of the file with the cross-language links of this Wikipedia edition, as of a specific date.
	 * @param date A date (in the form YYYYMMDD)
	 * @return The URL of the file with the cross-language links of this Wikipedia edition, as of a specific date.
	 */
	public String crossLinkFileUrl(String date) {
		return dumpUrl(date) + this.languageCode + "wiki-" + date + "-" + GraphipediaSettings.WIKIPEDIA_CROSSLINKS_FILE;
	}

	/**
	 * Returns the URL of the file with the geotags of this Wikipedia edition, as of a specific date.
	 * @param date A date (in the form YYYYMMDD)
	 * @return The URL of the file with the geotags of this Wikipedia edition, as of a specific date.
	 */
	public String geotagFileUrl(String date) {
		return dumpUrl(date) + this.languageCode + "wiki-" + date + "-" + GraphipediaSettings.WIKIPEDIA_GEOTAGS_FILE;
	}
	
	/**
	 * Downloads the last complete dump of this Wikipedia language edition.
	 * @param checkpoint The checkpoint information. 
	 * @param logger The logger of Graphipedia, to display information and warnings.
	 * @param targetDirectory The directory where the dump is downloaded
	 * @return {@code true} if no error occurs, {@code false} otherwise.
	 */
	public boolean download(CheckPoint checkpoint, Logger logger, File targetDirectory) {
		if ( checkpoint.isDownloadedEdition(languageCode()) ) {
			logger.info("Already downloaded");
			return true;
		}
		if ( targetDirectory.exists() ) {
			if ( !targetDirectory.mkdir() ) {
				logger.severe("Cannot create directory " + targetDirectory.getAbsolutePath());
				return false;
			}
		}
		WikipediaDump dump = null;
		try {
			dump = WikipediaDump.lastCompleteDump(this);
		} catch (Exception e) {
			logger.severe("Error while obtaining the date of the last complete dump");
			e.printStackTrace();
			return false;
		}
		if ( dump == null ) {
			logger.warning("No complete dump available");
			return true;
		}
		String dumpDate = dump.date();
		logger.info("Date of last complete dump " + dumpDate.substring(0, 4) + ""
				+ "-" + dumpDate.substring(4, 6) + "-" + dumpDate.substring(6));
		
		File xmlWikipediaFile = new File(targetDirectory, GraphipediaSettings.WIKIPEDIA_XML_FILE);
		downloadFile(checkpoint, logger, dump.xmlDumpFile(), xmlWikipediaFile, "Downloading the Wikipedia XML file...");
		
		File crossLinksFile = new File(targetDirectory, GraphipediaSettings.WIKIPEDIA_CROSSLINKS_FILE);
		downloadFile(checkpoint, logger, dump.crosslinkDumpFile(), crossLinksFile, "Downloading the cross-language links file...");
		
		File geotagsFile = new File(targetDirectory, GraphipediaSettings.WIKIPEDIA_GEOTAGS_FILE);
		downloadFile(checkpoint, logger, dump.geotagsDumpFile(), geotagsFile, "Downloading the geotags file...");
		
		try {
			checkpoint.addDownloadedEdition(languageCode(), true);
		} catch (IOException e) {
			logger.severe("Error while saving the checkpoint to file");
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	/**
	 * Downloads a dump file.
	 * @param checkpoint The checkpoint information.
	 * @param logger The logger of Graphipedia.
	 * @param sourceFile The the source file. 
	 * @param targetFile The target file.
	 * @param message The message to display.
	 */
	private void downloadFile(CheckPoint checkpoint, Logger logger, DumpFile sourceFile, File targetFile, String message) {
		if ( !checkpoint.isDownloadedFile(targetFile.getAbsolutePath()) ) {
			long start = System.currentTimeMillis();
			logger.info(message);
			URL url = null;
			try {
				url =  new URL(sourceFile.url());
			} catch (MalformedURLException e) {
				logger.severe("Malformed URL. Should not happen. Did Wikipedia change the URLs of its dump files?");
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				downloadFile(url, targetFile.getAbsolutePath(), sourceFile.size());
			} catch (IOException e) {
				logger.severe("Could not download the file at " + url.toString());
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				checkpoint.addDownloadedFile(targetFile.getName(), true);
			} catch (IOException e) {
				logger.severe("Error while saving the checkpoint to file");
				e.printStackTrace();
			}
			long elapsed = System.currentTimeMillis() - start;
			logger.info("File downloaded in "+ ReadableTime.readableTime(elapsed));
		}
		else
			logger.info("Already downloaded");
	}
	
	
	/**
	 * Download the file at the given URL to the target file.
	 * @param url The URL of the file to download.
	 * @param targetFile The target file.
	 * @param size The size of the file to download
	 * @throws IOException when some I/O error occurs.
	 */
	private void downloadFile(URL url, String targetFile, double size) throws IOException {
		HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
		DownloadProgress progressBar = size != -1L ? new DownloadProgress(size) : null;
		java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
		java.io.FileOutputStream fos = new java.io.FileOutputStream(targetFile);
		java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
		byte[] data = new byte[1024];
		long downloadedFileSize = 0;
		int x = 0;
		while ((x = in.read(data, 0, 1024)) >= 0) {
			downloadedFileSize += x;
			if ( progressBar != null && (double)downloadedFileSize < size )
				progressBar.visualize((double)downloadedFileSize);
			bout.write(data, 0, x);
		}
		if (progressBar != null)
			progressBar.visualize(size);
		in.close();
		bout.close();
	}



}
