package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.decodeString;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/**
 * Select Elements Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SelectElementsCommandDecoder implements CxCommandDecoder {
	
	/**
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// get the number of elements
		final short count = buffer.getShort();
		
		// retrieve the element labels
		final String[] labels = new String[ count ];
		for( int n = 0; n < count; n++ ) {
			labels[n] = decodeString( buffer );
		}
		
		// return the command
		return new SelectElementsCommand( labels );
	}

}