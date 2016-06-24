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
 * A link between a disambiguation page and another page (possibly, another disambiguation page) 
 * that provides a possible interpretation of the word which the disambiguation page is about.
 *
 */
public class DisambiguationLink extends Link {

	/**
	 * Creates a new disambiguation link.
	 * @param sourceTitle The title of the source page.
	 * @param targetTitle The title of the target page.
	 * @param offset The offset of the link  in the source page.
	 * @param rank The rank of the links in the source page.
	 * @param infobox Whether the link occurs in the infobox of the source page.
	 * @param intro Whether the link occurs in the introduction of the source page.
	 */
	public DisambiguationLink(String sourceTitle, String targetTitle, int offset, int rank, boolean infobox,
			boolean intro) {
		super(sourceTitle, targetTitle, offset, rank, infobox, intro);
	}

	@Override
	public boolean isRegularLink() {
		return false;
	}

}
