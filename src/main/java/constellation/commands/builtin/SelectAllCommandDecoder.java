package constellation.commands.builtin;

import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/** 
 * "Select All" Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SelectAllCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		return SelectAllCommand.create();
	}

}
