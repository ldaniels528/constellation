package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;
import constellation.drawing.EntityRepresentation;

/**
 * Select Entity Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SelectEntityCommandDecoder implements CxCommandDecoder {
	
	/**
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		// extract the attributes of the command
		final EntityRepresentation entity = decodeRepresentation( buffer );
		
		// return the command
		return new SelectEntityCommand( entity );
	}

}