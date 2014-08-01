package constellation.tools.sdk.cxscript;

import constellation.tools.sdk.cxscript.value.ValueReference;


/**
 * Constellation Script Function
 * @author lawrence.daniels@gmail.com
 */
public interface CxScriptFunction {

	/**
	 * Executes the function
	 * @param context the given {@link CxScriptRuntimeContext runtime}
	 * @param args the given {@link ValueReference function arguments}
	 */
	void execute( CxScriptRuntimeContext context, ValueReference[] args );

	/**
	 * Returns the name of the function
	 * @return the name
	 */
	String getName();

}
