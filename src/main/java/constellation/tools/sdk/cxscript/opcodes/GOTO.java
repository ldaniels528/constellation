package constellation.tools.sdk.cxscript.opcodes;

import static java.lang.String.format;

import java.nio.ByteBuffer;

import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;

/**
 * Constellation Script Goto Label (GOTO) OpCode
 * @author lawrence.daniels@gmail.com
 */
public class GOTO implements CxScriptOpCode {
	private final int offset;
	
	/**
	 * Creates a new "GOTO" opCode
	 * @param label the given label
	 */
	public GOTO( final int offset ) {
		this.offset = offset;
	}
	
	/**
	 * Creates a new "GOTO" opCode
	 * @param buffer the given {@link ByteBuffer buffer}
	 */
	public GOTO( final ByteBuffer buffer ) {
		this.offset = buffer.getInt();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptOpCode#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
	 */
	public void execute( final CxScriptRuntimeContext context ) {
		// TODO Auto-generated method stub
		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "GOTO %04X", offset );
	}

}
