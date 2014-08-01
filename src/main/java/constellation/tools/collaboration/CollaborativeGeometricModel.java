package constellation.tools.collaboration;

import static constellation.commands.CxCommandManager.*;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.commands.CxCommand;
import constellation.commands.CxCommandReader;
import constellation.commands.CxCommandWriter;
import constellation.commands.builtin.AddUserImageCommand;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.EntityNamingService;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.UserImage;
import constellation.model.DraftingStandards;
import constellation.model.Filter;
import constellation.model.GeometricModel;
import constellation.model.ModelChangeType;
import constellation.model.Unit;
import constellation.tools.collaboration.commands.CollaborativeCommandFactory;
import constellation.tools.collaboration.commands.RemoteMessagingDialog;
import constellation.tools.collaboration.commands.VirtualCommand;
import constellation.tools.collaboration.components.MessagingDialog;

/**
 * Represents a collaborative model; a model that is being
 * concurrently development by two or more individuals.
 * @author lawrence.daniels@gmail.com
 */
public class CollaborativeGeometricModel implements RemoteGeometricModel {
	private final Logger logger = Logger.getLogger( getClass() ); 
	private final IncomingCommandsThread incomingThread;
	private final OutgoingCommandsThread outgoingThread;
	private final LinkedList<CxCommand> outgoing;
	private final SessionDisconnectCallBack callBack;
	private final MessagingDialog messagingDialog;
	private final ApplicationController controller;
	private final GeometricModel model;
	private final Socket socket;
	private final String remoteID;
	private final String clientID;
	private String name;
	private boolean alive;
	
	/**
	 * Register the Collaborative remote commands
	 */
	static {
		CollaborativeCommandFactory.init();
	}
	
