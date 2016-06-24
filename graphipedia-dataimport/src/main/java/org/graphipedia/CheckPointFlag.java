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

package org.graphipedia;


/**
 * Enumerates all the checkpoint flags.
 *
 */
public enum CheckPointFlag {
	/**
	 * Indicates a successful file download.
	 */
	fileDownload,
	
	/**
	 * Indicates a successful download of a Wikipedia edition.
	 */
	editionDownload,
	
	/**
	 * Indicates that the disambiguation pages have already been obtained from 
	 * Wikipedia.
	 */
	disambigExtracted,
	
	/**
	 * Indicates that the infobox templates have already been obtained from 
	 * Wikipedia.
	 */
	infoboxExtracted,
	
	/**
	 * Indicates that the links have been extracted from the XML file 
	 * of a Wikipedia edition.
	 */
	linksExtracted,
	
	/**
	 * Indicates that the cross-links have been extracted from the dump file 
	 * of a Wikipedia edition.
	 */
	crosslinksExtracted
	
	
}
