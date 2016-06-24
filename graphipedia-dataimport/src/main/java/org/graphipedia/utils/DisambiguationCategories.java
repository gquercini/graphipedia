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
package org.graphipedia.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.wikipedia.Wiki;

/**
 * Utility class that is used to get all the root categories of 
 * the disambiguation pages across all Wikipedia language editions.
 * This class queries the MediaWiki API to get the list.
 * Basically, all the categories that are connected through a cross-language link 
 * to the English category "Category:Disambiguation pages" are obtained.
 * 
 *  The root categories are stored in a CSV file.
 *  Each line contains the code of the language of a Wikipedia edition 
 *  and the title of the root category in that edition. The two values 
 *  are separated by a tab character.
 *  
 *  There is no need to run this class, unless Wikipedia changes the names of these categories, which is not
 *  likely to happen often.
 *  
 *  For this reason, the CSV file is added as a resource to this project.
 *
 */
public class DisambiguationCategories {

	/**
	 * Entry point of the program.
	 * @param args Command-line arguments (none)
	 * @throws IOException with I/O errors.
	 */
	public static void main(String[] args) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("dp-root-categories.csv"));
		Wiki wiki = new Wiki("en.wikipedia.org");
		Map<String, String> clLinks = wiki.getInterWikiLinks("Category:Disambiguation pages");
		for ( Entry<String, String> link : clLinks.entrySet() )
			bw.write(link.getKey() + "\t" + link.getValue() + "\n");
		bw.close();
	}

}
