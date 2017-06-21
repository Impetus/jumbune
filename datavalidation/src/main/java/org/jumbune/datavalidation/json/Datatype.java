package org.jumbune.datavalidation.json;

public enum Datatype {
	NULL("null"),
	STRING("String"),
	NUMBER("Number"),
	BOOLEAN("boolean"),
	ARRAY("Array"),
	MAP("Map");
	
	private String datatype;
	
	private Datatype(String datatype){
		this.setDatatype(datatype);
		
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	
}
