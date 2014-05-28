/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 */
package com.neo4j.jena.graph;

import com.hp.hpl.jena.graph.Capabilities;

/**
 * All capabilities.
 * 
 * @author Khalid Latif (2014-02-12)
 */
public class NeoCapabilities implements Capabilities {
	
	public static final Capabilities DEFAULT = new NeoCapabilities(); 
	
	@Override
	public boolean sizeAccurate() {
		return true;
	}

	@Override
	public boolean addAllowed() {
		return addAllowed(false);
	}

	@Override
	public boolean addAllowed(boolean every) {
		return true;
	}

	@Override
	public boolean deleteAllowed() {
		return deleteAllowed(false);
	}

	@Override
	public boolean deleteAllowed(boolean every) {
		return true;
	}

	@Override
	public boolean canBeEmpty() {
		return true;
	}

	@Override
	public boolean iteratorRemoveAllowed() {
		return true;
	}

	@Override
	public boolean findContractSafe() {
		return true;
	}

	@Override
	public boolean handlesLiteralTyping() {
		return true;
	}
}