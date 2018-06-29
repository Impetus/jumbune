package org.jumbune.utils.conf.beans;

/**
 * The Class SeverityLevel.
 */
public class SeverityLevel {

	/** The warning level. */
	private int warningLevel;
	
	/** The critical level. */
	private int criticalLevel;
	
	public SeverityLevel() {
	}
		
	public SeverityLevel(int warningLevel, int criticalLevel) {
		this.warningLevel = warningLevel;
		this.criticalLevel = criticalLevel;
	}

	/**
	 * Gets the warning level.
	 *
	 * @return the warning level
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	/**
	 * Gets the critical level.
	 *
	 * @return the critical level
	 */
	public int getCriticalLevel() {
		return criticalLevel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SeverityLevel [warningLevel=" + warningLevel + ", criticalLevel=" + criticalLevel + "]";
	}
	
}
