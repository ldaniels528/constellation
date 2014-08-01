package constellation.tools.sdk.math.geometric.expressions;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a global scope
 * @author lawrence.daniels@gmail.com
 */
public class DefaultScope implements Scope {
	private final Map<String, Double> mapping;
	
	/**
	 * Default constructor
	 */
	public DefaultScope() {
		this.mapping = new HashMap<String, Double>();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.math.geometric.expressions.Scope#addVariable(java.lang.String, double)
	 */
	public void setVariable( final String variableName, final double value ) {
		mapping.put( variableName, value );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.math.geometric.expressions.Scope#getValue(java.lang.String)
	 */
	public Double getValue( final String variableName ) {
		// if no mapping was found, error
		if( !mapping.containsKey( variableName ) ) {
			throw new IllegalArgumentException( String.format( "No mapping found for '%s'", variableName ) );
		}
		
		return mapping.get( variableName );
	}
	
	/**
	 * Indicates whether the requested variable is contained 
	 * within this scope.
	 * @param name the name of the variable
	 * @return true, if the requested variable is contained 
	 * within this scope.
	 */
	protected boolean containsVariable( final String name ) {
		return mapping.containsKey( name );
	}
	
}
