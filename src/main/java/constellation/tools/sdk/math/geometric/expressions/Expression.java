package constellation.tools.sdk.math.geometric.expressions;

import static java.lang.String.format;

/**
 * Represents a mathematical expression (e.g. '5x+5')
 * @author lawrence.daniels@gmail.com
 */
public class Expression implements ValueReference {
	private final ValueReference valueReference;
	
	/**
	 * Creates a new mathematical expression (e.g. '5x+5')
	 * @param elements the given {@link valueReference value reference}
	 */
	public Expression( final ValueReference valueReference ) {
		this.valueReference = valueReference;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.equations.ValueReference#evaluate(constellation.math.geometric.equations.EquationContext)
	 */
	public Double evaluate( final Scope scope ) {
		return valueReference.evaluate( scope );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "f(x) = %s", valueReference );
	}
	
}