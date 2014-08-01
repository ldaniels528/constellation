package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.decodeString;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/**
 * Select Element Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SelectElementCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// extract the attributes of the command
		final String label = decodeString( buffer );
		
		// return the command
		return new SelectElementCommand( label );
	}

}