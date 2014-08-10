package com.neo4j.jena.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchRelationship;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class BatchHandler implements ModelChangedListener {

	private int totalTriples = 0;
	private int triplesLastCommit = 0;
	private int addedNodes = 0;
	private int addedLabels = 0;
	private int addedRelationships = 0;

	private int indexCache;
	private int timeout;

	private long tick = System.currentTimeMillis();
	private BatchInserter db;
	private BatchInserterIndex index;
	
	private static final String URI_PROPERTY="__URI__";

	private Map<String, Long> tmpIndex = new HashMap<String, Long>();

	public BatchHandler(BatchInserter db2, int indexCache, int timeout) {
		this.db = db2;
		this.indexCache = indexCache;
		this.timeout = timeout;

		BatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);
		index = indexProvider.nodeIndex("ttlIndex", MapUtil.stringMap("type", "exact"));
		index.setCacheCapacity(URI_PROPERTY, indexCache + 1);

	}

	@Override
	public void addedStatement(Statement st) {
		try {
			Resource subject = st.getSubject();
			Resource predicate = st.getPredicate();
			String predicateName = predicate.getLocalName();

			RDFNode object = st.getObject();

			// Check index for subject
			Long subjectNode = null;
			
		if(subject.isURIResource()){
			subjectNode = tmpIndex.get(subject.toString());
			
			if (subjectNode == null) {
				IndexHits<Long> hits = index.get(NeoGraph.LABEL_URI, subject.toString());
				if (hits.hasNext()) { // node exists
					subjectNode = hits.next();
				} else {
					Map<String, Object> props = new HashMap<String, Object>();
					props.put(NeoGraph.LABEL_URI, subject.toString());
					subjectNode = db.createNode(props);
					Label label = DynamicLabel.label(NeoGraph.LABEL_URI);
					db.setNodeLabels(subjectNode, label);

					tmpIndex.put(subject.toString(), subjectNode);

					index.add(subjectNode, props);
					addedNodes++;
				}
			}
		}
		else if(subject.isAnon()){
			//System.out.println("Blank node : " + subject.asNode().getBlankNodeId().toString());
			subjectNode = tmpIndex.get(subject.asNode().getBlankNodeId().toString());
			if(subjectNode == null){
				IndexHits<Long> hits = index.get(NeoGraph.LABEL_BNODE, subject.asNode().getBlankNodeId().toString());
				if (hits.hasNext()) { // node exists
					subjectNode = hits.next();
				}
				else{
					Label label = DynamicLabel.label(NeoGraph.LABEL_BNODE);
					Map<String, Object> props = new HashMap<String, Object>();
					props.put(NeoGraph.LABEL_URI, subject.asNode().getBlankNodeId().toString());
					subjectNode = db.createNode(props,label);
					tmpIndex.put(subject.asNode().getBlankNodeId().toString(), subjectNode);

					index.add(subjectNode, props);
					addedNodes++;
				}
			}
		}
			
		if (object instanceof Literal) {
				Resource type = (Resource) object.asLiteral().getDatatype();
				Object value;
				if (type == null) // treat as String
					value = object.toString();
				else {
					String localName = type.getLocalName();

					value = object.asLiteral().getValue();
				}
				
				long literal = 0;
				//Map<String, Object> nodeProps = db.getNodeProperties(subjectNode);
				//nodeProps.put(predicateName, value);
				//db.setNodeProperties(subjectNode, nodeProps);
				Label label = DynamicLabel.label(NeoGraph.LABEL_LITERAL);
				Map<String, Object> props = new HashMap<String, Object>();
				props.put(NeoGraph.PROPERTY_VALUE, object.asLiteral().getValue());
				
				if(object.asLiteral().getDatatype()!=null)
					props.put(NeoGraph.PROPERTY_DATATYPE, object.asLiteral().getDatatype());
				
				if(object.asLiteral().getLanguage()!=null)
					props.put(NeoGraph.PROPERTY_LANGUAGE, object.asLiteral().getLanguage());
				
				literal = db.createNode(props);
				db.setNodeLabels(literal, label);
				Long objectNode = literal;

				//tmpIndex.put(object.toString(), literal);

				index.add(literal, props);
				addedNodes++;
				
				getorCreateRelationship(subjectNode, objectNode, predicate);
				

		} 
		else if(object.isURIResource()) { // must be Resource
				// Make sure object exists
				Long objectNode = tmpIndex.get(object.toString());

				if (objectNode == null) {
					IndexHits<Long> hits = index.get(NeoGraph.LABEL_URI, object.toString());
					if (hits.hasNext()) { // node exists
						objectNode = hits.next();
					} else {
						Map<String, Object> props = new HashMap<String, Object>();
						props.put(NeoGraph.LABEL_URI, object.toString());
						objectNode = db.createNode(props);
						Label label = DynamicLabel.label(NeoGraph.LABEL_URI);
						db.setNodeLabels(objectNode, label);

						tmpIndex.put(object.toString(), objectNode);

						index.add(objectNode, props);
						addedNodes++;
					}
				}
				
				getorCreateRelationship(subjectNode, objectNode, predicate);
			}
		else if(object.isAnon()){
			Long objectNode = tmpIndex.get(object.asNode().getBlankNodeId().toString());
			
			if (objectNode == null) {
				IndexHits<Long> hits = index.get(NeoGraph.LABEL_URI, object.toString());
				if (hits.hasNext()) { // node exists
					objectNode = hits.next();
				}
				else{
					Label label = DynamicLabel.label(NeoGraph.LABEL_BNODE);
					Map<String, Object> props = new HashMap<String, Object>();
					props.put(NeoGraph.LABEL_URI, object.asNode().getBlankNodeId().toString());
					objectNode = db.createNode(props,label);
					tmpIndex.put(object.asNode().getBlankNodeId().toString(), objectNode);

					index.add(objectNode, props);
					addedNodes++;
				}
			}
			
			getorCreateRelationship(subjectNode, objectNode, predicate);
		}
			totalTriples++;

			long nodeDelta = totalTriples - triplesLastCommit;
			long timeDelta = (System.currentTimeMillis() - tick) / 1000;

			// periodical flush
			if (nodeDelta >= indexCache || timeDelta >= timeout) {
				index.flush();
				// clear HashMap after flushing
				tmpIndex.clear();
				
				triplesLastCommit = totalTriples;
				System.out.println(totalTriples + " triples, "+addedNodes+" nodes @ ~" + nodeDelta / timeDelta + " triples/second.");
				tick = System.currentTimeMillis();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getorCreateRelationship(Long subjectNode, Long objectNode, Resource predicate){
		// Make sure this relationship is unique
		RelationshipType relType = DynamicRelationshipType.withName(predicate.getURI());
		boolean hit = false;
		for (BatchRelationship rel : db.getRelationships(subjectNode)) {
			if (rel.getEndNode() == (objectNode) && rel.getType().equals(relType)) {
				hit = true;
				break;
			}
		}

		if (!hit) { // Only create relationship, if it didn't exist
			RelationshipType rel = DynamicRelationshipType.withName(predicate.getURI());
			//System.out.println("Relationship: "+ relType);
			db.createRelationship(subjectNode, objectNode, rel, null);
			addedRelationships++;
		}
		
	}

	public int getCountedStatements() {
		return totalTriples;
	}
	
	public int getNodesAdded(){
		return addedNodes;
	}

	@Override
	public void addedStatements(Statement[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addedStatements(List<Statement> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addedStatements(StmtIterator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addedStatements(Model arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyEvent(Model arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removedStatement(Statement arg0) {}

	@Override
	public void removedStatements(Statement[] arg0) {}

	@Override
	public void removedStatements(List<Statement> arg0) { }

	@Override
	public void removedStatements(StmtIterator arg0) { }

	@Override
	public void removedStatements(Model arg0) { }	
}