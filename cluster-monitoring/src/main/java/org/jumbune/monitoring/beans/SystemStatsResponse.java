package org.jumbune.monitoring.beans;

import java.util.Map;

/**
 * Pojo to store the response of System stats for a node
 */
public class SystemStatsResponse {

	private Map<String, String> cpu;
	private Map<String, String> memory;
	private Map<String, String> os;

	/**
	 * @return the cpu
	 */
	public Map<String, String> getCpu() {
		return cpu;
	}

	/**
	 * @param cpu
	 *            the cpu to set
	 */
	public void setCpu(Map<String, String> cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the memory
	 */
	public Map<String, String> getMemory() {
		return memory;
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(Map<String, String> memory) {
		this.memory = memory;
	}

	/**
	 * @return the os
	 */
	public Map<String, String> getOs() {
		return os;
	}

	/**
	 * @param os
	 *            the os to set
	 */
	public void setOs(Map<String, String> os) {
		this.os = os;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder=new StringBuilder();
		if(cpu!=null && !cpu.isEmpty()){
		for (Map.Entry<String, String> entry : cpu.entrySet()) {
			stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
    		}
		}
		if(memory!=null && !memory.isEmpty()){
			for (Map.Entry<String, String> entry : memory.entrySet()) {
				stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
			}
		}
		if(os!=null && !os.isEmpty()){
			for (Map.Entry<String, String> entry : os.entrySet()) {
				stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
			}
		}
		
		stringBuilder.substring(0,stringBuilder.length()-1);
		return stringBuilder.toString();
	}

	
}
