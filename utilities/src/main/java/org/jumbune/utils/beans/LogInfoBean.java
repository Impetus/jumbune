/**
 * 
 */
package org.jumbune.utils.beans;


/**
 * This Bean class is used to pass values to logMethod
 * 
 */

public class LogInfoBean {
	/** Name of the class in which this Log statement is written */
	private String logCallingClass;

	/** Name of the method in which this Log statement is written */
	private String logCallingMethod;

	/** Message prefix to written in log */
	private String msgPrefix;

	/** Message suffix to written in log */
	private String msgSuffix;

	/**
	 * Creates new object of LogInfoBean and initializes its variables
	 * 
	 * @param logCallingClass
	 *            - name of class in which log statement is written
	 * @param logCallingMethod
	 *            - name of method in which this log statement is written
	 * @param msgPrefix
	 *            - message prefix to be passed to logger
	 * @param msgSuffix
	 *            - message suffix to be passed to logger
	 */
	public LogInfoBean(String logCallingClass, String logCallingMethod, String msgPrefix, String msgSuffix) {
		this.logCallingClass = logCallingClass;
		this.logCallingMethod = logCallingMethod;
		this.msgPrefix = msgPrefix;
		this.msgSuffix = msgSuffix;
	}

	/**
	 * See <code>logCallingClass</code>
	 * 
	 * @return calling class
	 */
	public String getLogCallingClass() {
		return logCallingClass;
	}

	/**
	 * See <code>logCallingMethod</code>
	 * 
	 * @return calling method name
	 */
	public String getLogCallingMethod() {
		return logCallingMethod;
	}

	/**
	 * See <code>msgPrefix</code>
	 * 
	 * @return log message prefix
	 */
	public String getMsgPrefix() {
		return msgPrefix;
	}

	/**
	 * See <code>msgSuffix</code>
	 * 
	 * @return log message suffix
	 */
	public String getMsgSuffix() {
		return msgSuffix;
	}

	/**
	 * Set the value of <code>msgPrefix</code>
	 * 
	 * @param msgPrefix
	 */
	public void setMsgPrefix(String msgPrefix) {
		this.msgPrefix = msgPrefix;
	}

	/**
	 * Set the value of <code>msgSuffix</code>
	 * 
	 * @param msgPrefix
	 */
	public void setMsgSuffix(String msgSuffix) {
		this.msgSuffix = msgSuffix;
	}

	

	@Override
	public String toString() {
		return "LogInfoBean [logCallingClass=" + logCallingClass
				+ ", logCallingMethod=" + logCallingMethod + ", msgPrefix="
				+ msgPrefix + ", msgSuffix=" + msgSuffix
				+ ", getLogCallingClass()=" + getLogCallingClass()
				+ ", getLogCallingMethod()=" + getLogCallingMethod()
				+ ", getMsgPrefix()=" + getMsgPrefix() + ", getMsgSuffix()="
				+ getMsgSuffix() + ", hashCode()=" + hashCode()
				+ ", getClass()=" + getClass() + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((logCallingClass == null) ? 0 : logCallingClass.hashCode());
		result = prime
				* result
				+ ((logCallingMethod == null) ? 0 : logCallingMethod.hashCode());
		result = prime * result
				+ ((msgPrefix == null) ? 0 : msgPrefix.hashCode());
		result = prime * result
				+ ((msgSuffix == null) ? 0 : msgSuffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		LogInfoBean other = (LogInfoBean) obj;
		
		if(!processlogCallingClass(other)){
			return false;
		}
		
		if(!processlogCallingMethod(other)){
			return false;
		}
		if(!processMsgPrefix(other)){
			return false;
		}
		if(!processMsgSuffix(other)){
			return false;
		}
		return true;
	}

	private boolean processMsgSuffix(LogInfoBean other) {
		if (msgSuffix == null) {
			if (other.msgSuffix != null){
				return false;
			}
		} else if (!msgSuffix.equals(other.msgSuffix)){
			return false;
		}
		return true;
	}

	private boolean processMsgPrefix(LogInfoBean other) {
		if (msgPrefix == null) {
			if (other.msgPrefix != null){
				return false;
			}
		} else if (!msgPrefix.equals(other.msgPrefix)){
			return false;
		}
		return true;
	}

	private boolean processlogCallingMethod(LogInfoBean other) {
		if (logCallingMethod == null) {
			if (other.logCallingMethod != null){
				return false;
			}
		} else if (!logCallingMethod.equals(other.logCallingMethod)){
			return false;
		}
		return true;
	}

	private boolean processlogCallingClass(LogInfoBean other) {
		if (logCallingClass == null) {
			if (other.logCallingClass != null){
				return false;
			}
		} else if (!logCallingClass.equals(other.logCallingClass)){
			return false;
		}
		return true;
	}

}