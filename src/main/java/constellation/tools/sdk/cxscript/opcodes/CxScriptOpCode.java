package constellation.tools.sdk.cxscript.opcodes;

import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Script OpCode
 * @author lawrence.daniels@gmail.com
 */
public interface CxScriptOpCode {
	
	/**
	 * Executes the operational code
	 * @param context the given {@link CxScriptRuntimeContext context}
	 */
	void execute( CxScriptRuntimeContext context );
	
}
