package constellation.tools.sdk.math.geometric.expressions;

/**
 * Represents an expression variable (e.g. '5x')
 * @author lawrence.daniels@gmail.com
 */
public class Variable implements ValueReference {
	private final String name;
	private final double coefficient;
	private final double exponent;
	
	/**
	 * Creates a new variable
	 * @param name the name of the variable
	 */
	public Variable( final String name ) {
		this( 1, name );
	}
	
	/**
	 * Creates a new variable
	 * @param name the name of the variable
	 * @param exponent the exponent of the variable
	 */
	public Variable( final String name, final double exponent ) {
		this.coefficient	= 1;
		this.name			= name;
		this.exponent		= exponent;
	}
	
	/**
	 * Creates a new variable
	 * @param coefficient the variable's coefficient
	 * @param name the name of the variable
	 */
	public Variable( final double coefficient, final String name ) {
		this( coefficient, name, 1 );
	}
	
	/**
	 * Creates a new variable
	 * @param coefficient the coefficient of the variable
	 * @param name the name of the variable
	 * @param exponent the exponent of the variable
	 */
	public Variable( final double coefficient, final String name, final double exponent ) {
		this.coefficient	= coefficient;
		this.name			= name;
		this.exponent		= exponent;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( final Object object ) {
		// the object must be a variable
		if( !( object instanceof Variable ) ) {
			return false;
		}
		
		// cast the object
		final Variable variable = (Variable)object;
		
		// compare the variables
		return ( name.equals( variable.name ) && exponent == variable.exponent );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.equations.ValueReference#evaluate(constellation.math.geometric.equations.EquationContext)
	 */
	public Double evaluate( final Scope scope ) {
		// evaluate the mapped value
		final Double value = scope.getValue( name );
		
		// evaluate the expression
		return ( value != null ) ? coefficient * Math.pow( value, exponent ) : null;
	}
	
	/**
	 * Returns the exponent order
	 * @return the exponent order
	 */
	protected double getOrder() {
		return exponent;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ( exponent != 1.0d ) 
				? ( ( coefficient != 1.0d ) 
						? String.format( "%3.2f(%s^%.0f)", coefficient, name, exponent )
						: String.format( "%s^%.0f", name, exponent ) )
				: ( ( coefficient != 1.0d ) 
						? String.format( "%3.2f%s", coefficient, name ) 
						: String.format( "%s", name ) );
	}
	
}
