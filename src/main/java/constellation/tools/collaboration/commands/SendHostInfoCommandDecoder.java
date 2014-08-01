package constellation.tools.collaboration.commands;

import static constellation.commands.CxCommandManager.*;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/**
 * Send Host Information Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SendHostInfoCommandDecoder implements CxCommandDecoder {
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.CxCommandDecoder#decode(java.nio.ByteBuffer)
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// extract the attributes of the command
		final String modelName = decodeString( buffer );
		
		// return the command
		return new SendHostInfoCommand( modelName );
	}

}