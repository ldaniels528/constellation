package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.ADD_IMG;
import static constellation.commands.CxCommandManager.encodeContent;
import static constellation.commands.CxCommandManager.encodeString;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.entities.UserImage;
import constellation.model.GeometricModel;

/**
 * Add User Image Command
 * @author lawrence.daniels@gmail.com
 */
public class AddUserImageCommand extends AbstractCommand {
	private final UserImage image;
	
	/**
	 * Creates a new "Add User Image" command
	 * @param image the given {@link UserImage user image}
	 */
	AddUserImageCommand( final UserImage image ) {
		this.image = image;
	}
	
	/**
	 * Creates a new "Add User Image" command
	 * @param image the given {@link UserImage user image}
	 * @return the {@link AddUserImageCommand command}
	 */
	public static AddUserImageCommand create( final UserImage image ) {
		return new AddUserImageCommand( image );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream out ) 
	throws IOException {
		// encode the command
		out.writeInt( ADD_IMG );
		encodeString( out, image.getName() );
		encodeContent( out, image.getContent() );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		// add the image to the model
		model.addUserImage( image );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] ADD_IMG '%s' [%dx%d]", 
				ADD_IMG, image.getName(), image.getWidth(), image.getHeight() );
	}
	
}
