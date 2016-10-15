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
package org.graphipedia.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Logger;

import org.graphipedia.dataextract.PleaseWait;
import org.wikipedia.Wiki;

/**
 * The set of templates in a Wikipedia language editoion that are used to generate infoboxes.
 * Virtually any Wikipedia language edition organizes the infobox templates into specific categories 
 * that are direct or indirect subcategories of a root category. 
 * For instance, in English, the root category of all infobox templates is Category:Infobox templates. 
 * Since the root categories are not that likely to change (well, nothing is impossible), the names of the root categories of
 * all Wikipedia language editions are stored in file {@code ROOT_CATEGORY_FILE}.
 * Each line of this file contains the language code of a Wikipedia edition and the name of the root category in that edition.
 * The two fields are separated by a tabular character.
 * 
 * Before Graphipedia imports the data of a Wikipedia language edition, it checks whether the directory corresponding to the Wikipedia edition
 * to import contains a file named {@code INFOBOX_TEMPLATES_FILE}. 
 * This file contains the list of infobox templates, one for each line; the name of the template is assumed to be already stripped of the prefix 
 * Template:. 
 * If the file exists, Graphipedia loads the list of infobox templates from that file.
 * Otherwise, it downloads the list of templates directly from Wikipedia by using the MediaWiki API and stores the list
 * in a file named  {@code INFOBOX_TEMPLATES_FILE}.
 *  
 */
public class InfoboxTemplates  {

	/**
	 * The set of all infobox templates.
	 */
	private Set<String> infoboxTemplates;

	/**
	 * The file that lists the root categories across all Wikipedia editions.
	 */
	public static final String ROOT_CATEGORY_FILE = "infobox-templates-root-categories.csv";

	/**
	 * The file that contains the list of all infobox templates of a given Wikipedia language edition.
	 */
	public static final String INFOBOX_TEMPLATES_FILE = "infobox-templates.txt";

	/**
	 * The directory that contains the files necessary to import
	 * a Wikipedia language edition.
	 */
	private File wikipediaEditionDirectory;

	/**
	 * Creates a new {@code InfoboxTemplates}.
	 * @param wikipediaEditionDirectory The directory that contains the files necessary to import
	 * a Wikipedia language edition.
	 */
	public InfoboxTemplates(File wikipediaEditionDirectory) {
		this.infoboxTemplates = new HashSet<String>();
		this.wikipediaEditionDirectory = wikipediaEditionDirectory;
	}

	/**
	 * Loads the list of infobox templates from the input file. The file MUST exist.
	 * 
	 * @param inputFile The file that contains the list of all infobox templates.
	 * @throws IOException when something goes wrong while reading the input file.
	 */
	public void load(File inputFile) throws IOException {
		BufferedReader bd = new BufferedReader(new FileReader(inputFile));
		String line;
		while( (line = bd.readLine()) != null ) 
			infoboxTemplates.add(line);
		bd.close();
	}
	
	/**
	 * Returns the file that is expected to contain the infobox templates.
	 * @return The file that is expected to contain the infobox templates.
	 */
	public File getInfoboxTemplatesFile() {
		return new File(wikipediaEditionDirectory, INFOBOX_TEMPLATES_FILE);
	}

	/**
	 * Loads the list of infobox templates directly from Wikipedia by using the MediaWiki API.
	 * Writes the templates to a file named {@code INFOBOX_TEMPLATES_FILE} in the directory that
	 * contains the necessary files to import a Wikipedia language edition.
	 * @param language The code of the language edition to import.
	 * @param rootCategory The root category including the infobox templates.
	 * @param outputFile The file where the infobox templates are written.
	 * @param logger The logger of Graphipedia.
	 * @throws IOException when something goes wrong while using the MediaWiki API or writing the output file.
	 */
	public void load(String language, String rootCategory, File outputFile, Logger logger) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		Set<String> visitedCategories = new HashSet<String>();
		Wiki wiki = new Wiki(language + ".wikipedia.org");
		List<String> categories = new ArrayList<String>();
		categories.add(rootCategory);
		Timer timer = new Timer();
		PleaseWait pleaseWait = new PleaseWait(logger);
		timer.schedule(pleaseWait, 0, 10000);
		for ( int i = 0; i < categories.size(); i += 1 ) {
			String currentCategory = categories.get(i);
			String[] cats = null;
			do {
                int[] ns = {Namespace.CATEGORY};
				cats = wiki.getCategoryMembers(currentCategory, ns);
			} while( cats == null );
		
			for (String cat : cats) 
				if ( !visitedCategories.contains(cat) ) {
					visitedCategories.add(cat);
					categories.add(cat);
				}
			String[] templates = null;
			do {
                int[] ns = {Namespace.TEMPLATE};
				templates = wiki.getCategoryMembers(currentCategory, ns);
			} while( templates == null ); 
			
			for ( String template : templates ) {
				int colonIndex = template.indexOf(":");
				String templateName = template.substring(colonIndex + 1);
				infoboxTemplates.add(templateName);
				pleaseWait.addDetails("infobox templates extracted so far " + infoboxTemplates.size());
				bw.write(templateName + "\n");
			}

		}
		timer.cancel();
		pleaseWait.stop();
		bw.close();
	}

	/**
	 * Returns whether a page with a specific title is an infobox template.
	 * @param title The title of a Wikipedia page (without the prefix Template:)
	 * @return {@code true} if the title corresponds to a infobox template, {@code false} otherwise.
	 */
	public boolean isInfoboxTemplate(String title) {
		return  infoboxTemplates.contains(title);
	}
	
	@Override
	public String toString(){
		String s = "";
		for ( String it : infoboxTemplates )
			s += s.length() == 0 ? "[ " + it : ";; " + it;
		s += " ]";
		return s;
	}

}
