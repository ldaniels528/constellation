package constellation.model;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import constellation.drawing.EntityNamingService;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.UserImage;

/**
 * Constellation Model
 * @author lawrence.daniels@gmail.com
 */
public interface GeometricModel {
	
	/////////////////////////////////////////////////////////////////////
	//		Basic Informational Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the name of the model
	 * @return the name of the model
	 */
	String getName();
	
	/** 
	 * Returns the name of the model's author
	 * @return the name of the model's author
	 */
	String getAuthorName();
	
	/** 
	 * Sets the name of the model's author
	 * @param authorName the name of the model's author
	 */
	void setAuthorName( String authorName );
	
	/** 
	 * Returns the name of the model's author organization
	 * @return the name of the model's author organization
	 */
	String getAuthorOrganization();
	
	/** 
	 * Sets the name of the model's author organization
	 * @param authorOrganization the name of the model's author organization
	 */
	void setAuthorOrganization( String authorOrganization );
	
	/** 
	 * Returns the description of model
	 * @return the description of model
	 */
	String getDescription();

	/** 
	 * Sets the description of model
	 * @param description the given description
	 */
	void setDescription( String description );
	
	/**
	 * Indicates whether the model is virtual, non-physical or networked
	 * @return true, if the model is virtual, non-physical or networked
	 */
	boolean isVirtual();
	
	/////////////////////////////////////////////////////////////////////
	//		Layer Management Methods
	/////////////////////////////////////////////////////////////////////

	/**
	 * Adds the filter to the model
	 * @param filter the given {@link Filter table}
	 */
	void addFilter( Filter filter );
	
	/**
	 * Returns the current filter
	 * @return the current filter
	 */
	Filter getFilter();
	
	/**
	 * Set the current filter
	 * @param filter the given {@link Filter filter}
	 */
	void setFilter( Filter filter );
	
	/**
	 * Returns the collection of filters
	 * @return the collection of {@link Filter filters}
	 */
	Collection<Filter> getFilters();
	
	/////////////////////////////////////////////////////////////////////
	//		Servicing Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the entity naming service
	 * @return the {@link EntityNamingService entity naming service}
	 */
	EntityNamingService getNamingService();
	
	/////////////////////////////////////////////////////////////////////
	//		Model File Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the model file
	 * @return the model {@link File model file}
	 */
	File getModelFile();
	
	/**
	 * Sets the model file
	 * @param modelFile the model {@link File model file}
	 */
	void setModelFile( File modelFile );
		
	/////////////////////////////////////////////////////////////////////
	//		Default Settings Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the default color
	 * @return the {@link Color color} 
	 */
	Color getDefaultColor();
	
	/** 
	 * Sets the default color
	 * @param color the given {@link Color color} 
	 */
	void setDefaultColor( Color color );
	
	/**
	 * Returns the default layer
	 * @return the {@link Layer layer} 
	 */
	int getDefaultLayer();
	
	/** 
	 * Sets the default layer
	 * @param layer the given {@link Integer layer} 
	 */
	void setDefaultLayer( int layer );
	
	/**
	 * Returns the default (unspecified) pattern
	 * @return the {@link LinePatterns pattern} 
	 */
	LinePatterns getDefaultPattern();
	
	/** 
	 * Sets the default (unspecified) pattern
	 * @param pattern the given {@link LinePatterns pattern} 
	 */
	void setDefaultPattern( LinePatterns pattern );
	
	/**
	 * Returns the default line thickness
	 * @return the default line thickness
	 */
	int getDefaultThickness();

	/**
	 * Sets the default line thickness
	 * @param defaultThickness the given line thickness
	 */
	void setDefaultThickness( int defaultThickness );
	
	/** 
	 * Returns the drafting standard
	 * @return the drafting standard
	 */
	DraftingStandards getDraftingStandard();
	
	/** 
	 * Sets the drafting standard
	 * @param draftingStandard the drafting standard
	 */
	void setDraftingStandard( DraftingStandards draftingStandard );
	
