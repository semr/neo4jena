/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 */
package com.neo4j.jena.bench;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.neo4j.jena.graph.NeoGraph;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

/**
 * * Test Class for Wine dataset.
 * 
 * @author Khalid Latif, Mahek Hanfi
 */
public class Wine {
	private static final String NEO_STORE = "YOUR_DATASTORE_PATH";
	
	private static final String inputFileName = "wine.owl" ;

	public static void main(String[] args) {
		GraphDatabaseService njgraph = new GraphDatabaseFactory().newEmbeddedDatabase(NEO_STORE);
		
		Wine.write(njgraph);
		Wine.search(njgraph);
		njgraph.shutdown();
	}
	
	public static void search(GraphDatabaseService njgraph) {
		NeoGraph graph = new NeoGraph(njgraph);
		Model njmodel = ModelFactory.createModelForGraph(graph);
		      
		String s2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
					"PREFIX food: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/food#>"+
					"PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>"+
					"SELECT ?X WHERE {"+
					"?X food:SweetFruit ?Z . }";

        Query query = QueryFactory.create(s2);
        QueryExecution qExe = QueryExecutionFactory.create(query, njmodel);
        StopWatch watch = new StopWatch();
        ResultSet results = qExe.execSelect();
        System.out.println("Query took (ms): "+ watch.stop());
        //ResultSetFormatter.out(System.out, results);
        
        int count=0;
        while(results.hasNext()){
        	//System.out.println("in while"+count);
        	QuerySolution sol = results.next();
        	System.out.print(sol.get("X"));
        	count++;
        }
       System.out.println("Record fetched:"+ count);
       
	}
	
	public static void ensureIndex(GraphDatabaseService njgraph) {
		IndexDefinition indexDefinition;
        try ( Transaction tx = njgraph.beginTx() ) {
            Schema schema = njgraph.schema();
            indexDefinition = schema.indexFor( DynamicLabel.label( NeoGraph.LABEL_URI ) )
                    .on( NeoGraph.PROPERTY_URI )
                    .create();
            tx.success();
            System.out.println( "Index definition" );
        }
        try ( Transaction tx = njgraph.beginTx() ) {
            Schema schema = njgraph.schema();
            schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
            System.out.println( "Index loading" );
        }
	}
	
	public static void write(GraphDatabaseService njgraph) {
		InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
		Model model = ModelFactory.createDefaultModel();
        model.read(in,"","RDF");
        double triples = model.size();
        System.out.println("Model loaded with " +  triples + " triples");
        
		NeoGraph graph = new NeoGraph(njgraph);
		Model njmodel = ModelFactory.createModelForGraph(graph);
		graph.startBulkLoad();
		System.out.println("NeoGraph Model initiated");
		StopWatch watch = new StopWatch();
		//log.info(njmodel.add(model));
		njmodel.add(model);
		System.out.println("Storing completed (ms): " + watch.stop());
		graph.stopBulkLoad();
	}
	
}
