package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;
import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;
import constellation.drawing.entities.HUDXY;

/** 
 * Set Temporary Element HUD Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SetTempElementHUDCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		// decode the HUD
		final HUDXY hud = decodeHUD( buffer );		
		
		// return the command
		return new SetTempElementHUDCommand( hud );
	}

}
