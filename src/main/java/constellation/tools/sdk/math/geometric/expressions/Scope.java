package constellation.tools.sdk.math.geometric.expressions;

/**
 * Represents the scope which contains all variables
 * for evaluating functions.
 * @author lawrence.daniels@gmail.com
 */
public interface Scope {

	/** 
	 * Adds a mapping of a variable to a value
	 * @param variableName the given variable name
	 * @param value the given value
	 */
	void setVariable( String variableName, double value );
	
	/** 
	 * Returns the value for the given variable name
	 * @param variableName the given variable name
	 * @return the value or <tt>null</tt> if not found
	 */
	Double getValue( String variableName );
	
}
