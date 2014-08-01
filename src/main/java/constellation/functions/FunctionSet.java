package constellation.functions;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a collection of related functions.
 * @author lawrence.daniels@gmail.com
 */
public class FunctionSet {
	private final Map<String,Function> functions;
	private final String familyName;
	
	/**
	 * Default constructor
	 */
	public FunctionSet( final String familyName ) {
		this.familyName	= familyName;
		this.functions	= new LinkedHashMap<String,Function>();
	}
	
	/** 
	 * Adds the function to the command
	 * @param function the given {@link Function function}
	 */
	public void add( final Function function ) {
		functions.put( function.getName(), function );
	}
	
	/**
	 * Returns the name of the function set
	 * @return the name of the function set
	 */
	public String getName() {
		return familyName;
	}
	
	/**
	 * Returns the set of functions
	 * @return the set of functions
	 */
	public Function getFunction( final String name ) {
		return functions.get( name );
	}
	
	/**
	 * Returns the set of functions
	 * @return the set of functions
	 */
	public Collection<Function> getFunctions() {
		return functions.values();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}
	
}