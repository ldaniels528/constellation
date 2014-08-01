package constellation.tools.sdk.math.geometric.expressions;

/**
 * Represents a localized scope
 * @author lawrence.daniels@gmail.com
 */
public class LocalScope extends DefaultScope {
	private final Scope parentScope;
	
	/** 
	 * Creates a new local scope, which falls back to
	 * the parent scope if a variable is not found.
	 * @param parentScope the given parent {@link Scope scope}
	 */
	public LocalScope( final Scope parentScope ) {
		this.parentScope = parentScope;
	}
	
	/**
	 * Returns the parent scope
	 * @return the {@link Scope parent scope}
	 */
	public Scope getParentScope() {
		return parentScope;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.math.geometric.expressions.GlobalScope#getValue(java.lang.String)
	 */
	public Double getValue( final String variableName ) {
		// if the variable is not in the local scope ...
		if( !containsVariable( variableName ) ) {
			// allow the parent scope to handle it
			return parentScope.getValue( variableName );
		}
		
		// return the variable's value
		return super.getValue( variableName );
	}

}
