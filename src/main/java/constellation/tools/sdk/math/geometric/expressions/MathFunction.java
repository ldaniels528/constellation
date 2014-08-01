package constellation.tools.sdk.math.geometric.expressions;

/**
 * Represents a math function
 * @author lawrence.daniels@gmail.com
 */
public interface MathFunction extends ValueReference {
	
	/**
	 * Returns the inverse function
	 * @return the inverse function
	 */
	MathFunction invert();
	
	/**
	 * Returns the argument of the function
	 * @return the argument of the function
	 */
	ValueReference getArgument();

}
