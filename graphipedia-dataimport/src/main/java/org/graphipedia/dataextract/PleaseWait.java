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
package org.graphipedia.dataextract;

import java.util.TimerTask;
import java.util.logging.Logger;



/**
 * This class is used when Graphipedia is downloading the disambiguation pages
 * and the infobox templates from Wikipedia, using the MediaWiki API, which might take a while.
 *
 */
public class PleaseWait extends TimerTask {
	
	/**
	 * The logger of Graphipedia
	 */
	private Logger logger;
	
	/**
	 * Details about the progress of the task that takes a while.
	 */
	private String details;
	
	/**
	 * Constructor.
	 * @param logger The logger of Graphipedia.
	 */
	public PleaseWait(Logger logger) {
		this.logger = logger;
		this.details = "";
	}

	@Override
	public void run() {
		if ( details.length() == 0 )
			logger.info("Still working");
		else
			logger.info("Still working, " + details);
	}
	
	/**
	 * Adds some details to the progress of the task.
	 * @param details The details to display.
	 */
	public void addDetails(String details) {
		this.details = details;
	}

}
