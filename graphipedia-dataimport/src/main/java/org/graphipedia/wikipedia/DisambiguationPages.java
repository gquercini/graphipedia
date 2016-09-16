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
 * The set of disambiguation pages in a Wikipedia language edition. 
 * A disambiguation page is meant to provide links to articles that are possible interpretations of a 
 * ambiguous word (such as, Java for instance).
 * Virtually in any Wikipedia edition, disambiguation pages are affected to a specific root category (e.g., Category:Disambiguation_pages in the 
 * English Wikipedia) or any subcategory of this root category.
 * Since the root categories are not that likely to change (well, nothing is impossible), the names of the root categories of
 * all Wikipedia language editions are stored in file {@code ROOT_CATEGORY_FILE}.
 * Each line of this file contains the language code of a Wikipedia edition and the name of the root category in that edition.
 * The two fields are separated by a tabular character.
 * 
 * Before Graphipedia imports the data of a Wikipedia language edition, it checks whether the directory corresponding to the Wikipedia edition
 * to import contains a file named {@code DISAMBIGUATION_PAGES_FILE}. 
 * This file contains the list of titles of disambiguation files, one for each line.
 * If the file exists, Graphipedia loads the list of disambiguation pages from that file.
 * Otherwise, it downloads the list of disambiguation pages directly from Wikipedia by using the MediaWiki API and stores the list
 * in the file named {@code DISAMBIGUATION_PAGES_FILE}.
 *  
 */
public class DisambiguationPages {
	
	/**
	 * The set of all disambiguation pages in a Wikipedia language edition.
	 */
	private Set<String> disambiguationPages;

	/**
	 * The file that lists the root categories across all Wikipedia editions.
	 */
	public static final String ROOT_CATEGORY_FILE = "disambiguation-root-categories.csv";

	/**
	 * The file that contains the list of all disambiguation pagees of a given Wikipedia language edition.
	 */
	public static final String DISAMBIGUATION_PAGES_FILE = "disambiguation-pages.txt";
	
	/**
	 * The directory that contains the files necessary to import
	 * a Wikipedia language edition.
	 */
	private File wikipediaEditionDirectory;
	
	/**
	 * Creates a new {@code DisambiguationPages}.
	 * @param wikipediaEditionDirectory The directory that contains the files necessary to import
	 * a Wikipedia language edition.
	 */
	public DisambiguationPages(File wikipediaEditionDirectory) {
		this.disambiguationPages = new HashSet<String>();
		this.wikipediaEditionDirectory = wikipediaEditionDirectory;
	}
	
	/**
	 * Loads the list of disambiguation file from the input file. The file MUST exist.
	 * 
	 * @param inputFile The file that contains the list of all disambiguation pages.
	 * @throws IOException when something goes wrong while reading the input file.
	 */
	public void load(File inputFile) throws IOException {
		BufferedReader bd = new BufferedReader(new FileReader(inputFile));
		String line;
		while( (line = bd.readLine()) != null ) 
			disambiguationPages.add(line);
		bd.close();
	}
	
	/**
	 * Returns the file that holds the disambiguation pages file.
	 * @return The file that holds the disambiguation pages file, if it exists.
	 */
	public File getDisambiguationPagesFile() {
		return new File(wikipediaEditionDirectory, DISAMBIGUATION_PAGES_FILE);
	}

	
	/**
	 * Loads the list of disambiguation pages directly from Wikipedia by using the MediaWiki API.
	 * This list is saved to the a file named {@code DISAMBIGUATION_PAGES_FILE} in the directory
	 * that contains the files necessary to import a Wikipedia language edition.
	 * @param language The code of the language edition to import.
	 * @param rootCategory The root category that contains the disambiguation pages.
	 * @param outputFile The file where the list of disambiguation pages is written.
	 * @param logger The logger of Graphipedia.
	 * @throws IOException when something goes wrong while using the MediaWiki API or creating the output file.
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
			} while (cats == null);
			
			for (String cat : cats) 
				if ( !visitedCategories.contains(cat) ) {
					visitedCategories.add(cat);
					categories.add(cat);
				}
			String[] disambiguationPages = null;
			do {
                int[] ns = {Namespace.MAIN};
				disambiguationPages = wiki.getCategoryMembers(currentCategory, ns);
			}
			while( disambiguationPages == null );
			
			for ( String page : disambiguationPages ) { 
					this.disambiguationPages.add(page);
					pleaseWait.addDetails("disambiguation pages extracted so far " + this.disambiguationPages.size());
					bw.write(page + "\n");
			}

		}
		timer.cancel();
		bw.close();
	}
	
	/**
	 * Returns whether a page with a given title is a disambiguation page.
	 * @param title The title of a Wikipedia page.
	 * @return {@code true} if the page with the given {@code title} is a disambiguation page, {@code false}
	 * otherwise.
	 */
	public boolean isDisambiguationPage(String title) {
		return this.disambiguationPages.contains(title);
	}

}
