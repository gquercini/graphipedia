//
// Copyright (c) 2012 Mirko Nasato
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
package org.graphipedia.dataimport;

import java.util.logging.Logger;

/**
 * Tracks the progress of a task.
 *
 */
public class ProgressCounter {
	
	/**
	 * The logger where the progress is recorded.
	 */
	private Logger logger;

	/**
	 * Constant for thousand. 
	 */
    private static final int THOUSAND = 1000;
    //private static final int SMALL_STEP = 1 * THOUSAND;
    
    /**
     * Constant for the step of the progress.
     */
    private static final int BIG_STEP = 50 * THOUSAND;

    /**
     * Raw count.
     */
    private int count = 0;
    
    /**
     * Creates a new {@code ProgressCounter}.
     * @param logger The logger where the progress is recorded.
     */
    public ProgressCounter(Logger logger) {
    	this.logger = logger;
    }

    /**
     * Returns the raw count.
     * @return The raw count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Increments the raw count and records the progress to the logger
     * if enough progress is made. 
     * @param message A message to display along with the progress.
     */
    public void increment(String message) {
        count++;
        if (count % BIG_STEP == 0) 
            logger.info(message + " " + count / THOUSAND +"k");
    }

}
