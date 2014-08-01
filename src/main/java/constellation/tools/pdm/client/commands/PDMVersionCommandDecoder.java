package constellation.tools.pdm.client.commands;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/**
 * Constellation Server PDM Version Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class PDMVersionCommandDecoder implements CxCommandDecoder {

	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// get the version information
		final int majorVersion 	= buffer.getShort();
		final int minorVersion	= buffer.getShort();
		
		// return the command
		return new PDMVersionCommand( majorVersion, minorVersion );
	}

}
