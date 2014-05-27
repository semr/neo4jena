package com.neo4j.jena.graph;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * Jena graph wrapper for Neo4J graph database service.
 * 
 * @author Khalid Latif, Mahek Hanfi (2014-02-14)
 */

public class UniqueRelationshipFactory {
	
	//private final GraphDatabaseService graphdb;
	
	public UniqueRelationshipFactory(GraphDatabaseService graphdb) {
		//this.graphdb = graphdb;
	}
	
	/**
	 * Gets an existing relationship of specified type between subject and object node
	 * or creates one.
	 */
	public Relationship getOrCreate(Node subject, String predicate, Node object) {
		return getOrCreate(subject, RelationshipTypeFactory.getType(predicate), object);
	}
	
	/**
	 * Gets an existing relationship of specified type between subject and object node
	 * or creates one.
	 */
	public Relationship getOrCreate(Node subject, RelationshipType type, Node object) {
		Relationship r = get(subject, type, object);
		if(r==null)
			r = create(subject, type, object);
		return r;
	}
	
	/**
	 * Creates a relationship of given type between subject and object nodes.
	 * This method is not idempotent and would create duplicate relationships
	 * if called repeatedly. 
	 */
	public Relationship create(Node subject, RelationshipType type, Node object) {
		Relationship relation = subject.createRelationshipTo(object, type);
		relation.setProperty(NeoGraph.PROPERTY_URI, type.name());
		return relation;
	}
	
	/**
	 * Finds relationship of given type (specified as URI) between subject and object nodes.
	 * 
	 * @return Relationship if exists or null.
	 */
	public Relationship get(Node subject, String predicate, Node object) {
		return get(subject,RelationshipTypeFactory.getType(predicate), object);
	}
	
	/**
	 * Finds relationship of given type between subject and object nodes.
	 * 
	 * @return Relationship if exists or null.
	 */
	public Relationship get(Node subject, RelationshipType type, Node object) {
		try {
			// FIXME Use relationship index instead of iterating over all relationships
			Iterable<Relationship> relations = subject.getRelationships(Direction.OUTGOING, type);
			for(Relationship relation: relations) {
				org.neo4j.graphdb.Node target = relation.getEndNode();
				// Match object with target node in the existing triple
				Iterable<Label> labels = object.getLabels();
				for(Label label:labels) {
					if(label.name().equals(NeoGraph.LABEL_LITERAL)) {
						// Match literal value of object and target in existing triple
						if(object.getProperty(NeoGraph.PROPERTY_VALUE).equals(target.getProperty(NeoGraph.PROPERTY_VALUE)))
							return relation;
						else return null;
					}
				}
				// Now match URI of object and target in existing triple
				// FIXME Blank Nodes?
				if(object.getProperty(NeoGraph.PROPERTY_URI).equals(target.getProperty(NeoGraph.PROPERTY_URI)))
					return relation;
			}
		} catch(RuntimeException exception) { }
		return null;
	}
}