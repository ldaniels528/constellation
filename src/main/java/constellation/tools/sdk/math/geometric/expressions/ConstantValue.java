package constellation.tools.sdk.math.geometric.expressions;

/**
 * Represents an expression constant value (e.g. '5')
 * @author lawrence.daniels@gmail.com
 */
public class ConstantValue implements ValueReference {
	private double value;
	
	/**
	 * Creates a new constant
	 * @param value the contant's value
	 */
	public ConstantValue( final double value ) {
		this.value = value;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.equations.ValueReference#evaluate(constellation.math.geometric.equations.EquationContext)
	 */
	public Double evaluate( final Scope scope ) {
		return value;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.math.geometric.expressions.ValueReference#negate()
	 */
	public ValueReference negate() {
		return new ConstantValue( -value );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "%3.2f", value );
	}
	
}
