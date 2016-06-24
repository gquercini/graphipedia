package org.graphipedia.dataimport;

/**
 * The attributes of a link in the Neo4j database.
 *
 */
public enum LinkAttribute {
	
	/**
	 * The anchor texts of a link.
	 */
	anchors,
	
	/**
	 * The offset of a link.
	 */
    offset,
    
    /**
     * The rank of a link.
     */
    rank,
    
    /**
     * Whether a link occurs in the infobox of a Wikipedia page.
     */
    infobox,
    
    /**
     * Whether a link occurs in the introduction of a Wikipedia page.
     */
    intro,
    
    /**
     * The number of occurrences of a link in a Wikipedia page.
     */
    occurrences,
    
    /**
     * Whether a link is a disambiguation link (one that leads from a disambiguation page to one of its interpretations).
     */
    disambig
}
