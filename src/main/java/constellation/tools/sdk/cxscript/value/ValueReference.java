package constellation.tools.sdk.cxscript.value;

import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Value Reference
 * @author lawrence.daniels@gmail.com
 */
public interface ValueReference {

	/**
	 * Returns the referenced value
	 * @param context the given {@link CxScriptRuntimeContext runtime context}
	 * @return the {@link Object value}
	 */
	Object getValue( CxScriptRuntimeContext context );
	
}
