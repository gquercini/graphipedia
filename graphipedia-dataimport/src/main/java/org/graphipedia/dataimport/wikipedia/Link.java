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

import java.util.HashSet;
import java.util.Set;

/**
 * A link between two Wikipedia pages.
 * The link is directed from source page to a target page.
 */
public class Link {
	
	/**
	 * The title of the source page.
	 */
	private String sourceTitle;
	
	/**
	 * The title of the target page.
	 */
	private String targetTitle;
	
	/**
	 * The anchor texts associated with this link, if any.
	 */
	private Set<String> anchors;
	
	/**
	 * The offset of the first occurrence of this link in the source page.
	 * The offset is the number of words before the first occurrence of this link.
	 */
	private int offset;
	
	/**
	 * The rank of the first occurrence of this link in the source page.
	 * The rank is the number of links occurring before this link in the source page.
	 */
	private int rank;
	
	/**
	 * Whether this link occurs in the infobox of the source page.
	 */
	private boolean infobox;
	
	/**
	 * Whether this link occurs in the introduction of the source page.
	 */
	private boolean intro;
	
	/**
	 * The number of occurrences of this link in the source page.
	 */
	private int occurrences; 

	/**
	 * Creates a new {@code Link} from a source page to a target page.
	 * 
	 * @param sourceTitle The title of the source page.
	 * @param targetTitle The title of the target page.
	 * @param offset The offset of the link. The offset is defined as the number of characters occurring before the link
	 * in the source page.
	 * @param rank The rank of the link. The rank is the number of links occurring before the link in the source page.
	 * @param infobox Whether the link occurs in the infobox of the source page.
	 * @param intro Whether the link occurs in the introduction of the source page.
	 */
	public Link(String sourceTitle, String targetTitle, int offset, int rank, boolean infobox, boolean intro) {
		this.sourceTitle = sourceTitle;
		this.targetTitle = targetTitle;
		this.anchors = new HashSet<String>();
		this.offset = offset;
		this.rank = rank;
		this.infobox = infobox;
		this.intro = intro;
		this.occurrences = 1;
	}
	
	/**
	 * Returns the title of the source page.
	 * @return The title of the source page.
	 */
	public String sourceTitle() {
		return this.sourceTitle;
	}
	
	/**
	 * Returns the title of the target page.
	 * @return The title of the target page.
	 */
	public String targetTitle() {
		return this.targetTitle;
	}
	
	
	/**
	 * Returns the anchor texts associated to this link.
	 * @return The anchor texts associated to this link.
	 */
	public Set<String> anchors() {
		return this.anchors;
	}
	
	/**
	 * Adds an anchor text to this link.
	 * 
	 * @param anchor The new anchor to associate to this link
	 */
	public void addAnchor(String anchor) {
		this.anchors.add(anchor);
	}
	
	/**
	 * Returns the offset of the first occurrence of this link. 
	 *  The offset is defined as the number of characters occurring before the link
	 * in the source page.
	 * 
	 * @return The offset of the first occurrence of this link.
	 */
	public int offset() {
		return this.offset;
	}
	
	/**
	 * Returns the rank of the first occurrence of this link.
	 * The rank is the number of links occurring before the link in the source page.
	 * 
	 * @return The rank of the first occurrence of this link.
	 */
	public int rank() {
		return this.rank;
	}
	
	/**
	 * Returns whether this link occurs in the infobox of the source page.
	 * @return {@code true} if this link occurs in the infobox of the source page, {@code false} otherwise.
	 */
	public boolean infobox() {
		return this.infobox;
	}
	
	/**
	 * Sets this link as occurring or not in the infobox.
	 * @param infobox Set to {@code true} if this link occurs in the infobox, {@code false} otherwise.
	 */
	public void infobox(boolean infobox) {
		this.infobox = infobox;
	}
	
	/**
	 * Sets this link as occurring or not in the introduction.
	 * @param intro Set to {@code true} if this link occurs in the introduction, {@code false} otherwise.
	 */
	public void intro(boolean intro) {
		this.intro = intro;
	}
	
	/**
	 * Returns whether this link occurs in the introduction of the source page.
	 * 
	 * @return {@code true} if this link occurs in the introduction of the source page, {@code false} otherwise. 
	 */
	public boolean intro() {
		return this.intro;
	}
	
	/**
	 * Adds an occurrence of this link.
	 */
	public void addOccurrence() {
		this.occurrences += 1;
	}
	
	/**
	 * Returns the number of occurrences of this link.
	 * @return The number of occurrences of this link.
	 */
	public int occurrences() {
		return this.occurrences;
	}
	
	@Override
	public int hashCode() {
		return this.targetTitle.hashCode();
	}
	
	@Override
	public boolean equals(Object link) {
		if (!(link instanceof Link))
			return false;
		
		return ((Link)link).targetTitle.equals(this.targetTitle);
	}


}
