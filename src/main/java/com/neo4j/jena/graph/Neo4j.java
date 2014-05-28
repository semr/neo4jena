package com.neo4j.jena.graph;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Jena graph wrapper for Neo4J graph database service.
 * 
 * @author Khalid Latif, Mahek Hanfi (2014-02-14)
 */

public class Neo4j implements Graph {

	@Override
	public void add(Triple arg0) throws AddDeniedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Triple arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Node arg0, Node arg1, Node arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete(Triple arg0) throws DeleteDeniedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dependsOn(Graph arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExtendedIterator<Triple> find(TripleMatch arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtendedIterator<Triple> find(Node arg0, Node arg1, Node arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public BulkUpdateHandler getBulkUpdateHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphEventManager getEventManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrefixMapping getPrefixMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphStatisticsHandler getStatisticsHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionHandler getTransactionHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIsomorphicWith(Graph arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove(Node arg0, Node arg1, Node arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}


}
