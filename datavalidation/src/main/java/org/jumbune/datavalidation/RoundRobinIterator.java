package org.jumbune.datavalidation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
* The Class RoundRobinIterator.
* Takes the list of number of reducers in constructor
* returns the current index value in round robin fashion
* with every new instantiation of the class multiple list
* can be maintained in RR fashion.
*/
public class RoundRobinIterator {

	/** The index to track next value to be fetched from objectList. */

	private AtomicInteger index = new AtomicInteger(-1);

	/**
	 * The list of object. Record from this list will be fetched in round-robin
	 * manner.
	 */

	private List<Integer> objectList = null;
	private Integer nextObject = null;

	/**
	 * Instantiates a new round robin iterator.
	 * takes list of reducers as input these reducers
	 * are being calculated based upon the NOR algorithm
	 * @param objectList
	 */
	public RoundRobinIterator(final List<Integer> objectList) {
		if (objectList == null) {
			throw new IllegalArgumentException("Invalid argument objectList:["
					+ objectList + "] to RoundRobinIterator()");
		}
		this.objectList = objectList;
	}

	/**
	 * Checks for next number in the list if it exists
	 * the index is maintained and returns the current 
	 * value at the index and set that value in the nextobject object
	 * @return true, if successful
	 */
	public boolean hasNext() {
		if (this.objectList.size() > 0) {
			this.index.set(this.index.incrementAndGet()
					% this.objectList.size());
			nextObject = this.objectList.get(this.index.get());
		} else {
			nextObject = null;
		}
		return true;
	}

	/**
	 * Next invocation returns the object which has the
	 * value at the current index
	 *
	 * @return the int
	 */	
	public int next() {
		return nextObject;
	}

	/**
	 * Invoking removes method removes the element from the list
	 * it checks whether there are more elements in the list if not
	 * then returns null or if exists than it will remove the element
	 * at the index.
	 */
	public void remove() {
		if (this.index.get() == -1) {
			return;
		}
		if (this.objectList.size() > 0) {
			this.objectList.remove(this.index.get());
		} else if (this.index.get() != -1) {
			this.index.set(-1);
		}
	}
}
