package org.jumbune.web.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * It is used to create multiline timeseries graphs/charts json
 */
public class Graphs {
	
	private Map<String, Graph> graphs;
	
	public Graphs() {
	}
	
	public Graphs(int size) {
		graphs = new HashMap<String, Graph>(size);
	}
	
	public Map<String, Graph> getGraphs() {
		return this.graphs;
	}
	
	public void addGraph(String graphName, Graph graph) {
		if (graphs == null) {
			graphs = new HashMap<String, Graph>(1);
		}
		graphs.put(graphName, graph);
	}

}
