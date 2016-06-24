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
package org.graphipedia.progress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the checkpoint information that
 * Graphipedia stores in order to be resilient to abrupt shutdowns.
 * When Graphipedia is shutdown because of a problem, some operations are not
 * performed again, provided that the previous computation has completed them
 * safely.
 * This guarantees that Graphipedia does not have to be run from scratch in case
 * some problems arise.
 *
 */
public class CheckPoint {
	
	/**
	 * The name of the file where the checkpoint information are stored by Graphipedia.
	 */
	public static final String CHECK_POINT_FILE_NAME = "gp-checkpoint";
	
	/**
	 * The actual file where the check point information are stored.
	 */
	private File checkpointFile;
	
	/**
	 * The absolute paths of the dump files that are completely downloaded from
	 * the Wikimedia websites. 
	 * 
	 */
	private Set<String> downloadedFiles;
	
	/**
	 * The codes of the languages of the editions that have already been succesfully downloaded.
	 */
	private Set<String> downloadedEditions;
	
	/**
	 * The codes of the languages of the editions for which the disambiguation pages have been already 
	 * extracted from Wikipedia.
	 */
	private Set<String> disambigExtracted;
	
	/**
	 * The codes of the languages of the editions for which the infobox templates have been already 
	 * extracted from Wikipedia.
	 */
	private Set<String> infoboxExtracted;
	
	/**
	 * The codes of the languages of the editions for which the links have been extracted from the XML dump file.
	 */
	private Set<String> linksExtracted;
	
	/**
	 * The codes of the languages of the editions for which the cross-links have been extracted from the dump file.
	 */
	private Set<String> crossLinksExtracted;
	
	/**
	 * Constructor.
	 * @param rootDirectory The root working directory of Graphipedia. 
	 */
	public CheckPoint(File rootDirectory) {
		this.checkpointFile = new File(rootDirectory, CHECK_POINT_FILE_NAME);
		this.downloadedFiles = new HashSet<String>();
		this.downloadedEditions = new HashSet<String>();
		this.disambigExtracted = new HashSet<String>();
		this.infoboxExtracted = new HashSet<String>();
		this.linksExtracted = new HashSet<String>();
		this.crossLinksExtracted = new HashSet<String>();
	}
	
	/**
	 * Returns the file where the checkpoint information are saved.
	 * @return The file where the checkpoint information are saved.
	 */
	public File getCheckPointFile() {
		return this.checkpointFile;
	}
	
	/**
	 * Adds the specified file to the list of downloaded files.
	 * 
	 * @param fileName The name of the file that is successfully downloaded.
	 * @param save Set {@code true} if the checkpoint is to be saved to file. 
	 * @throws IOException when something goes wrong while writing the checkpoint file.
	 */
	public void addDownloadedFile(String fileName, boolean save) throws IOException {
		this.downloadedFiles.add(fileName);
		if (save)
			save(CheckPointFlag.fileDownload, fileName);
	}
	
	/**
	 * Adds a Wikipedia edition to the list of successfully downloaded Wikipedia editions.
	 * The checkpoint is saved to file.
	 * 
	 * @param edition The code of the language of a specific Wikipedia edition.
	 * @param save Set {@code true} if the checkpoint is to be saved to file.
	 * @throws IOException when a I/O error occurs while writing the checkpoint file.
	 */
	public void addDownloadedEdition(String edition, boolean save) throws IOException {
		this.downloadedEditions.add(edition);
		if (save)
			save(CheckPointFlag.editionDownload, edition);
	}
	
	/**
	 * Adds a Wikipedia edition to the list of editions for which the disambiguation pages have already been 
	 * extracted.
	 * The checkpoint is saved to file.
	 * 
	 * @param edition The code of the language of the edition for which the disambiguation pages have already been 
	 * extracted. 
	 * @param save Set {@code true} if the checkpoint is to be saved to file.
	 * @throws IOException when a I/O error occurs while writing the checkpoint file.
	 */
	public void addDisambiguationExtracted(String edition, boolean save) throws IOException {
		this.disambigExtracted.add(edition);
		save(CheckPointFlag.disambigExtracted, edition);
	}
	
	/**
	 * Adds a Wikipedia edition to the list of editions for which the infobox templates have already been 
	 * extracted.
	 * The checkpoint is saved to file.
	 * 
	 * @param edition The code of the language of the edition for which the infobox templates have already been 
	 * extracted. 
	 * @param save Set {@code true} if the checkpoint is to be saved to file.
	 * @throws IOException when a I/O error occurs while writing the checkpoint file.
	 */
	public void addInfoboxExtracted(String edition, boolean save) throws IOException {
		this.infoboxExtracted.add(edition);
		if (save)
			save(CheckPointFlag.infoboxExtracted, edition);
	}
	
