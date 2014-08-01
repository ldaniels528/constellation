package constellation.app.functions.point;

import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * Represents a generic Point Function
 * @author lawrence.daniels@gmail.com
 */
class AbstractPointFunction extends StructuredSelectionFunction {
	
	/** 
	 * Creates a generic point function
	 * @param functionName the given function name
	 * @param iconPath the given icon path
	 * @param helpPath the given help URI
	 * @param steps the given {@link Steps functional steps}
	 */
	protected AbstractPointFunction( final String functionName,
									 final String iconPath, 
									 final String helpPath, 
									 final Steps steps) {
		super( "POINT", functionName, iconPath, helpPath, steps );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// initialize the editor dialog
		dialog.resetIdentity( EntityCategoryTypes.VERTEX );
	}
	
	/**
	 * Creates a new point, and adds it to the model
	 * @param controller the given {@link ApplicationController controller}
	 * @param vertex the given {@link PointXY vertex}
	 */
	protected void addPointToModel( final ApplicationController controller,
								  	final PointXY vertex ) {
		// create an element to wrap the vertex
		final ModelElement element = new CxModelElement( vertex );
		
		// export the settings to the element
		dialog.exportSettings( element );
		
		// create a new point
		final GeometricModel model = controller.getModel();
		model.addPhysicalElement( element );
		
		// set the status message
		controller.setStatusMessage( format( "Created point '%s' at %s", element.getLabel(), vertex ) );
		
		// restart the function
		onStart( controller );
	}
	
	/**
	 * Creates new points, and adds them to the model
	 * @param controller the given {@link ApplicationController controller}
	 * @param vertex the given {@link PointXY vertex}
	 */
	protected void addPointsToModel( final ApplicationController controller,
								  	 final PointXY ... vertex ) {
		// create an element to wrap the vertex
		final ModelElement[] elements = new ModelElement[ vertex.length ];
		for( int n = 0; n < elements.length; n++ ) {
			// create a new element
			final ModelElement element = new CxModelElement( vertex[n] );
			
			// export the settings to the element
			dialog.exportAttributes( element );
			
			// record the element
			elements[n] = element;
		}
		
		// add the elements to the model
		final GeometricModel model = controller.getModel();
		model.addPhysicalElement( elements );
		
		// set the status message
		controller.setStatusMessage( format( "Created %d points", elements.length ) );
		
		// restart the function
		onStart( controller );
	}

}
