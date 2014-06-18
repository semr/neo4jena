package com.neo4j.jena.graph;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.index.UniqueFactory;

import com.hp.hpl.jena.graph.Graph;
import com.neo4j.jena.bench.StopWatch;


/**
 * Jena graph wrapper for Neo4J graph database service.
 * 
 * @author Khalid Latif, Mahek Hanfi (2014-02-14)
 */

public class UniqueNodeFactory extends UniqueFactory.UniqueNodeFactory {
	
	/** Holds nodes in memory during bulk loading */
	private Map<com.hp.hpl.jena.graph.Node, Node> bulkNodes;
	
	/** Reference to Jena Graph */
	private final Graph graph;

	/**
	 * Initialize the factory.
	 * 
	 * @param A GraphDatabaseService instance to store newly create node.
	 * @param A Jena graph to process new node creation.
	 */
	public UniqueNodeFactory(GraphDatabaseService graphdb, Graph graph) {
		super(graphdb, "Resources");
		this.graph = graph;
	}

	/**
	 * Initializes the node.
	 */
	@Override
	protected void initialize(Node created, Map<String, Object> properties) {
		created.addLabel(DynamicLabel.label(NeoGraph.LABEL_URI));
		created.setProperty(NeoGraph.PROPERTY_URI, properties.get(NeoGraph.PROPERTY_URI));
	}
	
	/**
	 * Get an existing node or return null.
	 */
	public Node get(com.hp.hpl.jena.graph.Node node) {
		Node neoNode = null;
		
		//StopWatch watch = new StopWatch();
		
		if(bulkNodes!=null) {
			neoNode = bulkNodes.get(node);
			if(neoNode!=null) return neoNode;
		}
		if(node.isLiteral()) {
			Label label = DynamicLabel.label(NeoGraph.LABEL_LITERAL);
			try ( ResourceIterator<org.neo4j.graphdb.Node> nodes = super.graphDatabase().findNodesByLabelAndProperty( label, NeoGraph.PROPERTY_VALUE, node.getLiteralValue()).iterator() ) {
	            if ( nodes.hasNext() ) {
	            	neoNode = nodes.next();
	            }
	        }
		} else if(node.isBlank()) {
			Label label = DynamicLabel.label(NeoGraph.LABEL_BNODE);
			//FIXME Blank node id might not be unique across multiple loads of same RDF data
			try ( ResourceIterator<org.neo4j.graphdb.Node> nodes = super.graphDatabase().findNodesByLabelAndProperty( label, NeoGraph.PROPERTY_VALUE, node.getBlankNodeId().toString()).iterator() ) {
	            if ( nodes.hasNext() ) {
	            	neoNode = nodes.next();
	            }
	        }
		} else {
			Label label = DynamicLabel.label(NeoGraph.LABEL_URI);
			String prefixed = graph.getPrefixMapping().shortForm(node.getURI());
			try ( ResourceIterator<org.neo4j.graphdb.Node> nodes = super.graphDatabase().findNodesByLabelAndProperty( label, NeoGraph.PROPERTY_URI, prefixed).iterator() ) {
	            if ( nodes.hasNext() ) {
	            	neoNode = nodes.next();
	            }
	        }
		}
		
		//System.out.println("Get " + node + " took: " + watch.stop());
		if(neoNode!=null && bulkNodes!=null)
			bulkNodes.put(node, neoNode);
		return neoNode;
	}
	
	/**
	 * Get or create a Neo4J node for a given Jena node object.
	 * 
	 * @param Jena node (either resource or a literal)
	 * @return Neo4J node
	 */
	public Node getOrCreate(com.hp.hpl.jena.graph.Node node) {
		Node created;
		
		if(bulkNodes!=null) {
			Node neoNode = bulkNodes.get(node);
			if(neoNode!=null){
				//System.out.println(node + " already exist");
				return neoNode;
			}
		}
		else
			System.out.println("Bulk node is null");
		
		//StopWatch watch = new StopWatch();
		
		// Literals get special treatment (duplicates allowed)
		if(node.isLiteral()) {
			Label label = DynamicLabel.label(NeoGraph.LABEL_LITERAL);
			created = super.graphDatabase().createNode(label);
			created.setProperty(NeoGraph.PROPERTY_VALUE, node.getLiteralValue().toString());
			// Add data-type tag
			if(node.getLiteralDatatype()!=null)
				created.setProperty(NeoGraph.PROPERTY_DATATYPE, node.getLiteralDatatype().toString());
			// Add language tag
			if(node.getLiteralLanguage()!=null)
				created.setProperty(NeoGraph.PROPERTY_LANGUAGE, node.getLiteralLanguage());
			return created;
		} else if(node.isBlank()) {
			// FIXME Handle blank node
				Label label = DynamicLabel.label(NeoGraph.LABEL_BNODE);
				created = super.graphDatabase().createNode(label);
				created.setProperty(NeoGraph.PROPERTY_URI, node.getBlankNodeId().toString());
			
		} else {
			String prefixed = graph.getPrefixMapping().shortForm(node.getURI());
			// Back to normal procedure for resources
			created = getOrCreate(NeoGraph.PROPERTY_URI, prefixed);
		}
		
		//System.out.println("Get or create " + node + " took:" + watch.stop());
		
		if(bulkNodes!=null)
			bulkNodes.put(node,  created);
		return created;
	}
	
	public void startBulkLoad() {
		bulkNodes = new HashMap<com.hp.hpl.jena.graph.Node, Node>();
	}
	
	public void stopBulkLoad() {
		bulkNodes = null;
	}
}