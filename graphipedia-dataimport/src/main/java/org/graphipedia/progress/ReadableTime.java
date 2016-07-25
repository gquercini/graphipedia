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
package org.graphipedia.progress;


/**
 * Utility class to convert a time duration in milliseconds to a readable form (in terms of number of minutes, seconds...)
 *
 */
public class ReadableTime {
	
	/**
	 * Milliseconds in one second.
	 */
	private static final long ONE_SEC = 1000;
	
	/**
	 * Milliseconds in one minute
	 */
	private static final long ONE_MIN = ONE_SEC * 60L;
	
	/**
	 * Milliseconds in one hour.
	 */
	private static final long ONE_HOUR = ONE_MIN * 60L;
	
	/**
	 * Milliseconds in one day.
	 */
	private static final long ONE_DAY = ONE_HOUR * 24L;
	
	/**
	 * Milliseconds in one year.
	 */
	private static final long ONE_YEAR = ONE_DAY * 365L;
	 
	/**
	 * Returns a readable time duration.
	 * @param millis The duration in milliseconds.
	 * @return A readable time duration.
	 */
	public static String readableTime(long millis) {
		long milliseconds = millis % 1000L;
		long seconds = (millis / ONE_SEC) % 60L ;
		long minutes = (millis / ONE_MIN ) % 60L;
		long hours = (millis / ONE_HOUR) % 24L;
		long days = (millis / ONE_DAY) % 365L;
		long years = (millis / ONE_YEAR);
		
		long[] time = new long[]{years, days, hours, minutes, seconds, milliseconds};
		String[] unit = new String[]{"y", "d", "h", "m", "s", "ms"};
		String rt = "";
		for ( int i = 0; i < time.length; i += 1 ) {
			if ( time[i] == 0 )
				continue;
			rt += rt.length() == 0 ? "0 ms" + time[i] + unit[i] : ", " + time[i] + unit[i]; 
		}
		return rt;
	}

}
