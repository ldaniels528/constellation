package constellation.tools.sdk.math.geometric.expressions;

/**
 * Represents a reference to a value (either constant or variable)
 * @author lawrence.daniels@gmail.com
 */
public interface ValueReference {
	
	/**
	 * Evaluates the value reference
	 * @param scope the given {@link Scope context}
	 * @return the resultant value
	 */
	Double evaluate( Scope scope );

}