	/** 
	 * Returns the scale of the model
	 * @return the scale of the model
	 */
	double getScale();
	
	/** 
	 * Sets the scale of the model
	 * @param scale the scale of the model
	 */
	void setScale( double scale );
	
	/**
	 * Returns the currently defined units
	 * @return the {@link Unit unit}
	 */
	Unit getUnit();
	
	/** 
	 * Sets the units for the model
	 * @param units the given {@link Unit unit}
	 */
	void setUnit( Unit units );
	
	
	/////////////////////////////////////////////////////////////////////
	//		User Image Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds a new user image to the model
	 * @param image the given {@link UserImage image}
	 */
	void addUserImage( UserImage image );
	
	/**
	 * Retrieves all currently associated user images
	 * @return a {@link Set set} of user {@link UserImage images}
	 */
	Set<UserImage> getUserImages();
	
	/**
	 * Lookups a user image by name
	 * @param imageName the name of the desired image
	 * @return a user {@link UserImage image}
	 */
	UserImage lookupUserImage( String imageName );
	
	/**
	 * Removes a user image by name
	 * @param imageName the name of the desired image
	 */
	void removeUserImage( String imageName );
	
	
	/////////////////////////////////////////////////////////////////////
	//		Model Element C.R.U.D. Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * Adds the given internal representation(s) to the model
	 * @param representations one of more {@link EntityRepresentation internal representations}
	 * @return the wrapped {@link ModelElement elements}
	 */
	List<ModelElement> addPhysicalElement( EntityRepresentation... representations );
	
	/** 
	 * Adds the given physical element(s) to the model
	 * @param elements one of more {@link ModelElement drawing elements}
	 */
	void addPhysicalElement( ModelElement... elements );
	
	/** 
	 * Adds the given set of physical elements to the model
	 * @param elements a collection of {@link ModelElement drawing elements}
	 */
	void addPhysicalElements( Collection<? extends ModelElement> elements );
	
	/**
	 * Returns the complete set of geometry that exists in the model
	 * @return a collection of {@link ModelElement geometric elements}
	 */
	Collection<ModelElement> getPhysicalElements();
	
	/**
	 * Populates the given container with the set of elements 
	 * on the current layer.
	 * @param container the given container to use while 
	 * populating matching elements.
	 */
	void getVisibleElements( Collection<ModelElement> container );
	
	/**
	 * Populates the given container with the set of elements 
	 * on the current layer.
	 * @param container the given container to use while 
	 * populating matching elements.
	 * @param types the given array of {@link EntityTypes element types}
	 */
	void getVisibleElements( Collection<ModelElement> container, EntityTypes ... types );
	
	/**
	 * Removes the given drawing elements from the model
	 * @param elements a collection of {@link ModelElement drawing elements}
	 * @return the number of elements removed
	 */
	int erase( Collection<ModelElement> elements );

	/**
	 * Removes the given drawing elements from the model
	 * @param elements the given {@link ModelElement drawing elements}
	 * @return true, if the element was successfully removed
	 */
	int erase( ModelElement... elements );
	
	/**
	 * Returns the epoch time since the last change of the given type
	 * @param type the given {@link ModelChangeType model change type}
	 * @return the epoch time
	 */
	long getLastChangeTime( ModelChangeType type );
	
	/////////////////////////////////////////////////////////////////////
	//		Element Detection Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Retrieve a geometric element by label
	 * @param label the given label
	 * @return the {@link ModelElement elements}
	 */
	ModelElement lookupElementByLabel( String label );
	
	/////////////////////////////////////////////////////////////////////
	//		Drawing Element Picking Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Clears the current picked geometry
	 */
	void clearPickedElement();
	
	/**
	 * Returns the picked geometry
	 * @return the {@link ModelElement element}
	 */
	ModelElement getPickedElement();
	
