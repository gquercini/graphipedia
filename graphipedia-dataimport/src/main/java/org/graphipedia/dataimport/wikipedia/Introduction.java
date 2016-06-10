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

/**
 * The introduction of a Wikipedia page.
 *
 */
public class Introduction {

	/**
	 * The text of this introduction.
	 */
	private String text;

	/**
	 * The offset of the first character of this introduction.
	 */
	private int startIndex;

	/**
	 * The offset of the last character of this introduction.
	 */
	private int endIndex;

	/**
	 * Creates a new {@code Introduction}.
	 * 
	 * @param text The text of the introduction.
	 * @param startIndex The offset of the first character of the introduction.
	 * @param endIndex The offset of the last character of the introduction.
	 */
	public Introduction(String text, int startIndex, int endIndex) {
		this.text = text;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	/**
	 * Returns the text of this introduction.
	 * @return  The text of this introduction.
	 */
	public String text() {
		return this.text;
	}

	/**
	 * The offset of the first character of this introduction.
	 * @return The offset of the first character of this introduction.
	 */
	public int startIndex() {
		return this.startIndex;
	}

	/**
	 * The offset of the last character of this introduction.
	 * @return The offset of the last character of this introduction.
	 */
	public int endIndex() {
		return this.endIndex;
	}



}
