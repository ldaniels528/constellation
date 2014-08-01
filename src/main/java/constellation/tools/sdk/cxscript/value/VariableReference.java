package constellation.tools.sdk.cxscript.value;

import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Variable Reference
 * @author lawrence.daniels@gmail.com
 */
public class VariableReference implements ValueReference {
	private final String name;
	
	/** 
	 * Creates a new variable reference
	 * @param name the name of the variable
	 */
	public VariableReference( final String name ) {
		this.name = name;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.value.ValueReference#getValue(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
	 */
	public Object getValue( final CxScriptRuntimeContext context ) {
		// TODO Auto-generated method stub
		return null;
	}

}
