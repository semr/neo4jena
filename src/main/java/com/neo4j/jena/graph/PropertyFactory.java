package com.neo4j.jena.graph;

import java.util.HashMap;
import java.util.Map;


public class PropertyFactory {
	
	private static Map<String, OntologyProperty> properties = new HashMap<String, OntologyProperty>();

	public static OntologyProperty getType(String uri) {
		OntologyProperty type = properties.get(uri);
		if(type==null) {
			type = new OntologyProperty(uri);
			properties.put(uri, type);
		}
		return type;
	}
}
