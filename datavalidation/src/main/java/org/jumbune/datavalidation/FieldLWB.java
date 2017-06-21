package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;


public class FieldLWB implements Writable{
	//Key : violationType value : ViolationWB
	private MapWritable typeViolationMap ;

	public FieldLWB() {
		setTypeViolationMap(new MapWritable());
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		getTypeViolationMap().readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		getTypeViolationMap().write(out);
	}

	 public void putAll(Map<? extends Writable, ? extends Writable> t) {
		 for (Map.Entry<? extends Writable, ? extends Writable> e: t.entrySet()) {
			 getTypeViolationMap().put(e.getKey(), e.getValue());
		     }
		 }
	 
	 public String toString(){
		 StringBuilder builder = new StringBuilder();
		 for(Map.Entry<Writable, Writable> entry: getTypeViolationMap().entrySet()){
			 builder.append(this.hashCode()+"-FieldLWB:"+ entry.getKey()+":"+entry.getValue());
		 }
		 return builder.toString(); 
	 }

	

	public void resetTypeViolationMap() {
		setTypeViolationMap(new MapWritable());
	}

	public MapWritable getTypeViolationMap() {
		return typeViolationMap;
	}

	public void setTypeViolationMap(MapWritable typeViolationMap) {
		this.typeViolationMap = typeViolationMap;
	}


	
	
}
