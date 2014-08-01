package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;
import constellation.drawing.entities.UserImage;

/**
 * Add User Image Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class AddUserImageCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		// decode the user image attributes
		final String imageName	= decodeString( buffer ); 
		final byte[] content	= decodeContent( buffer );
		final UserImage image	= UserImage.createUserImage( imageName, content );
		
		// return the picture
		return new AddUserImageCommand( image );
	}

}