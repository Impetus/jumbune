package org.jumbune.datavalidation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * A MultiValueTreeMap allows Map to have more than one value for a key.
 * The multiple values for a key if present will be sorted based on the Comparator<V>.
 * MultiValueTreeMap is not synchronized and is not thread-safe

 * @param <K>
 * @param <V>
 */
public class MultiValueTreeMap<K,V> extends HashMap<K,TreeMap<V, LinkedList<V>>> implements Cloneable, java.io.Serializable {

	private static final long serialVersionUID = -7316106923870914861L;

	private static final boolean lineSeparatorInToString = false;
	
	private int MAX_STORED_ELEMENTS;

	public MultiValueTreeMap(int maxSortedElements){
		super();
		MAX_STORED_ELEMENTS = maxSortedElements;
	}

	/**
	 * Adds the value to the collection associated with the given key
	 * @param reference
	 * @param value
	 */
	public void add(K reference, V value){
		TreeMap<V, LinkedList<V>> innerMap;
		innerMap = get(reference);
		LinkedList<V> list;
		if(innerMap!=null){
			list = innerMap.get(value);
			if(list!=null){
				list.add(value);
			}else{
				list = new LinkedList<V>();
				innerMap.put(value, list);
				list.add(value);
			}
		}else {
			innerMap = new TreeMap<V, LinkedList<V>>();
			put(reference, innerMap);
			list = new LinkedList<V>();
			list.add(value);
			innerMap.put(value, list);
		}
		if (innerMap.size() > MAX_STORED_ELEMENTS){
			innerMap.remove(innerMap.lastKey());
		}
	}

	/**
	 * Given the reference returns the collection of linked values. The collector iterator returns the values in ascending order
     * of the corresponding keys.
	 * @param reference
	 * @return sorted map containing linked values
	 */
	public List<V> getAllElements(K reference){
		List<V> returnedList = null;
		if(containsKey(reference)){
			returnedList = new LinkedList<V>();
			TreeMap<V, LinkedList<V>> innerMap = get(reference);
			for(LinkedList<V> list: innerMap.values()){
				for(V t: list){
					returnedList.add(t);
				}
			}
			if(returnedList.size()>MAX_STORED_ELEMENTS){
				return returnedList.subList(0, MAX_STORED_ELEMENTS);
			}	
		}
		return returnedList;
	}

	public String toString(){
		StringBuilder b = new StringBuilder();
		for(K reference: keySet()){
			b.append("\nReference -"+reference);
			TreeMap<V, LinkedList<V>> innerMap = get(reference);
			for(List<V> list : innerMap.values()){
				b.append(System.lineSeparator());
				for(V entry:list){
					if(lineSeparatorInToString){
						if(entry!=null){
							b.append( entry  );						
						}
						b.append(System.lineSeparator());
					}else{
						b.append("[" + entry + "], " );					
					}
				}
			}
		}
		return b.toString();
	}
	
	public void clear(){
		super.clear();
	}
	
	
}
