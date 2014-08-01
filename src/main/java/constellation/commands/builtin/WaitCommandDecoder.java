package constellation.commands.builtin;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/** 
 * Time Delay Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class WaitCommandDecoder implements CxCommandDecoder {

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.CxCommandDecoder#decode(java.nio.ByteBuffer)
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// get the delay value
		final long delay = buffer.getLong();
		
		// return the command
		return new WaitCommand( delay );
	}

}
