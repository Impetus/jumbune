package org.jumbune.common.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class Doc {

	@XmlElement(name = "property")
	public List<Property> configuration;

}
