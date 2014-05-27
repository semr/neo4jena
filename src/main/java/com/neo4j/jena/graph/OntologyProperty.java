/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 */
package com.neo4j.jena.graph;

import org.neo4j.graphdb.RelationshipType;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * Ontology/RDF properties as Neo4J relationship types
 * 
 * @author Khalid Latif (2014-02-12)
 */
public class OntologyProperty implements RelationshipType {
	
	/** URI of the property */
	private final String uri;
	
	/**
	 * Initialize the ontology property from given URI
	 */
	public OntologyProperty(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Initialize the onotlogy property from given RDF property.
	 * 
	 * {@link #OntologyProperty(String)}
	 */
	public OntologyProperty(Property p) {
		this(p.getURI());
	}

	@Override
	public String name() {
		return uri;
	}
}