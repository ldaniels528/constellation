package constellation.tools.sdk.cxscript;

import java.nio.ByteBuffer;

import constellation.ApplicationController;
import constellation.tools.sdk.cxscript.opcodes.CxScriptOpCode;
import constellation.tools.sdk.cxscript.opcodes.CxScriptOpCodeDecoder;

/**
 * Constellation Script Runtime
 * @author lawrence.daniels@gmail.com
 */
public class CxScriptRuntime {
	private final ApplicationController controller;
	private final CxScriptOpCodeDecoder decoder;
	
	/**
	 * Creates a new CxScript runtime
	 * @param controller the given {@link ApplicationController controller}
	 */
	public CxScriptRuntime( final ApplicationController controller ) {
		this.controller	= controller;
		this.decoder 	= new CxScriptOpCodeDecoder();
	}

	/** 
	 * Executes the byte code
	 * @param buffer the byte code
	 */
	public void execute( final ByteBuffer buffer ) {
		// create a new runtime context
		final CxScriptRuntimeContext context = new CxScriptRuntimeContext( controller );
		
		// execute until the end of the buffer
		while( !context.isTerminate() && buffer.hasRemaining() ) {
			final CxScriptOpCode opCode = decoder.decode( buffer );
			opCode.execute( context );
		}
	}
	
}
