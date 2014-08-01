package constellation.model;

import static constellation.drawing.LinePatterns.PATTERN_SOLID;
import static constellation.drawing.LinePatterns.PATTERN_UNSPEC;
import static constellation.model.DraftingStandards.STD_ISO;
import static constellation.model.Units.UNITS_MILLIMETERS;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constellation.CxConfigurationUtil;
import constellation.drawing.EntityNamingService;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.UserImage;
import constellation.model.formats.cxm.CXMFormatReader;

/**
 * Constellation Default Geometric Model
 * @author lawrence.daniels@gmail.com
 */
public class DefaultGeometricModel implements GeometricModel {
	// constants
	private static final Collection<ModelElement> NO_RESULT = new ArrayList<ModelElement>(0);
	
	// immutable fields
	private final DrawingElementCollection collection;
	private final EntityNamingService namingService;
	private final HashSet<ModelElement> phantomElements;
	private final Set<UserImage> userImages;
	private final Set<Filter> filters;
	
	// selection related fields
	private final LinkedList<RenderableElement> selectedElements;
	private Collection<ModelElement> highlightedElements;
	private RenderableElement temporaryElement;
	private ModelElement pickedElement;
	
	// mutable fields
	private DraftingStandards draftingStandard;
	private LinePatterns defaultPattern;
	private Color defaultColor;
	private int defaultThickness;
	private int defaultLayer; 
	private Image backgroundImage;
	private String description;
	private String authorName;
	private String authorOrganization;
	private double scale;
	private Filter filter;
	private File modelFile;
	private Unit unit;
	
	// indicator fields
	private long lastChangeTimeVisual;
	private long lastChangeTimePhysical;
	private long lastChangeTimeInformational;
	private boolean keepPhantoms;
	
