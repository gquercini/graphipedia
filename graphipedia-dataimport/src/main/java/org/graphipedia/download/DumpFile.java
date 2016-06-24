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
package org.graphipedia.download;


/**
 * A dump file of a Wikipedia edition.
 *
 */
public class DumpFile {
	
	/**
	 * The URL of this dump file.
	 */
	private String url;
	
	/**
	 * The size (in bytes) of this dump file.
	 */
	private double size;
	
	/**
	 * Creates a new {@code DumpFile}.
	 * @param url The URL of the dump file.
	 * @param size THe size of the dump file.
	 */
	public DumpFile(String url, double size) {
		this.url = url;
		this.size = size;
	}
	
	/**
	 * Returns the URL of this dump file.
	 * @return The URL of this dump file.
	 */
	public String url() {
		return this.url;
	}
	
	/**
	 * Returns the size of this dump file.
	 * @return The size of this dump file.
	 */
	public double size() {
		return this.size;
	}

}
