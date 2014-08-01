package constellation.tools.sdk.cxscript.opcodes;

import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Script Terminate (TERM) OpCode
 * @author lawrence.daniels@gmail.com
 */
public class TERM implements CxScriptOpCode {

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptOpCode#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
	 */
	public void execute( final CxScriptRuntimeContext context ) {
		context.setTerminate( true );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TERM";
	}

}
