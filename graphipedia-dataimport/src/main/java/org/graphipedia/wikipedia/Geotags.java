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
 * The geotags associated to Wikipedia pages that 
 * describe spatial entities.
 *
 */
public class Geotags {
	
	/**
	 * The globe of the spatial entity (earth, moon...).
	 */
	private String globe;
	
	/**
	 * The latitude of the spatial entity.
	 */
	private double latitude;
	
	/**
	 * The longitude of the spatial entity.
	 */
	private double longitude;
	
	/**
	 * The type of the spatial entity.
	 */
	private String type;
	
	/**
	 * Creates a new {@code Geotags} relative to a spatial entity.
	 * @param globe  The globe of the spatial entity.
	 * @param latitude The latitude of the spatial entity.
	 * @param longitude The longitude of the spatial entity.
	 * @param type The type of the spatial entity.
	 */
	public Geotags(String globe, double latitude, double longitude, String type) {
		this.globe = globe;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
	}
	
	/**
	 * Creates a new {@code SpatialMetadata} relative to a spatial entity.
	 * @param latitude The latitude of the spatial entity.
	 * @param longitude The longitude of the spatial entity.
	 * @param type The type of the spatial entity.
	 */
	public Geotags(double latitude, double longitude, String type) {
		this(null, latitude, longitude, type);
	}
	
	/**
	 * Creates a new {@code SpatialMetadata} relative to a spatial entity.
	 * @param globe  The globe of the spatial entity.
	 * @param latitude The latitude of the spatial entity.
	 * @param longitude The longitude of the spatial entity.
	 */
	public Geotags(String globe, double latitude, double longitude) {
		this(globe, latitude, longitude, null);
		
	}
	
	/**
	 * Creates a new {@code SpatialMetadata} relative to a spatial entity.
	 * @param latitude The latitude of the spatial entity.
	 * @param longitude The longitude of the spatial entity.
	 */
	public Geotags(double latitude, double longitude) {
		this(null, latitude, longitude, null);
	}
	
	/**
	 * Returns the globe of the spatial entity.
	 * @return The globe of the spatial entity.
	 */
	public String globe() {
		return globe;
	}
	
	/**
	 * Returns the latitude of the spatial entity.
	 * @return The latitude of the spatial entity.
	 */
	public double latitude() {
		return latitude;
	}
	
	/**
	 * Returns the longitude of the spatial entity.
	 * @return The longitude of the spatial entity.
	 */
	public double longitude() {
		return longitude;
	}
	
	/**
	 * Returns the type of the spatial entity.
	 * @return The type of the spatial entity.
	 */
	public String type() {
		return type;
	}
}
