package org.jumbune.profiling.hprof;

/**
 * The Enum DFSEnum is used for marking the data file system statistics.
 */
public enum DFSEnum {
	InitialValue("initValue", "initValue"), Capacity("Configured Capacity", "Capacity"), DfsUsed("DFS Used", "DfsUsed"), Remaining("DFS Remaining",
			"Remaining"), NonDfsUsed("Non DFS Used", "NonDfsUsed");

	private String name;
	private String value;

	/**
	 * Instantiates a new dFS enum.
	 *
	 * @param name the name
	 * @param value the value
	 */
	private DFSEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if is exist.
	 *
	 * @param name the name
	 * @return true, if is exist
	 */
	public boolean isExist(String name) {
		for (DFSEnum dfsEnum : DFSEnum.values()) {
			if (dfsEnum.name.equals(name)) {
				this.value = dfsEnum.value;
				return true;
			}
		}
		return false;
	}
}
