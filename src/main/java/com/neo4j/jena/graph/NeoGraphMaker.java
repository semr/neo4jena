/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 */
package com.neo4j.jena.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class NeoGraphMaker implements GraphMaker {
	
	NeoGraph neoGraph;
	
	public Graph addDescription(Graph arg0, Node arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		neoGraph.close();
	}

	@Override
	public Graph createGraph() {
		// TODO Auto-generated method stub
		return neoGraph;
	}

	@Override
	public Graph createGraph(String arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public Graph createGraph(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Graph getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Graph getDescription(Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReificationStyle getReificationStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasGraph(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExtendedIterator listGraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph openGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph openGraph(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph openGraph(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeGraph(String arg0) {
		// TODO Auto-generated method stub	
	}
}