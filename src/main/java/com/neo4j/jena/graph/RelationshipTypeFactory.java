/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 */
package com.neo4j.jena.graph;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.RelationshipType;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * Factory for generating <code>RelationshipType</code>s for ontology properties.
 * Types are not re-created.
 * 
 * @author Khalid Latif (2014-02-12)
 */
public class RelationshipTypeFactory {

	/** In memory store of loaded relationship types */
	private static Map<String, OntologyProperty> types = new HashMap<String, OntologyProperty>();

	/**
	 * Prepares a relationship type from given URI.
	 * 
	 * @return A new relationship type of an existing instance of already created.
	 */
	public static RelationshipType getType(String uri) {
		OntologyProperty type = types.get(uri);
		if(type==null) {
			type = new OntologyProperty(uri);
			types.put(uri, type);
		}
		return type;
	}
	
	/**
	 * Prepares a relationship type from given RDF property.
	 * 
	 * @see #getType(String)
	 */
	public static RelationshipType getType(Property p) {
		return getType(p.getURI());
	}
}