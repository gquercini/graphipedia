package org.graphipedia.dataimport.neo4j;

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
    occurrences
}
