package constellation.tools.sdk.cxscript.value;

import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Constant Value
 * @author lawrence.daniels@gmail.com
 */
public class ConstantValue implements ValueReference {
	private final Object value;
	
	/**
	 * Default Constructor
	 */
	public ConstantValue( final Object value ) {
		this.value = value;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.CxScriptExecutable#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
	 */
	public Object getValue( final CxScriptRuntimeContext context ) {
		return value;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value.toString();
	}

}
