package constellation.tools.demopro;

import static constellation.commands.CxCommandManager.createAddCommands;
import static constellation.commands.CxCommandManager.createDeleteCommands;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.commands.CxCommand;
import constellation.commands.CxCommandReader;
import constellation.commands.VirtualModel;
import constellation.commands.builtin.AddUserImageCommand;
import constellation.drawing.EntityNamingService;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.UserImage;
import constellation.model.DraftingStandards;
import constellation.model.Filter;
import constellation.model.GeometricModel;
import constellation.model.ModelChangeType;
import constellation.model.Unit;
import constellation.tools.demopro.plugin.RecordingPlugin;

/**
 * Constellation DemoPro Play-back Model
 * @author lawrence.daniels@gmail.com
 */
public class PlaybackModel implements VirtualModel {
	private final Logger logger = Logger.getLogger( getClass() );
	private final RecordingPlugin recorder;
	private final ApplicationController controller;
	private final DataInputStream in;
	private final PlaybackThead thread;
	private final GeometricModel model;

	/**
	 * Creates a new play-back model instance
	 * @param model the given {@link GeometricModel host model}
	 * @param controller the given {@link ApplicationController controller}
	 * @param recorder the given {@link RecordingPlugin recorder}
	 * @param in the given recording {@link InputStream input stream}
	 */
	public PlaybackModel( final GeometricModel model, 
						  final ApplicationController controller, 
						  final RecordingPlugin recorder, 
						  final InputStream in ) {
		this.recorder	= recorder;
		this.controller	= controller;
		this.in			= new DataInputStream( new BufferedInputStream( in, 8192 ) );
		this.thread		= new PlaybackThead();
		this.model		= model;
		
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
	public void getPhantomElements( final Collection<ModelElement> container) {
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
	public void clearTemporaryElement() {
		model.clearTemporaryElement();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void clearPickedElement() {
		model.clearPickedElement();
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
	public void setPickedElement( final ModelElement element ) {
		model.setPickedElement( element );
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
	public void close() {
		try {
			in.close();
		}
		catch( final Exception e ) {
			logger.error( "Error closing stream", e );
		}
	}
		
	/** 
	 * {@inheritDoc}
	 */
	public void queue( final Collection<CxCommand> commands ) {
		// do nothing
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void queue( final CxCommand ... commands ) {
		// do nothing
	}
	
	///////////////////////////////////////////////////////////////////////////
	//		Recording Thread
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * This thread is responsible for reading commands
	 * previously written to disk.
	 * @author lawrence.daniels@gmail.com
	 */
	private class PlaybackThead extends Thread {
		private CxCommandReader reader;
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			// open a new command reader
			reader = new CxCommandReader( in );
			
			// read commands until empty ...
			CxCommand command;
			while( ( command = nextCommand() ) != null ) {
				// evaluate the command 
				command.evaluate( model );
				
				// redraw the scene
				controller.requestRedraw();
			}
			
			// stop the timer
			recorder.stopPlaybackOrRecording();
		}
		
		/**
		 * Retrieves the next command from the stream
		 * @return the {@link CxCommand command}
		 */
		private CxCommand nextCommand() {
			CxCommand command = null;
			
			// read the next command
			try {
				command = reader.read();
			}
			catch( final Exception e ) {
				logger.error( "Error reading command from stream",  e );
			}
			
			return command;
		}
	}
	
}
