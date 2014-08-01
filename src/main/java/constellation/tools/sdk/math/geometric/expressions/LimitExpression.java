package constellation.tools.sdk.math.geometric.expressions;

import static java.lang.String.format;

/**
 * Represents an expression expressed within a limit declaration (e.g. 'f(x)=x^2+5[1:6]')
 * @author lawrence.daniels@gmail.com
 */
public class LimitExpression extends Expression {
	private final double start;
	private final double end;
	
	/** 
	 * Represents a limit-based expression
	 * @param elements the given expression elements
	 * @param start the start of the range for the expression
	 * @param end the given of the range for the expression
	 */
	public LimitExpression( final double start, 
							final double end,
							final ValueReference valueReference ) {
		super( valueReference );
		this.start 	= start;
		this.end	= end;
	}

	/**
	 * @return the start
	 */
	public double getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public double getEnd() {
		return end;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "%s[%3.2d:%3.2d]", super.toString(), start, end );
	}
	
}
