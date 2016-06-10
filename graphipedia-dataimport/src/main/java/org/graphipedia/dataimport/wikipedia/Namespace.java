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
package org.graphipedia.dataimport.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.graphipedia.dataimport.LinkExtractor;

/**
 * This class represents a namespace in Wikipedia.
 * Namespaces in Wikipedia are identified by a number, which is the same across all language editions
 * (e.g., 14 is the identifier of the namespace Category) and a name, which is language-specific (e.g., the namespace
 * Category has name Categoria in Italian and Category in English).
 * The names of each namespace can be found in the XML file that has the whole content of a Wikipedia language edition.
 * When the {@link LinkExtractor} parses this file, it creates a CSV file that records the namespaces, so that they can be retrieved later.
 * Each line of this file contains information about a namespace, that is the identifier and the name, separated by a comma. 
 */
public class Namespace {

	/**
	 * The name of the file where the names of the namespaces are saved for each 
	 * Wikipedia language edition, so that they can be retrieved later.
	 */
	public static final String NAMESPACE_FILE = "namespaces.csv";

	/**
	 * The identifier of the main namespace in Wikipedia. 
	 */
	public static final int MAIN = 0;

	/**
	 * The identifier of the talk namespace in Wikipedia.
	 */
	public static final int TALK = 1;

	/**
	 * The identifier of the user namespace in Wikipedia.
	 */
	public static final int USER = 2;

	/**
	 * The identifier of the user talk namespace in Wikipedia.
	 */
	public static final int USER_TALK = 3;

	/**
	 * The identifier of the wikipedia namespace in Wikipedia.
	 */
	public static final int WIKIPEDIA = 4;

	/**
	 * The identifier of the wikipedia talk namespace in Wikipedia.
	 */
	public static final int WIKIPEDIA_TALK = 5;

	/**
	 * The identifier of the file namespace in Wikipedia.
	 */
	public static final int FILE = 6;

	/**
	 * The identifier of the file talk namespace in Wikipedia.
	 */
	public static final int FILE_TALK = 7;

	/**
	 * The identifier of the mediawiki namespace in Wikipedia.
	 */
	public static final int MEDIAWIKI = 8;

	/**
	 * The identifier of the mediawiki talk namespace in Wikipedia.
	 */
	public static final int MEDIAWIKI_TALK = 9;

	/**
	 * The identifier of the namespace template in Wikipedia.
	 */
	public static final int TEMPLATE = 10;

	/**
	 * The identifier of the template talk namespace in Wikipedia.
	 */
	public static final int TEMPLATE_TALK = 11;

	/**
	 * The identifier of the help namespace in Wikipedia.
	 */
	public static final int HELP = 12;

	/**
	 * The identifier of the help talk namespace in Wikipedia.
	 */
	public static final int HELP_TALK = 13;

	/**
	 * The identifier of the namespace category in Wikipedia.
	 */
	public static final int CATEGORY = 14;

	/**
	 * The identifier of the category talk namespace in Wikipedia.
	 */
	public static final int CATEGORY_TALK = 15;

	/**
	 * The identifier of the portal namespace in Wikipedia.
	 */
	public static final int PORTAL = 100;

	/**
	 * The identifier of the portal talk namespace in Wikipedia.
	 */
	public static final int PORTAL_TALK = 101;

	/**
	 * The identifier of the book namespace in Wikipedia.
	 */
	public static final int BOOK = 108;

	/**
	 * The identifier of the book talk namespace in Wikipedia.
	 */
	public static final int BOOK_TALK = 109;

	/**
	 * The identifier of the draft namespace in Wikipedia.
	 */
	public static final int DRAFT = 118;

	/**
	 * The identifier of the draft talk namespace in Wikipedia.
	 */
	public static final int DRAFT_TALK = 119;

	/**
	 * The identifier of the education program namespace in Wikipedia.
	 */
	public static final int EDUCATION_PROGRAM = 446;

	/**
	 * The identifier of the education program talk namespace in Wikipedia.
	 */
	public static final int EDUCATION_PROGRAM_TALK = 447;