	/**
	 * Adds a Wikipedia edition to the list of editions for which the links have already been 
	 * extracted from the XML file.
	 * The checkpoint is saved to file.
	 * 
	 * @param edition The code of the language of the edition for which the links have already been 
	 * extracted from the XML file. 
	 * @param save Set {@code true} if the checkpoint is to be saved to file.
	 * @throws IOException when a I/O error occurs while writing the checkpoint file.
	 */
	public void addLinksExtracted(String edition, boolean save) throws IOException {
		this.linksExtracted.add(edition);
		if (save)
			save(CheckPointFlag.linksExtracted, edition);
	}
	
	/**
	 * Adds a Wikipedia edition to the list of editions for which the cross-links have already been 
	 * extracted from the dump file.
	 * The checkpoint is saved to file.
	 * 
	 * @param edition The code of the language of the edition for which the cross-links have already been 
	 * extracted from the dump file. 
	 * @param save Set {@code true} if the checkpoint is to be saved to file.
	 * @throws IOException when a I/O error occurs while writing the checkpoint file.
	 */
	public void addCrossLinksExtracted(String edition, boolean save) throws IOException {
		this.crossLinksExtracted.add(edition);
		if (save)
			save(CheckPointFlag.crosslinksExtracted, edition);
	}
	
	/**
	 * Returns whether a file has been already successfully downloaded
	 * @param filename The absolute path to the file to check
	 * @return {@code true} if the specified  file has been successfully downloaded, {@code false} otherwise.
	 */
	public boolean isDownloadedFile(String filename) {
		return this.downloadedFiles.contains(filename);
	}
	
	/**
	 * Returns whether a Wikipedia edition has been already successfully downloaded
	 * @param edition The code of the language of the Wikipedia edition to check.
	 * @return {@code true} if the specified  edition has been successfully downloaded, {@code false} otherwise.
	 */
	public boolean isDownloadedEdition(String edition) {
		return this.downloadedEditions.contains(edition);
	}
	
	/**
	 * Returns whether the disambiguation pages have already been obtained from Wikipedia for a 
	 * specific edition.
	 * @param edition The code of the language of a Wikipedia edition.
	 * @return {@code true} if the disambiguation pages have already been obtained for the specified edition,
	 * {@code false} otherwise.
	 */
	public boolean isDisambiguationExtracted(String edition) {
		return this.disambigExtracted.contains(edition);
	}
	
	/**
	 * Returns whether the infobox templates have already been obtained from Wikipedia for a 
	 * specific edition.
	 * @param edition The code of the language of a Wikipedia edition.
	 * @return {@code true} if the infobox templates have already been obtained for the specified edition,
	 * {@code false} otherwise.
	 */
	public boolean isInfoboxExtracted(String edition) {
		return this.infoboxExtracted.contains(edition);
	}
	
	/**
	 * Returns whether the links have already been extracted from the XML file of a 
	 * specific edition.
	 * @param edition The code of the language of a Wikipedia edition.
	 * @return {@code true} if links have already been extracted from the XML file of the specified edition,
	 * {@code false} otherwise.
	 */
	public boolean isLinksExtracted(String edition) {
		return this.linksExtracted.contains(edition);
	}
	
	/**
	 * Returns whether the cross-links have already been extracted from the dump file of a 
	 * specific edition.
	 * @param edition The code of the language of a Wikipedia edition.
	 * @return {@code true} if the cross-links have already been extracted from the dump file of the specified edition,
	 * {@code false} otherwise.
	 */
	public boolean isCrossLinksExtracted(String edition) {
		return this.crossLinksExtracted.contains(edition);
	} 
	
	/**
	 * Reads the checkpoint from file.
	 * @return {@code true} if the file exists, {@code false} otherwise.
	 * @throws IOException when some I/O error occurs while reading the checkpoint file.
	 */
	public boolean load() throws IOException {
		if ( !this.checkpointFile.exists() )
			return false;
		BufferedReader bd = new BufferedReader(new FileReader(this.checkpointFile));
		String line;
		while ( (line = bd.readLine()) != null ) {
			String[] values = line.split("\t");
			CheckPointFlag flag = CheckPointFlag.valueOf(values[0]);
			String object = values[1];
			switch(flag) {
			case fileDownload:
				addDownloadedFile(object, false);
				break;
			case editionDownload:
				addDownloadedEdition(object, false);
				break;
			case disambigExtracted:
				addDisambiguationExtracted(object, false);
				break;
			case infoboxExtracted:
				addInfoboxExtracted(object, false);
				break;
			case linksExtracted:
				addLinksExtracted(object, false);
				break;
			case crosslinksExtracted:
				addCrossLinksExtracted(object, false);
				break;
			default:
				break;
			}	
		}
		bd.close();
		return true;
	}
	
	/**
	 * Writes a checkpoint to file.
	 * 
	 * @param flag The flag of the checkpoint.
	 * @param object The object of the checkpoint.
	 * @throws IOException when a I/O error occurs while writing the file.
	 */
	private synchronized void save(CheckPointFlag flag, String object) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(checkpointFile, true));
		bw.write(flag.name() + "\t" + object + "\n");
		bw.close();
	}
	
	
}
