package constellation.tools.collaboration.commands;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandManager;
import constellation.commands.builtin.AddUserImageCommand;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.UserImage;
import constellation.model.GeometricModel;

/**
 * Constellation Collaborative Command Factory
 * @author lawrence.daniels@gmail.com
 */
public class CollaborativeCommandFactory {
	public static final Integer SEND_HOST_INFO	= 0x1000;
	public static final Integer SEND_MSG		= 0x1001;
	
	/**
	 * Register the Collaborative command decoders
	 */
	public static void init() {
		final CxCommandManager ccm = CxCommandManager.getInstance();
		ccm.register( SEND_HOST_INFO,	new SendHostInfoCommandDecoder() );
		ccm.register( SEND_MSG, 		new SendMessageCommandDecoder() );
	}

	/** 
	 * Creates an array of operations capable of creating the given 
	 * geometry on a remote host.
	 * @param model the given {@link GeometricModel model}
	 * @return a collection of synchronization {@link CxCommand commands}
	 */
	public static Collection<CxCommand> createModelSynchronizationCommands( final GeometricModel model ) {
		// gather the necessary objects to transfer
		final Set<UserImage> images = model.getUserImages();
		final Collection<ModelElement> elements = model.getPhysicalElements();
		
		// create a container for all the commands
		final Collection<CxCommand> commands = new LinkedList<CxCommand>();
		
		// add a host information operation
		commands.add( SendHostInfoCommand.create( model ) );
		
		// add the collection of user images
		for( final UserImage image : images ) {
			final CxCommand command = AddUserImageCommand.create( image );
			if( command != null ) {
				commands.add( command );
			}
		}
		
		// add the collection of elements
		for( final ModelElement element : elements ) {
			final CxCommand command = CxCommandManager.createAddCommand( element );
			if( command != null ) {
				commands.add( command );
			}
		}
		
		// return the requests
		return commands;
	}

}
