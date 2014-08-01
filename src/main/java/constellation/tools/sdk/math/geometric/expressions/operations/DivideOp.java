package constellation.tools.sdk.math.geometric.expressions.operations;

import constellation.tools.sdk.math.geometric.expressions.Scope;
import constellation.tools.sdk.math.geometric.expressions.ValueReference;

/**
 * Represents an Division operation
 * @author lawrence.daniels@gmail.com
 */
public class DivideOp implements ValueReference {
	private final ValueReference lvar;
	private final ValueReference rvar;
	
	/**
	 * Creates a new "divide" operation
	 * @param lvar the left side {@link ValueReference value reference}
	 * @param rvar the right side {@link ValueReference value reference}
	 */
	public DivideOp( final ValueReference lvar, final ValueReference rvar ) {
		this.lvar 	= lvar;
		this.rvar	= rvar;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.math.geometric.expressions.ValueReference#evaluate(constellation.tools.sdk.math.geometric.expressions.Scope)
	 */
	public Double evaluate( final Scope scope ) {
		final Double lvalue = lvar.evaluate( scope );
		final Double rvalue = rvar.evaluate( scope );
		return ( lvalue == null || rvalue == null ) ? null : ( lvalue / rvalue );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "(%s / %s)", lvar, rvar );
	}

}