	/**
	 * The identifier of the timed text namespace in Wikipedia.
	 */
	public static final int TIMED_TEXT = 710;

	/**
	 * The identifier of the timed text talk namespace in Wikipedia.
	 */
	public static final int TIMED_TEXT_TALK = 711;

	/**
	 * The identifier of the module namespace in Wikipedia.
	 */
	public static final int MODULE = 828;
	
	/**
	 * The identifier of the module talk namespace in Wikipedia.
	 */
	public static final int MODULE_TALK = 829;

	/**
	 * The identifier of the gadget namespace in Wikipedia.
	 */
	public static final int GADGET = 2300;

	/**
	 * The identifier of the gadget talk namespace in Wikipedia.
	 */
	public static final int GADGET_TALK = 2301;

	/**
	 * The identifier of the gadget definition namespace in Wikipedia.
	 */
	public static final int GADGET_DEFINITION = 2302;

	/**
	 * The identifier of the gadget definition talk namespace in Wikipedia.
	 */
	public static final int GADGET_DEFINITION_TALK = 2303;

	/**
	 * The identifier of the topic namespace in Wikipedia.
	 */
	public static final int TOPIC = 2600;

	/**
	 * The identifier of the special namespace in Wikipedia.
	 */
	public static final int SPECIAL = -1;

	/**
	 * The identifier of the media namespace in Wikipedia.
	 */
	public static final int MEDIA = -2;

	/**
	 * Any namespace. 
	 */
	public static final int ANY = Integer.MIN_VALUE;

	/**
	 * The identifier of this namespace.
	 */
	private int id;

	/**
	 * The name of this namespace
	 */
	private String name;

	/**
	 * Creates a new namespace.
	 * @param id The identifier of the namespace.
	 * @param name The name of the namespace.
	 */
	public Namespace(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the identifier of this namespace.
	 * @return The identifier of this namespace.
	 */
	public int id() {
		return this.id;
	}

	/**
	 * Returns the name of this namespace.
	 * @return The name of this namespace.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Returns whether this namespace is the main namespace.
	 * @return {@code true} if this namespace is the main namespace, {@code false} otherwise.
	 */
	public boolean isMain() {
		return this.id == MAIN;
	}

	/**
	 * Returns whether this namespace is the category namespace.
	 * @return {@code true} if this namespace is the category namespace, {@code false} otherwise.
	 */
	public boolean isCategory() {
		return this.id == CATEGORY;
	}

	/**
	 * Writes this namespace to the CSV file that records the namespaces for convenience.
	 * @param namespaceFile The namespace file.
	 * @throws IOException when something goes wrong while writing to file.
	 */
	public void writeNamespaceToFile(File namespaceFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(namespaceFile, true));
		bw.write(id() + "," + name() + "\n");
		bw.close();
	}

	/**
	 * Reads the namespaces from the CSV file recording the namespaces. 
	 * @param namespaceFile The namespace file.
	 * @return A map, where the keys are the namespace names and the values are the corresponding namespaces.
	 * @throws IOException  when something goes wrong while reading the file.
	 */
	public static Map<String, Namespace> getNamespacesFromFile(File namespaceFile) throws IOException {
		Map<String, Namespace> namespaces = new HashMap<String, Namespace>();
		BufferedReader ns = new BufferedReader(new FileReader(namespaceFile));
		String line;
		while( (line = ns.readLine()) != null ) {
			String [] values = line.split(",");
			int id = Integer.parseInt(values[0]);
			String name = values[1];
			namespaces.put(name, new Namespace(id, name));
		}
		ns.close();
		return namespaces;
	}

	/**
	 * Returns the namespace of a given Wikipedia page.
	 * This is the string preceding the colon (:) in a the title, or the empty string if the page
	 * is an article in the main namespace. 
	 * 
	 * @param title The title of a Wikipedia page.
	 * @return The namespace of the page.
	 */
	public static String wikipediaPageNamespace(String title) {
		int colonIndex = title.indexOf(":"); 
		if ( colonIndex < 0 ) // the page belongs to the main namespace (it is an article)
			return "";
		return title.substring(0, colonIndex);
	}

}
