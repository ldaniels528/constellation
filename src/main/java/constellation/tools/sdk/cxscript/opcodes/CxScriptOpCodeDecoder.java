package constellation.tools.sdk.cxscript.opcodes;

import static java.lang.String.format;

import java.nio.ByteBuffer;

/**
 * Constellation Script OpCode Decoder
 * @author lawrence.daniels@gmail.com
 */
public class CxScriptOpCodeDecoder {
	public static final short OP_GOTO 		= 0x0000;
	public static final short OP_INVOK 		= 0x0100;
	public static final short OP_MSGSTAT	= 0x0200;
	public static final short OP_MSGINFO	= 0x0201;
	public static final short OP_TERM 		= 0x7FFF;
	
	/**
	 * Decodes the next instruction from the buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link CxScriptOpCode opCode}
	 */
	public CxScriptOpCode decode( final ByteBuffer buffer ) {
		// get the opCode
		final short opCode = buffer.getShort();
		
		// decode the instruction
		switch( opCode ) {
			case OP_GOTO:		return new GOTO( buffer );
			case OP_INVOK:		return new INVOK( buffer );
			case OP_MSGINFO:	return new MSGINFO( buffer );
			case OP_MSGSTAT:	return new MSGSTAT( buffer );
			case OP_TERM:		return new TERM();
			default:		
				throw new IllegalArgumentException( format( "Illegal instruction code '%04X'", opCode ) );
		}
	}
	
}