	/** 
	 * Creates a new collaborative model instance
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel model}
	 * @param socket the given {@link Socket socket}
	 * @param callBack the given {@link SessionDisconnectCallBack session disconnect call-back} routine
	 * @param isHost indicates whether the peer is the host
	 * @throws IOException 
	 */
	public CollaborativeGeometricModel( final ApplicationController controller, 
								    	final GeometricModel model, 
								    	final Socket socket, 
								    	final SessionDisconnectCallBack callBack, 
								    	final boolean isHost ) 
	throws IOException {
		this.alive				= true;
		this.controller			= controller;
		this.model				= model;
		this.socket 			= socket;
		this.callBack			= callBack;
		this.outgoing			= new LinkedList<CxCommand>();
		this.incomingThread		= new IncomingCommandsThread( socket.getInputStream() );
		this.outgoingThread		= new OutgoingCommandsThread( socket.getOutputStream() );
		this.messagingDialog	= new MessagingDialog( controller );
		this.clientID			= socket.getLocalAddress().getHostName();
		this.remoteID			= socket.getInetAddress().getHostName();
		this.name				= format( "%s@%s", model.getName(), ( isHost ? clientID : remoteID ) );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public List<ModelElement> addPhysicalElement( final EntityRepresentation... representations ) {
		// add the internal representations to the model
		final List<ModelElement> elements = model.addPhysicalElement( representations );
		
		// queue the 'Add' operations
		queue( createAddCommands( elements ) );
		return elements;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhysicalElement( final ModelElement... elements ) {
		// add the geometry array to the model
		model.addPhysicalElement( elements );
		
		// queue the 'Add' operations
		queue( createAddCommands( elements ) );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhysicalElements( final Collection<? extends ModelElement> elements ) {
		// add the geometry array to the model
		model.addPhysicalElements( elements );
		
		// queue the 'Add' operations
		queue( createAddCommands( elements ) );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhantoms( final ModelElement ... elements ) {
		model.addPhantoms( elements );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhantoms( final Collection<? extends ModelElement> geometryCollection ) {
		model.addPhantoms( geometryCollection );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int erasePhantom( final ModelElement... elements ) {
		return model.erasePhantom( elements );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int erasePhantoms( final Collection<ModelElement> elements ) {
		return model.erasePhantoms( elements );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void clearSelectedElements() {
		model.clearSelectedElements();
	}

	/** 
	 * {@inheritDoc}
	 */
	public int erase( final ModelElement... geometryArray ) {
		return erase( Arrays.asList( geometryArray ) );
	}

	/** 
	 * {@inheritDoc}
	 */
	public int erase( final Collection<ModelElement> elements ) {
		final int count = model.erase( elements );
		if( count > 0 ) {
			queue( createDeleteCommands( elements ) );
		}
		return count;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public long getLastChangeTime( final ModelChangeType type ) {
		return model.getLastChangeTime( type );
	}

	/** 
	 * {@inheritDoc}
	 */
	public Collection<ModelElement> getPhysicalElements() {
		return model.getPhysicalElements();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getVisibleElements(Collection<ModelElement> container) {
		model.getVisibleElements( container );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getVisibleElements( Collection<ModelElement> container, EntityTypes ... types ) {
		model.getVisibleElements( container, types );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement lookupElementByLabel( final String label ) {
		return model.lookupElementByLabel( label );
	}

	/** 
	 * {@inheritDoc}
	 */
	public File getModelFile() {
		return model.getModelFile();
	}

	/** 
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setName( final String name ) {
		// set the new name
		this.name = name;
		
		// update the title
		controller.updateTitle( this );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public EntityNamingService getNamingService() {
		return model.getNamingService();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getAuthorName() {
		return model.getAuthorName();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setAuthorName( final String authorName ) {
		model.setAuthorName( authorName );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getAuthorOrganization() {
		return model.getAuthorOrganization();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setAuthorOrganization( final String authorOrganization ) {
		model.setAuthorOrganization( authorOrganization ); 
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return model.getDescription();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setDescription( final String description ) {
		model.setDescription( description );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addFilter( final Filter filter ) {
		model.addFilter( filter );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Filter getFilter() {
		return model.getFilter();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setFilter( final Filter filter ) {
		model.setFilter( filter );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Collection<Filter> getFilters() {
		return model.getFilters();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public boolean isVirtual() {
		return alive;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getClientID() {
		return clientID;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getRemoteID() {
		return remoteID;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.CxDistributedModel#getHostModel()
	 */
	public GeometricModel getHostModel() {
		return model;
	}

	/** 
	 * {@inheritDoc}
	 */
	public Color getDefaultColor() {
		return model.getDefaultColor();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultColor( final Color color ) {
		model.setDefaultColor( color );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int getDefaultLayer() {
		return model.getDefaultLayer();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultLayer( final int layer ) {
		model.setDefaultLayer( layer );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public LinePatterns getDefaultPattern() {
		return model.getDefaultPattern();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultPattern( final LinePatterns pattern ) {
		model.setDefaultPattern( pattern );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int getDefaultThickness() {
		return model.getDefaultThickness();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultThickness( final int defaultThickness ) {
		model.setDefaultThickness( defaultThickness );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public DraftingStandards getDraftingStandard() {
		return model.getDraftingStandard();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDraftingStandard( final DraftingStandards draftingStandard ) {
		model.setDraftingStandard( draftingStandard );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public double getScale() {
		return model.getScale();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setScale( final double scale ) {
		model.setScale( scale );
	}

	/** 
	 * {@inheritDoc}
	 */
	public Unit getUnit() {
		return model.getUnit();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setUnit( final Unit unit ) {
		model.setUnit( unit );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Image getBackgroundImage() {
		return model.getBackgroundImage();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setBackgroundImage( final Image image ) {
		model.setBackgroundImage( image );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addUserImage( final UserImage image ) {
		model.addUserImage( image );
		
		// queue the synchronization event
		queue( AddUserImageCommand.create( image ) );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Set<UserImage> getUserImages() {
		return model.getUserImages();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public UserImage lookupUserImage( final String imageName ) {
		return model.lookupUserImage( imageName );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void removeUserImage( final String imageName ) {
		model.removeUserImage( imageName );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.model.GeometricModel#getPhantomElements(java.util.Collection)
	 */
	public void getPhantomElements( final Collection<ModelElement> container ) {
		model.getPhantomElements( container );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void clearHighlightedGeometry() {
		model.clearHighlightedGeometry();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Collection<ModelElement> getHighlightedGeometry() {
		return model.getHighlightedGeometry();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setHighlightedGeometry( final Collection<ModelElement> elements ) {
		model.setHighlightedGeometry( elements );
	}

	/** 
	 * {@inheritDoc}
	 */
	public RenderableElement getTemporaryElement() {
		return model.getTemporaryElement();
	}

	/** 
	 * {@inheritDoc}
	 */
	public boolean isPhantomsEnabled() {
		return model.isPhantomsEnabled();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void clearTemporaryElement() {
		model.clearTemporaryElement();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void clearPickedElement() {
		model.clearPickedElement();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.GeometricModel#getPickedGeometry()
	 */
	public ModelElement getPickedElement() {
		return model.getPickedElement();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setPickedElement( final ModelElement element ) {
		model.setPickedElement( element );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int getSelectedElementCount() {
		return model.getSelectedElementCount();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void getSelectedGeometry( final Collection<RenderableElement> collection ) {
		model.getSelectedGeometry( collection );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getSelectedGeometry( final RenderableElement[] elements ) {
		model.getSelectedGeometry( elements );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void selectAll() {
		model.selectAll();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void selectGeometry( final RenderableElement element ) {
		model.selectGeometry( element );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void selectGeometry( final Collection<? extends RenderableElement> elements ) {
		model.selectGeometry( elements );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setModelFile( final File modelFile ) {
		model.setModelFile( modelFile );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setPhantomMode( final boolean enabled ) {
		model.setPhantomMode( enabled ); 
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setTemporaryElement( final RenderableElement temporaryGeometry ) {
		model.setTemporaryElement( temporaryGeometry );
	}
	
	///////////////////////////////////////////////////////////////////////////
	//		Connectivity Method(s)
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void close() {
		if( alive ) {
			// process is not set to die
			alive = false;
			
			// shutdown the threads
			incomingThread.interrupt();
			outgoingThread.interrupt();
			
			// close the socket
			try { socket.close(); } catch( final Exception e ) { }
			
			// re-point to the original model
			controller.setModel( model );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public RemoteMessagingDialog getMessagingDialog() {
		return messagingDialog;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//		Queuing Method(s)
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void queue( final Collection<CxCommand> commands ) {
		synchronized( outgoing ) {
			outgoing.addAll( commands );
			outgoing.notifyAll();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void queue( final CxCommand ... commands ) {
		synchronized( outgoing ) {
			outgoing.addAll( Arrays.asList( commands ) );
			outgoing.notifyAll();
		}
	}
	
	/**
	 * Determines whether the given command is marked as "virtual"
	 * @param command the given {@link CxCommand command}
	 * @return true, if the command's class implements the 
	 * {@link VirtualCommand virtual command annotation}
	 */
	private boolean isVirtual( final CxCommand command ) {
		// does the command has the "virtual command" annotation?
		final VirtualCommand virtualCommand = 
			command.getClass().getAnnotation( VirtualCommand.class );
		
		// is the virtual flag set?
		return ( virtualCommand != null ) ? virtualCommand.value() : false;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//		Inner Classes
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Incoming Commands Thread
	 * @author lawrence.daniels@gmail.com
	 */
	private class IncomingCommandsThread extends Thread {
		private final CxCommandReader reader;
		
		/** 
		 * Creates a new incoming objects thread
		 * @param in the given {@link InputStream input stream}
		 */
		public IncomingCommandsThread( final InputStream in ) {
			this.reader	= new CxCommandReader( in );
			super.start();
		}

		/** 
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			logger.info( "Started incoming commands thread" );
			while( alive ) {
				try {	
					// get the next command from the stream
					final CxCommand command = reader.read();
					
					// if a command was retrieved ...
					if( command != null ) {
						// alert the operator
						if( logger.isInfoEnabled() ) {
							logger.info( format( "[READ] %s (%d bytes)", command, command.getLength() ) );
						}
						
						// if the command is virtual, use the collaborative model instead
						command.evaluate( isVirtual( command ) ? CollaborativeGeometricModel.this : model );
						controller.requestRedraw();
					}
				}
				catch( final Exception e ) {
					controller.showErrorDialog( "Collaborative Session", "Connection broken", e );
					close();
				}
			}
			
			// notify the caller
			callBack.disconnected();
			logger.info( "Thread died" );
		}		
	}
	
	/** 
	 * Outgoing Commands Thread
	 * @author lawrence.daniels@gmail.com
	 */
	private class OutgoingCommandsThread extends Thread {
		private final CxCommandWriter writer;
		
		/** 
		 * Creates a new outgoing objects thread
		 * @param out the given {@link OutputStream output stream}
		 */
		public OutgoingCommandsThread( final OutputStream out ) {
			this.writer	 = new CxCommandWriter( out );
			super.start();
		}

		/** 
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			logger.info( "Started outgoing commands thread" );
			while( alive ) {
				// get the next synchronization command from the queue
				final CxCommand command = getNextCommand();
				
				// if a request was retrieved, transfer it ...
				if( command != null ) {
					try {
						// write the command
						writer.write( command );
						
						// alert the operator
						if( logger.isInfoEnabled() ) {
							logger.info( format( "[WROTE] %s (%d bytes)", command, command.getLength() ) );
						}
					}
					catch( final Exception e ) {
						controller.showErrorDialog( "Collaborative Session", "Connection broken", e );
						close();
					}
				}
			}
			
			// notify the caller
			callBack.disconnected();
			logger.info( "Thread is dead" );
		}
		
		/** 
		 * Returns the next synchronization command from the queue
		 * @return the next {@link CxCommand command}
		 */
		private CxCommand getNextCommand() {
			CxCommand request = null;
			synchronized( outgoing ) {
				// wait until the queue is no longer empty ...
				while( outgoing.isEmpty() ) {
					try { outgoing.wait(); } catch( Exception e ) { }
				}
				
				// is there an object to retrieve?
				request = !outgoing.isEmpty() ? outgoing.removeFirst() : null;
					
				// notify all others
				outgoing.notifyAll();
			}
			return request;
		}
	}
	
}
