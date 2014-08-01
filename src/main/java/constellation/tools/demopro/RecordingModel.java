package constellation.tools.demopro;

import static constellation.commands.CxCommandManager.*;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandWriter;
import constellation.commands.VirtualModel;
import constellation.commands.builtin.AddUserImageCommand;
import constellation.commands.builtin.ClearPickedElementCommand;
import constellation.commands.builtin.ClearSelectedElementsCommand;
import constellation.commands.builtin.ClearTempElementCommand;
import constellation.commands.builtin.SelectAllCommand;
import constellation.commands.builtin.SelectElementCommand;
import constellation.commands.builtin.SelectElementsCommand;
import constellation.commands.builtin.SelectEntityCommand;
import constellation.commands.builtin.SetPickedCommand;
import constellation.commands.builtin.SetTempElementCommand;
import constellation.commands.builtin.SetTempElementHUDCommand;
import constellation.commands.builtin.WaitCommand;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.EntityNamingService;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.UserImage;
import constellation.model.DraftingStandards;
import constellation.model.Filter;
import constellation.model.GeometricModel;
import constellation.model.ModelChangeType;
import constellation.model.Unit;

/**
 * Constellation DemoPro Recording Model
 * @author lawrence.daniels@gmail.com
 */
public class RecordingModel implements VirtualModel {
	private final Logger logger = Logger.getLogger( getClass() );
	private final LinkedList<CxCommand> queue;
	private final DataOutputStream out;
	private final RecordingThead thread;
	private final GeometricModel model;
	private boolean alive;
	
	/**
	 * Creates a new recording model instance
	 * @param model the given {@link GeometricModel host model}
	 * @param out the given recording {@link OutputStream output stream}
	 */
	public RecordingModel( final GeometricModel model, final OutputStream out ) {
		this.queue	= new LinkedList<CxCommand>();
		this.thread	= new RecordingThead();
		this.out	= new DataOutputStream( new BufferedOutputStream( out, 8192 ) );
		this.model	= model;
		
		// start the thread
		thread.start();
	}

	/** 
	 * {@inheritDoc}
	 */
	public GeometricModel getHostModel() {
		return model;
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
	public long getLastChangeTime( final ModelChangeType type ) {
		return model.getLastChangeTime( type );
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
		return model.getName();
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
		return true;
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
		// add the user image
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
	
	/** 
	 * {@inheritDoc}
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
	public void setTemporaryElement( final RenderableElement element ) {
		// is the element null?
		if( element == null ) {
			queue( ClearTempElementCommand.create() );
		}
		
		// is the element a HUD?
		else if( element instanceof HUDXY ) {
			queue( SetTempElementHUDCommand.create( (HUDXY)element ) );
		}
		
		// for all other elements...
		else {
			queue( SetTempElementCommand.create( element ) );
		}
		
		// set the temporary element
		model.setTemporaryElement( element );
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
	public void clearPickedElement() {
		model.clearPickedElement();
		
		// schedule the command
		queue( ClearPickedElementCommand.create() );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement getPickedElement() {
		return model.getPickedElement();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setPickedElement( final ModelElement element ) {
		model.setPickedElement( element );
		
		// schedule the command
		queue( SetPickedCommand.create( element ) );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void clearSelectedElements() {
		model.clearSelectedElements();
		
		// schedule the command
		queue( ClearSelectedElementsCommand.create() );
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
		
		// schedule the command
		queue( SelectAllCommand.create() );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void selectGeometry( final RenderableElement element ) {
		model.selectGeometry( element );
		
		// schedule the command
		if( element instanceof ModelElement ) {
			queue( SelectElementCommand.create( (ModelElement)element ) );
		}
		else if( element instanceof EntityRepresentation ) {
			queue( SelectEntityCommand.create( (EntityRepresentation)element ) );
		}
		else {
			logger.error( format( "Unhandled type '%s'", element.getClass().getName() ) );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void selectGeometry( final Collection<? extends RenderableElement> elements ) {
		model.selectGeometry( elements );
		
		// schedule the command
		try {
			// TODO revisit this logic
			queue( SelectElementsCommand.create( (Collection<ModelElement>)elements ) );
		}
		catch( ClassCastException e ) {
			logger.error( format( "Unhandled type '%s'", elements.getClass().getName(), e ) );
		}
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
	public void clearTemporaryElement() {
		queue( ClearTempElementCommand.create() );
		model.clearTemporaryElement();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			out.close();
		}
		catch( final Exception e ) {
			logger.error( "Error closing stream", e );
		}
	}
		
	/** 
	 * {@inheritDoc}
	 */
	public void queue( final Collection<CxCommand> commands ) {
		synchronized( queue ) {
			queue.addAll( commands );
			queue.notifyAll();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void queue( final CxCommand ... commands ) {
		synchronized( queue ) {
			queue.addAll( Arrays.asList( commands ) );
			queue.notifyAll();
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//		Recording Thread
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * This thread is responsible for persisting commands
	 * from the user to disk.
	 * @author lawrence.daniels@gmail.com
	 */
	private class RecordingThead extends Thread {
		private CxCommandWriter writer;
		private long lastReceived;
		
		/** 
		 * {@inheritDoc}
		 */
		public void run() {
			// the process is now alive
			lastReceived = System.currentTimeMillis();
			alive = true;
			
			// open a command writer
			writer = new CxCommandWriter( out );
			
			while( alive ) {
				// get the next command from queue
				final CxCommand command = getNextCommand();
				
				// write the command to the stream
				if( command != null ) {
					// get the current time
					final long currentTime = System.currentTimeMillis();
					
					// get the elapsed time
					final long elapsedTime = currentTime - lastReceived; 
					
					// persist the elapsed time 
					persistCommand( WaitCommand.create( elapsedTime ) );
					
					// persist the command
					persistCommand( command );
					
					// update last received
					lastReceived = currentTime;
				}
			}
		}
		
		/**
		 * Retrieves the next command from the queue
		 * @return the {@link CxCommand command}
		 */
		private CxCommand getNextCommand() {
			CxCommand command = null;
			
			synchronized( queue ) {
				// wait for at least 1 command
				while( alive && queue.isEmpty() ) {
					try { queue.wait( 15000L ); } 
					catch( InterruptedException e ) {
						logger.error( "Thread was interrupted while waiting for queue to fill", e );
					}
				}
				
				// get the command
				command = !queue.isEmpty() ? queue.removeFirst() : null;
			}
			
			return command;
		}
		
		/**
		 * Persists the command to the output stream
		 * @param command the given {@link CxCommand command}
		 */
		private void persistCommand( final CxCommand command ) {
			try {
				writer.write( command );
			} 
			catch( final IOException e ) {
				logger.error( format( "Error persisting command '%s'", command.toString() ), e );
			}
		}
		
	}
	
}
