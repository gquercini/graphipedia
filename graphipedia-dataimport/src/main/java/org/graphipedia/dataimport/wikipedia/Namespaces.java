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

import java.util.HashMap;
import java.util.Map;

/**
 * The namespaces of Wikipedia.
 * Wikipedia organizes its pages into namespaces. 
 * Two pages that belong to the same namespace cannot have 
 * the same title.
 * The namespaces are the same across all language editions. The numeric identifier 
 * of a namespace is the same across all language editions.
 * However, the name of a namespace may vary from language to language.
 *
 */
public class Namespaces {
	
	/**
	 * The list of namespaces indexed by their title.
	 */
	private Map<String, Namespace> namespacesByTitle;
	
	/**
	 * The list of namespaces indexed by their identifier.
	 */
	private Map<Integer, Namespace> namespacesById;
	
	/**
	 * Creates a new {@code Namespaces}.
	 */
	public Namespaces() {
		this.namespacesByTitle = new HashMap<String, Namespace>();
		this.namespacesById = new HashMap<Integer, Namespace>();
	}
	
	/**
	 * Adds a new namespace.
	 * @param namespace The namespace to add.
	 */
	public void add(Namespace namespace) {
		this.namespacesById.put(namespace.id(), namespace);
		this.namespacesByTitle.put(namespace.title(), namespace);
	}
	
	/**
	 * Returns the namespace with the given identifier, if any.
	 * @param identifier An identifier. 
	 * @return The namespace with the given identifier, or {@code null} if no namespace corresponds
	 * to the given identifier.
	 */
	public Namespace getNamespaceFromId(int identifier) {
		if (namespacesById.containsKey(identifier) )
			return namespacesById.get(identifier);
		return null;
	}
	
	/**
	 * Returns the namespace with the given title, if any.
	 * @param title The title of a namespace.
	 * @return The namespace with the given title, if any, or {@code null}.
	 */
	public Namespace getNamespaceFromTitle(String title) {
		if (namespacesByTitle.containsKey(title) )
			return namespacesByTitle.get(title);
		return null;
	}
	
	/**
	 * Returns the namespace of a Wikipedia page.
	 * 
	 * @param title The title of a Wikipedia page.
	 * @return The namespace of the page.
	 */
	public Namespace wikipediaPageNamespace(String title) {
		int colonIndex = title.indexOf(":"); 
		if ( colonIndex < 0 ) // the page belongs to the main namespace (it is an article)
			return getNamespaceFromId(Namespace.MAIN);
		String prefix = title.substring(0, colonIndex);
		Namespace namespace = getNamespaceFromTitle(prefix);
		if ( namespace == null ) // An article in the main namespace that happens to have a colon in the title.
			return getNamespaceFromId(Namespace.MAIN);
		return namespace;
	}

}
















