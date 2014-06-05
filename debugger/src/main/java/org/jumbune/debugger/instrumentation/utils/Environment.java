package org.jumbune.debugger.instrumentation.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The class Environment
 *
 */
public class Environment {
	
	//---Debugger optimization....
	private Map<String,String> classNamevsClassSymbolTable = new HashMap<String, String>(InstrumentConstants.FIFTEEN);
	private static Map<String, Integer> classWiseMethodCounter = new HashMap<String, Integer>(InstrumentConstants.FIFTEEN);
	private int classCounter = 0;
	private static Map<String,String> methodNamevsMethodSymbo1Table = new HashMap<String, String>(InstrumentConstants.FIFTEEN);
	private static Map<String,String> classAndMethodvsMethodNameTable = new HashMap<String, String>(InstrumentConstants.FIFTEEN);
	
	private static final String METHOD_SYMBOL_SEPARATOR = "|";
	private static final String METHOD_SYMBOL_SUFFIX = "m";
	private static final String CLASS_SYMBOL_SUFFIX = "c";
	
	/**
	 * constructor for Environment
	 */
	public Environment() {}

	/**
	 * getter for symbol table
	 * @return
	 */
	public Map<String, String> getSymbolTable() {
		return classNamevsClassSymbolTable;
	}

	/**
	 * getter for symbol counter
	 * @return
	 */
	public Map<String, Integer> getSymbolCounter() {
		return classWiseMethodCounter;
	}

	/**
	 * getter for class counter
	 * @return
	 */
	public int getClassCounter() {
		return classCounter;
	}

	/**
	 * getter for classAndMethodvsMethodNameTable
	 * @return
	 */
	public static Map<String, String> getClassMethodSymbols() {
		return classAndMethodvsMethodNameTable;
	}
	
	/**
	 * API to get class symbol
	 * @param className
	 * @return
	 */
	public String getClassSymbol(String className){
		
		String symbol = classNamevsClassSymbolTable.get(className);
		if(symbol== null || symbol.isEmpty() ){
			classCounter ++;
			symbol = classCounter+CLASS_SYMBOL_SUFFIX;
			classNamevsClassSymbolTable.put(className, symbol);
		}
		return symbol;
	}


	/**
	 * API to get method symbol
	 * @param className
	 * @param classSymbol
	 * @param methodName
	 * @return
	 */
	public static String getMethodSymbol(final String className,String classSymbol,String methodName){
		String tempClassName=className;
		if(tempClassName == null){
			tempClassName="";
		}
		String key = new StringBuilder(tempClassName).
				append(METHOD_SYMBOL_SEPARATOR).
				append(methodName).toString();
		String symbol = methodNamevsMethodSymbo1Table.get(key); 
		if(symbol == null || symbol.isEmpty()){
			Integer mCounter = classWiseMethodCounter.get(tempClassName);
			if(mCounter == null){
				mCounter=0;
			}
			symbol = new StringBuilder(InstrumentConstants.FIVE).append(++mCounter).
					append(METHOD_SYMBOL_SUFFIX).toString();
			methodNamevsMethodSymbo1Table.put(key, symbol);
			classWiseMethodCounter.put(tempClassName, mCounter);

			classAndMethodvsMethodNameTable.put(new StringBuilder(InstrumentConstants.TEN).
					append(classSymbol).append(METHOD_SYMBOL_SEPARATOR).
					append(symbol).toString(), methodName);
		}
		return symbol;		
	}
	
}
