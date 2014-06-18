package com.neo4j.jena.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Prefix Mapping
 * 
 * @author Khalid Latif (2014-03-10)
 */


public class NeoPrefixMapping implements PrefixMapping {
	
	/** Neo4J node */
	private Node delegate;
	private PrefixMapping proxy = PrefixMapping.Factory.create();
	
	/**
	 * Initializes this node.
	 */
	public NeoPrefixMapping(Node node) {
		this(node, new HashMap<String, String>());
	}

	public NeoPrefixMapping(Node node, PrefixMapping pmap) {
		this(node, pmap.getNsPrefixMap());
	}
	
	public NeoPrefixMapping(Node node, Map<String, String> map) {
		this.delegate = node;
		if(map!=null)
			setNsPrefixes(map);
	}	

	/**
	 * Set the prefix
	 */
	@Override
	public PrefixMapping setNsPrefix(String prefix, String uri) {
		proxy.setNsPrefix(prefix, uri);
		//delegate.setProperty(prefix, uri);
		return this;
	}

	/**
	 * Remove the prefix
	 */
	@Override
	public PrefixMapping removeNsPrefix(String prefix) {
		//delegate.removeProperty(prefix);
		proxy.removeNsPrefix(prefix);
		return this;
	}

	/**
	 * Set the prefix
	 */
	@Override
	public PrefixMapping setNsPrefixes(PrefixMapping other) {
		return setNsPrefixes(other.getNsPrefixMap());
	}

	@Override
	public PrefixMapping setNsPrefixes(Map<String, String> map) {
		for(String prefix: map.keySet()) {
			setNsPrefix(prefix, map.get(prefix));
		}
		return this;
	}

	@Override
	public PrefixMapping withDefaultMappings(PrefixMapping map) {
		/*(Map<String, String> copy = map.getNsPrefixMap();
		for(String prefix: delegate.getPropertyKeys()) {
			copy.remove(prefix);
		}
		return setNsPrefixes(copy);*/
		return proxy.withDefaultMappings(map);
	}

	/**
	 * Get the URI of given prefix
	 */
	@Override
	public String getNsPrefixURI(String prefix) {
		//return (String)delegate.getProperty(prefix);
		return proxy.getNsPrefixURI(prefix);
	}

	/**
	 *  Get the prefix of given uri
	 */
	@Override
	public String getNsURIPrefix(String uri) {
		return proxy.getNsURIPrefix(uri);
		/*for(String prefix:delegate.getPropertyKeys()) {
			if(uri.equals(delegate.getProperty(prefix)))
				return prefix;
		}
		return null;*/
	}

	/**
	 *  Get the prefix map
	 */
	@Override
	public Map<String, String> getNsPrefixMap() {
		/*Map<String, String> map = new HashMap<String, String>();
		for(String prefix: delegate.getPropertyKeys()) {
			map.put(prefix, delegate.getProperty(prefix).toString());
		}
		return map;*/
		return proxy.getNsPrefixMap();
	}

	@Override
	public String expandPrefix(String prefixed) {
		int index = prefixed.indexOf(':');
		if(index==-1)
			return prefixed;
		String uri = getNsPrefixURI(prefixed.substring(0,index+1));
		System.out.println(prefixed+"<>"+index+"<>"+uri+"<>"+prefixed.substring(index+1));
		return uri + prefixed.substring(index+1);
	}

	@Override
	public String shortForm(String uri) {
		/*int index = uri.lastIndexOf('#');
		if(index==-1) {
			index = uri.lastIndexOf('/');
		}
		if(index>0) {
			String ns = uri.substring(0, index);
			String local = uri.substring(index+1);
			return ns+":"+local;
		} else {
			return uri;
		}*/
		return proxy.shortForm(uri);
	}

	@Override
	public String qnameFor(String uri) {
		String shortForm = shortForm(uri);
		return shortForm.equals(uri)? null:shortForm;
	}

	@Override
	public PrefixMapping lock() {
		return this;
	}

	@Override
	public boolean samePrefixMappingAs(PrefixMapping other) {
		//FIXME optimize for early exit
		return getNsPrefixMap().equals(other.getNsPrefixMap());
	}
}