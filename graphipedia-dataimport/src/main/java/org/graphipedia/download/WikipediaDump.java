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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphipedia.GraphipediaSettings;

/**
 * A specific dump of a Wikipedia language edition.
 *
 */
public class WikipediaDump {
	
	/**
	 * The date of this Wikipedia dump.
	 */
	private String date;
	
	/**
	 * The XML dump file.
	 */
	private DumpFile xmlDumpFile;
	
	/**
	 * The cross link dump file.
	 */
	private DumpFile crosslinkDumpFile;
	
	/**
	 * The geo tags dump file.
	 */
	private DumpFile geotagsDumpFile;
	
	/**
	 * Returns the XML dump file.
	 * @return The XML dump file.
	 */
	public DumpFile xmlDumpFile() {
		return this.xmlDumpFile;
	}
	
	/**
	 * Returns the cross-link dump file.
	 * @return The cross-link dump file.
	 */
	public DumpFile crosslinkDumpFile() {
		return this.crosslinkDumpFile;
	}
	
	/**
	 * 
	 * Returns the geotags dump file.
	 * @return The geotags dump file.
	 * 
	 */
	public DumpFile geotagsDumpFile() {
		return this.geotagsDumpFile;
	}
	
	/**
	 * Returns the date of this dump.
	 * @return The date of this dump.
	 */
	public String date() {
		return this.date;
	}
	
	/**
	 * Returns the date (in the form YYYYMMDD) of the last complete dump of a Wikipedia edition.
	 * @param edition A wikipedia edition.
	 * 
	 * @return The date of the last complete dump of the Wikipedia edition, if any, {@code null} otherwise.
	 * @throws Exception when any error occurs while reading the Web page that lists the dumps of the 
	 * Wikipedia edition.
	 */
	public static WikipediaDump lastCompleteDump(WikipediaEdition edition) throws Exception {
		URL url = new URL(edition.dumpUrl());
		Pattern pattern = Pattern.compile("<a href=\"(\\d{8})/\">\\d{8}/</a>.*");
		BufferedReader bd = new BufferedReader(new InputStreamReader(url.openStream()));
		String line; 
		LinkedList<String> editionDates = new LinkedList<String>();
		while( (line = bd.readLine() ) != null ) {
			Matcher m = pattern.matcher(line);
			if ( m.matches() ) 
				editionDates.addFirst(m.group(1));
		}
		bd.close();
		for (String editionDate : editionDates) {
			WikipediaDump dump = new WikipediaDump();
			if ( dump.parseDump(edition, editionDate) ) 
				return dump;
		}
		return null;
	}
	
	/**
	 * Parses the Web page of a specific dump as of a specific date.
	 * @param edition A Wikipedia edition.
	 * @param date The date of the Wikipedia edition.
	 * @return {@code true} if the dump is complete, {@code  false} otherwise.
	 * @throws Exception when some error occurs while parsing the Web page.
	 */
	private boolean parseDump(WikipediaEdition edition, String date) throws Exception {
		this.date = date;
		String wikiCodeName = edition.wikiCodeName();
		Pattern xmlFilePattern = Pattern.compile(".*<a href=\"/"+wikiCodeName+"/"+date+"/"+wikiCodeName+"-"+date+"-"
				+GraphipediaSettings.WIKIPEDIA_XML_FILE+"\">"+wikiCodeName+"-"+date+"-"+GraphipediaSettings.WIKIPEDIA_XML_FILE+"</a>(.+?)</li>.*");
		Pattern crossLinkFilePattern = Pattern.compile(".*<a href=\"/"+wikiCodeName+"/"+date+"/"+wikiCodeName+"-"+date+"-"
				+GraphipediaSettings.WIKIPEDIA_CROSSLINKS_FILE+"\">"+wikiCodeName+"-"+date+"-"+GraphipediaSettings.WIKIPEDIA_CROSSLINKS_FILE+"</a>(.+?)</li>.*");
		Pattern geotagsFilePattern = Pattern.compile(".*<a href=\"/"+wikiCodeName+"/"+date+"/"+wikiCodeName+"-"+date+"-"
				+GraphipediaSettings.WIKIPEDIA_GEOTAGS_FILE+"\">"+wikiCodeName+"-"+date+"-"+GraphipediaSettings.WIKIPEDIA_GEOTAGS_FILE+"</a>(.+?)</li>.*");
		
		URL url = new URL(edition.dumpUrl(date));
		BufferedReader bd = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		boolean complete = false;
		while( (line = bd.readLine()) != null ) {
			if ( line.contains("<span class='done'>Dump complete</span>") )
				complete = true;
			Matcher xmlFileMatcher = xmlFilePattern.matcher(line);
			Matcher crossLinkFileMatcher = crossLinkFilePattern.matcher(line);
			Matcher geotagsFileMatcher = geotagsFilePattern.matcher(line);
			if (xmlFileMatcher.matches()) 
				this.xmlDumpFile = new DumpFile(edition.xmlFileUrl(date), size(xmlFileMatcher.group(1))) ;
			else if ( crossLinkFileMatcher.matches() )
				this.crosslinkDumpFile = new DumpFile(edition.crossLinkFileUrl(date), size(xmlFileMatcher.group(1))) ;
			else if ( geotagsFileMatcher.matches() )
				this.geotagsDumpFile = new DumpFile(edition.geotagFileUrl(date), size(xmlFileMatcher.group(1)));
		}
		bd.close();
		return complete;
		
	}
	
	/**
	 * Returns the size in bytes out of the given raw size 
	 * @param rawSize The raw size (expressed in some unit, such as KB, MB, GB)
	 * @return The size in bytes
	 */
	private double size(String rawSize) {
		rawSize = rawSize.trim();
		String[] values = rawSize.split(" ");
		double sizeValue = Double.parseDouble(values[0]);
		String unit = values[1];
		if ( unit.equals("KB") )
			return sizeValue * 1024.;
		if ( unit.equals("MB") )
			return sizeValue * 1024. * 1024.;
		if (unit.equals("GB"))
			return sizeValue * 1024. * 1024. * 1024.;
		return sizeValue;
	}

}