	/////////////////////////////////////////////////////////////////////
	//		Constructor(s)
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a new model
	 * @param modelFile the given model file
	 * @param unit the given {@link Unit model unit}
	 */
	private DefaultGeometricModel( final File modelFile, final Unit unit ) {
		this.unit						= unit;
		this.collection 				= new DrawingElementCollection();
		this.namingService				= new EntityNamingService();
		this.selectedElements			= new LinkedList<RenderableElement>();
		this.phantomElements			= new HashSet<ModelElement>();
		this.userImages					= new LinkedHashSet<UserImage>();
		this.filters					= new LinkedHashSet<Filter>();
		this.keepPhantoms				= true;
		
		// set default attributes
		this.defaultColor				= Color.BLACK;
		this.defaultPattern				= PATTERN_SOLID;
		this.defaultThickness			= 0;
		this.draftingStandard			= STD_ISO;
		this.scale						= 1.0;
		this.backgroundImage			= null;
	
		// set the model file
		setModelFile( modelFile );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Creation Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a new empty model
	 * @return a new empty {@link DefaultGeometricModel model}
	 */
	public static DefaultGeometricModel newModel() {
		return newModel( getDefaultModelPath(), UNITS_MILLIMETERS );
	}
	
	/**
	 * Creates a new empty model
	 * @param modelFile the given model file
	 * @return a new empty {@link DefaultGeometricModel model}
	 */
	public static DefaultGeometricModel newModel( final File modelFile ) {
		return newModel(  modelFile, UNITS_MILLIMETERS );
	}
	
	/**
	 * Creates a new empty model
	 * @param modelFile the given model file
	 * @param unit the given {@link Unit model unit}
	 * @return a new empty {@link DefaultGeometricModel model}
	 */
	public static DefaultGeometricModel newModel( final File modelFile, final Unit unit  ) {
		return new DefaultGeometricModel( modelFile, unit );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Object Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return getName();
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Basic Informational Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public String getName() {
		final String name = modelFile.getName();
		final int index = name.indexOf( '.' );
		return name.substring( 0, index );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public EntityNamingService getNamingService() {
		return namingService;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getAuthorName() {
		return authorName;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setAuthorName( final String authorName ) {
		this.authorName = authorName;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getAuthorOrganization() {
		return authorOrganization;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setAuthorOrganization( final String authorOrganization ) {
		this.authorOrganization = authorOrganization;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return description;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setDescription( final String description ) {
		this.description = description;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public boolean isVirtual() {
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Model File Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public File getModelFile() {
		return modelFile;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setModelFile( final File modelFile ) {
		this.modelFile = CXMFormatReader.fixFilePath( modelFile );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Layer Management Methods
	/////////////////////////////////////////////////////////////////////

	/** 
	 * {@inheritDoc}
	 */
	public void addFilter( Filter filter ) {
		synchronized( filters ) {
			filters.add( filter );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Collection<Filter> getFilters() {
		return filters;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Filter getFilter() {
		return filter;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setFilter( final Filter filter ) {
		this.filter = filter;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int getDefaultLayer() {
		return defaultLayer;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultLayer( final int layer ) {
		this.defaultLayer = layer;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Model Scale Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public Color getDefaultColor() {
		return defaultColor;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultColor( final Color color ) {
		this.defaultColor = color;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public LinePatterns getDefaultPattern() {
		return defaultPattern;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultPattern( final LinePatterns pattern ) {
		this.defaultPattern = pattern;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int getDefaultThickness() {
		return defaultThickness;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void setDefaultThickness(int defaultThickness) {
		this.defaultThickness = defaultThickness;
	}

	/** 
	 * {@inheritDoc}
	 */
	public Image getBackgroundImage() {
		return backgroundImage;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setBackgroundImage( final Image image ) {
		this.backgroundImage = image;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public DraftingStandards getDraftingStandard() {
		return draftingStandard;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setDraftingStandard( final DraftingStandards draftingStandard ) {
		this.draftingStandard = draftingStandard;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public double getScale() {
		return scale;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setScale( final double scale ) {
		this.scale = scale;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Unit getUnit() {
		return unit;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setUnit( final Unit unit ) {
		this.unit = unit;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		User Image Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void addUserImage( final UserImage image ) {
		userImages.add( image );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Set<UserImage> getUserImages() {
		return userImages;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public UserImage lookupUserImage( final String imageName ) {
		for( final UserImage image : userImages ) {
			if( image.getName().equals( imageName ) ) {
				return image;
			}
		}
		return null;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void removeUserImage( final String imageName ) {
		userImages.remove( imageName );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Element C.R.U.D. Methods
	/////////////////////////////////////////////////////////////////////

	/** 
	 * {@inheritDoc}
	 */
	public List<ModelElement> addPhysicalElement( final EntityRepresentation... representations ) {
		// create a container for the elements
		final List<ModelElement> elements = new ArrayList<ModelElement>( representations.length );
		
		// create a collection of wrapped element
		for( final EntityRepresentation representation : representations ) {
			elements.add( new CxModelElement( representation ) );
		}
		
		// add the elements to the model
		addPhysicalElements( elements );
		
		// return the "wrapped" elements to the caller
		return elements;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhysicalElement( final ModelElement ... elements ) {
	 	addPhysicalElements( Arrays.asList( elements ) );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhysicalElements( final Collection<? extends ModelElement> elements ) {
		// update the geometry defaults
	 	for( final ModelElement element : elements ) {	
	 		// update the elements defaults
	 		updateDefaults( element );
		}
	 	
	 	// add the elements to the collection
	 	collection.addAll( elements );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Collection<ModelElement> getPhysicalElements() {
		return collection.getAllLayers();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getVisibleElements( final Collection<ModelElement> container ) {
		collection.filter( filter, container );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getVisibleElements( Collection<ModelElement> container, EntityTypes ... types ) {
		// build the filter set
		final Set<EntityTypes> typeSet = new HashSet<EntityTypes>( Arrays.asList( types ) );
		
		// populate the elements
		collection.filter( filter, container, typeSet );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public long getLastChangeTime( final ModelChangeType type ) {
		switch( type ) {
			case VISUAL:
				return lastChangeTimeVisual;
				
			case PHYSICAL:
				return lastChangeTimePhysical;
				
			case INFORMATIONAL:
				return lastChangeTimeInformational;
				
			default:
				throw new IllegalArgumentException( format( "Unhandled model change type '%s'", type ) );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int erase( final Collection<ModelElement> elements ) {
		int count = 0;
		for( final ModelElement element : elements ) {
			if( collection.remove( element ) ) { count++; }
		}
		
		// record the fact that the model has been modified
		modelChanged( ModelChangeType.PHYSICAL );
		
		// return the count
		return count;
	}

	/** 
	 * {@inheritDoc}
	 */
	public int erase( final ModelElement... elements ) {
		int count = 0;
		for( ModelElement element : elements ) {
			if( collection.remove( element ) ) { count++; }
		}
		
		// record the fact that the model has been modified
		modelChanged( ModelChangeType.PHYSICAL );
		
		// return the count
		return count;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement lookupElementByLabel( final String label ) {
		return collection.lookupElementByLabel( label );
	}
		
	/////////////////////////////////////////////////////////////////////
	//		Picked Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void clearPickedElement() {
		this.pickedElement = null;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement getPickedElement() {
		return pickedElement;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setPickedElement( final ModelElement element ) {
		this.pickedElement = element;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Element Selection Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void clearSelectedElements() {
		synchronized( selectedElements ) {
			selectedElements.clear();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int getSelectedElementCount() {
		synchronized( selectedElements ) {
			return selectedElements.size();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getSelectedGeometry( final Collection<RenderableElement> collection ) {
		synchronized( selectedElements ) {
			collection.addAll( selectedElements );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getSelectedGeometry( final RenderableElement[] elements ) {
		synchronized( selectedElements ) {
			selectedElements.toArray( elements );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void selectGeometry( final RenderableElement element ) {
		synchronized( selectedElements ) {
			if( !selectedElements.contains( element ) ) {
				selectedElements.add( element );
			}
			else {
				selectedElements.remove( element );
			}
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void selectGeometry( final Collection<? extends RenderableElement> elements ) {
		// create a set to filter out duplicates
		final Set<? extends RenderableElement> set = 
			new LinkedHashSet<RenderableElement>( elements );
		
		// add all of the elements to the selection
		synchronized( selectedElements ) {	
			selectedElements.addAll( set );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void selectAll() {
		synchronized( selectedElements ) {
			selectedElements.clear();
			selectedElements.addAll( collection );
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Highlighted Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void clearHighlightedGeometry() {
		this.highlightedElements = NO_RESULT;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Collection<ModelElement> getHighlightedGeometry() {
		return highlightedElements;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setHighlightedGeometry( final Collection<ModelElement> elements ) {
		this.highlightedElements = ( elements != null ) ? elements : NO_RESULT;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Temporary Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void clearTemporaryElement() {
		this.temporaryElement = null;
	}

	/** 
	 * {@inheritDoc}
	 */
	public RenderableElement getTemporaryElement() {
		return temporaryElement;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setTemporaryElement( final RenderableElement temporaryGeometry ) {
		this.temporaryElement = temporaryGeometry;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Phantom Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhantoms( final ModelElement ... elements ) {
		addPhantoms( Arrays.asList( elements ) );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void addPhantoms( final Collection<? extends ModelElement> elements ) {
		synchronized( phantomElements ) {
			phantomElements.addAll( elements );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int erasePhantom( final ModelElement... elements ) {
		int count = 0;
		for( ModelElement element : elements ) {
			synchronized( phantomElements ) {
				final boolean removed = phantomElements.remove( element );
				if( removed ) { count++; }
			}
		}
		return count;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public int erasePhantoms( final Collection<ModelElement> elements ) {
		int count = 0;
		for( final ModelElement element : elements ) {
			synchronized( phantomElements ) {
				final boolean removed = phantomElements.remove( element );
				if( removed ) { count++; }
			}
		}
		return count;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void getPhantomElements( final Collection<ModelElement> container ) {
		// if the container is not empty, clear it
		if( !container.isEmpty() ) {
			container.clear();
		}
		
		// add the phantom elements
		synchronized( phantomElements ) {
			container.addAll( phantomElements );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public boolean isPhantomsEnabled() {
		return keepPhantoms;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setPhantomMode( final boolean enabled ) {
		this.keepPhantoms = enabled;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Internal Methods
	/////////////////////////////////////////////////////////////////////

	/**
	 * Returns the file reference to the default model
	 * @return the {@link File file} reference
	 */
	private static File getDefaultModelPath() {
		// get the configuration instance
		final CxConfigurationUtil config = CxConfigurationUtil.getInstance();
		
		// get a reference to the model file
		return config.getModelReference( "Untitled.cxm" );
	}
	
	/**
	 * Automatically labels the geometric element
	 * @param geometry the geometry to be labeled
	 * @return the {@link ModelElement drawing element}
	 */
	private void autoLabel( final ModelElement geometry ) {
		geometry.setLabel( namingService.getEntityName( geometry.getCategoryType() ) );
	}
	
	/**
	 * Sets the last changed time for the model
	 * @param type the given {@link ModelChangeType change type}
	 */
	private void modelChanged( final ModelChangeType type ) {
		// capture the current time
		final long currentTime 	= System.currentTimeMillis();
		
		switch( type ) {
			// record the last visual change time
			case VISUAL:
				lastChangeTimeVisual = currentTime;
				break;
		
			// record the physical change time
			case PHYSICAL:
				lastChangeTimeVisual = currentTime;
				lastChangeTimePhysical = currentTime;
				break;
				
			// record the physical change time
			case INFORMATIONAL:
				lastChangeTimePhysical = currentTime;
				lastChangeTimePhysical = currentTime;
				break;
		}
	}
	
	/**
	 * Updates the geometric element's default values
	 * @param element the given {@link ModelElement drawing element}
	 */
	private void updateDefaults( final ModelElement element ) {
		// auto-generate a label for the geometry
		if( !element.isLabeled() ) {
			autoLabel( element );
		}
		
		// record the label
		namingService.addLabel( element );
		
		// set the default color
		if( element.getColor() == null ) {
			element.setColor( defaultColor );
		}
		
		// set the default pattern
		if( element.getPattern() == null ||
			element.getPattern() == PATTERN_UNSPEC ) {
			element.setPattern( defaultPattern );
		}
		
		// set the current layer
		if( element.getLayer() == -1 ) {
			element.setLayer( defaultLayer );
		}
	}
	
}