	/**
	 * Sets the picked geometry
	 * @param element the {@link ModelElement drawing element}
	 */
	void setPickedElement( ModelElement element );
	
	
	/////////////////////////////////////////////////////////////////////
	//		Element Selection Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Clears the current selection
	 */
	void clearSelectedElements();
	
	/**
	 * Returns the current count of selected elements
	 * @return the count of selected elements
	 */
	int getSelectedElementCount();
	
	/**
	 * Populates the given collection with current selected geometry
	 * @param elements the given collection for which to store the results.
	 */
	void getSelectedGeometry( Collection<RenderableElement> elements );
	
	/**
	 * Populates the given array with current selected geometry
	 * @param elements the given array for which to store the results.
	 */
	void getSelectedGeometry( RenderableElement[] elements );
	
	/**
	 * Adds the given element to the current selection
	 * @param element the current selected {@link RenderableElement element}
	 */
	void selectGeometry( RenderableElement element );
	
	/**
	 * Adds the given geometry to the current selection
	 * @param elements the collection of geometry to select
	 */
	void selectGeometry( Collection<? extends RenderableElement> elements );
	
	/**
	 * Selects all currently existing geometry
	 */
	void selectAll();
	
	/////////////////////////////////////////////////////////////////////
	//		Highlighted Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Clears the current highlighted geometry selection
	 */
	void clearHighlightedGeometry();
	
	/** 
	 * Returns the set of highlighted geometry
	 * @return the set of highlighted geometry
	 */
	Collection<ModelElement> getHighlightedGeometry();
	
	/** 
	 * Sets the highlighted geometry
	 * @param elements the set of highlighted geometry
	 */
	void setHighlightedGeometry( Collection<ModelElement> elements );
	
	/////////////////////////////////////////////////////////////////////
	//		Temporary Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the current temporary element.
	 * @return the current {@link RenderableElement temporary element}
	 */
	RenderableElement getTemporaryElement();
	
	/**
	 * Sets the current temporary element.
	 * @param element the current {@link RenderableElement temporary element}
	 */
	void setTemporaryElement( RenderableElement element );
	
	/**
	 * Discards the current temporary drawing element.
	 */
	void clearTemporaryElement();
	
	
	/////////////////////////////////////////////////////////////////////
	//		Phantom Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds the given array of geometry to the model as phantoms
	 * @param elements one of more {@link ModelElement elements} 
	 */
	void addPhantoms( final ModelElement ... elements );
	
	/**
	 * Adds the given array of geometry to the model as phantoms
	 * @param elements the given collection of {@link ModelElement elements} 
	 */
	void addPhantoms( final Collection<? extends ModelElement> elements );
	
	/**
	 * Removes the given phantom drawing elements from the model
	 * @param elements the given array of {@link ModelElement drawing elements}
	 * @return the number of phantoms remove
	 */
	int erasePhantom( ModelElement... elements );
	
	/**
	 * Removes the given phantom drawing elements from the model
	 * @param elements the given collection of {@link ModelElement drawing elements}
	 * @return the number of phantoms remove
	 */
	int erasePhantoms( Collection<ModelElement> elements );
	
	/** 
	 * Returns the 'phantom geometry'; geometry that has been deleted from the model.
	 * @param container the collection for which to store the phantom elements
	 */
	void getPhantomElements( final Collection<ModelElement> container );
	
	/** 
	 * Indicates whether phantom geometry is enabled
	 * @return true, if phantom geometry is enabled
	 */
	boolean isPhantomsEnabled();
	
	/** 
	 * Turns the phantom geometry on or off
	 * @param enabled indicates whether the phantom geometry mode is on or off
	 */
	void setPhantomMode( boolean enabled );
	
	/////////////////////////////////////////////////////////////////////
	//		System Element Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the background image
	 * @return the {@link Image image}
	 */
	Image getBackgroundImage();
	
	/** 
	 * Sets the background image of the drawing panel
	 * @param image the given {@link Image background image}
	 */
	void setBackgroundImage( Image image );
	
}
