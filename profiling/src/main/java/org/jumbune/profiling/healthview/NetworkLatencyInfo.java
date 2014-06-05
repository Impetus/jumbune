package org.jumbune.profiling.healthview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * POJO class to get Network Latency information
 *
 */
public class NetworkLatencyInfo {

	private String id;
	private String name;
	private Map<String, String> data = new HashMap<String, String>() {

		{

			put("stat1", "value1");
			put("stat1", "value1");

		}
	};
	private List<AdjacencyInfo> adjacencies;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the adjacencies
	 */
	public List<AdjacencyInfo> getAdjacencies() {
		return adjacencies;
	}

	/**
	 * @param adjacencies
	 *            the adjacencies to set
	 */
	public void setAdjacencies(List<AdjacencyInfo> adjacencies) {
		this.adjacencies = adjacencies;
	}

	/**
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}

	
}