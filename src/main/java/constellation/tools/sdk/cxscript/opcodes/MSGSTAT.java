package constellation.tools.sdk.cxscript.opcodes;

import static constellation.tools.sdk.cxscript.CxScriptUtil.decodeString;
import static java.lang.String.format;

import java.nio.ByteBuffer;

import constellation.ApplicationController;
import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Script Goto Label (MSGSTAT) OpCode
 * @author lawrence.daniels@gmail.com
 */
public class MSGSTAT implements CxScriptOpCode {
	private final String message;
	
	/**
	 * Creates a new set status message operation
	 * @param message the given status message
	 */
	public MSGSTAT( final String message ) {
		this.message = message;
	}
	
	/**
	 * Creates a new set status message operation
	 * @param buffer the given {@link ByteBuffer buffer}
	 */
	public MSGSTAT( final ByteBuffer buffer ) {
		this.message = decodeString( buffer );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptOpCode#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
	 */
	public void execute( final CxScriptRuntimeContext context ) {
		// get the application controller instance
		final ApplicationController controller = context.getApplicationController();
		controller.setStatusMessage( message );
		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "MSGSTAT \"%s\"", message );
	}

}
