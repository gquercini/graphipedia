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

/**
 * The infobox of a Wikipedia page.
 *
 */
public class Infobox {
	
	/**
	 * The text of the infobox.
	 */
	private String text;
	
	/**
	 * The offset of the first character of this infobox.
	 */
	private int startIndex;
	
	/**
	 * The offset of the last character of this infobox.
	 */
	private int endIndex;
	
	/**
	 * Creates a new {@code Infobox}.
	 * 
	 * @param text The text of the infobox.
	 * @param startIndex The offset of the first character of the infobox.
	 * @param endIndex The offset of the last character of the infobox.
	 */
	public Infobox(String text, int startIndex, int endIndex) {
		this.text = text;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	/**
	 * Returns the text of this infobox.
	 * @return  The text of this infobox.
	 */
	public String text() {
		return this.text;
	}
	
	/**
	 * The offset of the first character of this infobox.
	 * @return The offset of the first character of this infobox.
	 */
	public int startIndex() {
		return this.startIndex;
	}
	
	/**
	 * The offset of the last character of this infobox.
	 * @return The offset of the last character of this infobox.
	 */
	public int endIndex() {
		return this.endIndex;
	}


}
