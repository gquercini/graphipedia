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
package org.graphipedia.wikipedia.parser;

import org.graphipedia.wikipedia.Namespaces;
import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;

/**
 * This class is responsible for cleaning the wikicode of a Wikipedia article
 * in a given language edition.
 *
 */
public class WikiTextCleaner {
	
	/**
	 * The configuration of the wikitext parser.
	 */
	private SimpleWikiConfiguration config;
	
	/**
	 * Used to obtain a text representation of the wikicode of a Wikipedia article.
	 */
	private TextConverter textConverter;
	
	/**
	 * A compiler for Wikipedia articles. It returns a AST representation of a Wikipedia article. 
	 */
	private Compiler compiler;
	
	
	/**
	 * Creates a new {@code WikiTextCleaner}, used to parse Wikipedia articles in language editions other than English.
	 * The list of namespaces in the given language edition is provided to this constructor.
	 * @param ns The namespaces of a Wikipedia language edition.
	 * @throws Exception when something goes wrong while configuring the text parser.
	 */
	public WikiTextCleaner(Namespaces ns) throws Exception {
		this.config = new SimpleWikiConfiguration("classpath:/org/graphipedia/SimpleWikiConfiguration.xml");
		/*for ( Namespace namespace : ns ) {
			int namespaceId = namespace.id();
			String namespaceName = namespace.title();
			String namespaceCanonicalName = namespace.title(); 
			boolean subpages = false;
			boolean isFileNs = namespaceId == 6 ? true: false;
			org.sweble.wikitext.engine.config.Namespace newNamespace = 
					new org.sweble.wikitext.engine.config.Namespace(namespaceId, namespaceName, 
							namespaceCanonicalName, subpages, isFileNs, new ArrayList<String>());
			if( namespaceId == 0 )
				this.config.setDefaultNamespace(newNamespace);
			else
			if( namespaceId == 10 )
				this.config.setTemplateNamespace(newNamespace);
			config.addNamespace(newNamespace);
		}*/
		
		int wrapCol = 80;
		this.compiler = new Compiler(config);
		this.textConverter = new TextConverter(config, wrapCol);
	}
	
	/**
	 * Cleans the wiki text of a Wikipedia article.
	 * @param title The title of the Wikipedia article to clean.
	 * @param id The identifier of the Wikipedia article to clean.
	 * @param text The wiki code of the Wikipedia article to clean.
	 * @return The clean wiki code of the Wikipedia article.
	 * @throws Exception When something goes wrong while parsing the wiki text of the article.
	 */
	public String cleanText(String title, int id, String text) throws Exception {
		System.out.println("cleaning " + title);
		PageTitle pageTitle = PageTitle.make(config, title);
		PageId pageId = new PageId(pageTitle, id);
		CompiledPage cp = null;
		try {
			cp = compiler.postprocess(pageId, text, null);
		} catch (CompilerException e) {
			System.out.println(title + " impossible to parse");
			return "";
		}
		String cleanText = (String)textConverter.go(cp.getPage());
		return cleanText;
	}

}
