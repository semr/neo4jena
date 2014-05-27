package com.neo4j.jena.bench;

public class StopWatch {
	
	private long start;
	private long elapsed;
	
	public StopWatch() {
		start();
	}
	
	public void start() {
		start = System.currentTimeMillis();
		elapsed = 0;
	}
	
	public long pause() {
		elapsed += System.currentTimeMillis() - start;
		return elapsed;
	}
	
	public void resume() {
		start = System.currentTimeMillis();
	}
	
	public long stop() {
		return pause();
	}
	
	public double asSeconds(long milli) {
		return ((double)milli)/1000;
	}

}