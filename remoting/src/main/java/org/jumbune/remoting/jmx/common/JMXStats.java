package org.jumbune.remoting.jmx.common;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JMXStats implements Serializable {

	private static final long serialVersionUID = -634040695028567680L;
	private String objectNamePrefix;
	private Map<String, Object> attributeMap = new HashMap<String, Object>(1);

	public String getObjectNamePrefix() {
		return objectNamePrefix;
	}

	public void setObjectNamePrefix(String objectNamePrefix) {
		this.objectNamePrefix = objectNamePrefix;
	}

	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
	}

	@Override
	public String toString() {
		return "JMXStats [objectNamePrefix=" + objectNamePrefix
				+ ", attributeMap=" + attributeMap + "]";
	}

}
