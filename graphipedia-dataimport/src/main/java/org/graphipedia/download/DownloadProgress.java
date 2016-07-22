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

import java.util.logging.Logger;

/**
 * Displays the progress of the download of a file.
 *
 */
public class DownloadProgress {
	/** 
	 * The maximum value corresponding to a 100% progress.
	 */
	private double maxValue;

	/**
	 * The current progress of a task (percent).
	 */
	private double progress;

	/**
	 * Creates a new {@code ProgressBar}, initialized to 0%.
	 * 
	 * @param maxValue The maximum value, corresponding to a
	 * 100% progress.
	 */
	public DownloadProgress(double maxValue) {
		this.maxValue = maxValue;
		this.progress = 0.;
	}

	/**
	 * Visualizes the progress with the given logger.
	 * @param currentValue The current progress (percent).
	 * @param logger The logger used to visualize the progress.
	 */
	public void visualize(double currentValue, Logger logger) {
		double currentProgress = Math.floor(currentValue / maxValue * 100.);
		if (currentProgress > progress) {
			progress = currentProgress;
			logger.info(progress + "% Complete");
		}
	}
	
	/**
	 * Displays a progress bar, based on the current value.
	 * 
	 * @param currentValue The current progress (percent). 
	 */
	public void visualize(double currentValue) {
		if ( currentValue == 0 )
			System.out.println();
		double currentProgress = Math.floor(currentValue / maxValue * 100.);

		if (currentProgress > progress) {
			progress = currentProgress;
			for( int k = 0; k < progress; k += 5 )
				System.out.print("|");

			System.out.print(" <-- " + progress + "% Complete --> \r");
		}
	}
}
