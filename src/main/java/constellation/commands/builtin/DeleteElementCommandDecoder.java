package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/**
 * Delete Element Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class DeleteElementCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// extract the attributes of the command
		final String label = decodeString( buffer );
		
		// return the command
		return new DeleteElementCommand( label );
	}

}