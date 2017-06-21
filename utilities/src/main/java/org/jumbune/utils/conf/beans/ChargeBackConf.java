/*
 * 
 */
package org.jumbune.utils.conf.beans;

/**
 * The Class ChargeBackConf is a pojo containing the cost of vcore and memory defined by user for a particular queue.
 */
public class ChargeBackConf {
	
	/** The queue name. */
	private String queueName;
	
	/** The v core. */
	private double vCore ;
	
	/** The memory. */
	private double memory ;
	
	/** The execution engine. */
	private String executionEngine ;

	/**
	 * Gets the queue name.
	 *
	 * @return the queue name
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Sets the queue name.
	 *
	 * @param queueName the new queue name
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Gets the v core.
	 *
	 * @return the v core
	 */
	public double getvCore() {
		return vCore;
	}

	/**
	 * Sets the v core.
	 *
	 * @param vCore the new v core
	 */
	public void setvCore(double vCore) {
		this.vCore = vCore;
	}

	/**
	 * Gets the memory.
	 *
	 * @return the memory
	 */
	public double getMemory() {
		return memory;
	}

	/**
	 * Sets the memory.
	 *
	 * @param memory the new memory
	 */
	public void setMemory(double memory) {
		this.memory = memory;
	}

	/**
	 * Gets the execution engine.
	 *
	 * @return the execution engine
	 */
	public String getExecutionEngine() {
		return executionEngine;
	}

	/**
	 * Sets the execution engine.
	 *
	 * @param executionEngine the new execution engine
	 */
	public void setExecutionEngine(String executionEngine) {
		this.executionEngine = executionEngine;
	}

	@Override
	public String toString() {
		return "ChargeBackConf [queueName=" + queueName + ", vCore=" + vCore + ", memory=" + memory
				+ ", executionEngine=" + executionEngine + "]";
	}
	
	

}
