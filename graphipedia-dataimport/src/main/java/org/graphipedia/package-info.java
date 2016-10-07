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
/**
 * 
 * Graphipedia is a Java program designed to transform Wikipedia, a <a href="https://www.wikipedia.org">free online encylopedia</a>, 
 * into a graph. <br>
 * Wikipedia consists of several language editions; as of September 2016, 
 * there are 283 active language editions.
 * Any Wikipedia language edition consists of a set of <b>articles</b>, 
 * each describing in the language of the edition a specific <i>subject</i>, such as 
 * a city (e.g., <i>New York City</i>), a person (e.g., <i>Denis Diderot</i>), a tool (e.g., <i>hammer</i>) or any other
 * notion and concept related to human knowledge. 
 * Any article has a <i>title</i>, that is the name of the subject it describes, and a <i>text</i>, that is the description
 * itself. 
 * The text is usually organized into one or several <i>sections</i> that help the reader find the information they need
 * more easily; the first section is referred to as the <i>introduction</i> of the article and is always present. 
 * Optionally, the content of an article can be summarized in a <i>infobox</i>, that is a table containing a set
 * of attribute-value pairs that highlight some of the most important information conveyed by the article
 * itself about a subject. 
 * For instance, the infobox of an article that describes a city will typically have attributes such as ``country'', 
 * ``population'' and ``time zone''. 
 * If an article describes a spatial (i.e., geographic) entity, the geographi coordinates of the entity itself are provided.
 * 
 *        
 * 
 * 
 * and written 
 * in the    
 * 
 * 
 * 
 * Graphipedia automatically downloads the necessary dump files of selected Wikipedia editions and imports 
 * them to a Neo4j database.
 * The data is downloaded to directory {@code ROOT_DIR}, that is created by Graphipedia itself.
 * Note that if this directory already exists (from a previous computation) Graphipedia will flag an error and 
 * will stop.  
 */

package org.graphipedia;