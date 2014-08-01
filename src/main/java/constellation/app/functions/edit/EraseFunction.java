package constellation.app.functions.edit;

import static constellation.app.math.ElementDetectionUtil.lookupElementByRegion;
import static constellation.app.math.ElementDetectionUtil.lookupElementsByRegion;
import static constellation.drawing.EntityTypes.POINT;
import static constellation.drawing.entities.CompositionXY.createRectangle;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static java.lang.String.format;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxDialog;

/**
 * The EDIT::ERASE function
 * @author lawrence.daniels@gmail.com
 */
public class EraseFunction extends StructuredSelectionFunction {
	private static final Map<EraseMethods, Steps> STEP_MAPPING = createStepsToEraseMethodMapping();
	private final Map<EraseMethods, EraseMethodHandler> METHOD_MAPPING = createStepsToMethodHandlerMapping();
	private EraseDialog dialog;

	/**
	 * Default Constructor
	 */
	public EraseFunction() {
		super( 
			"EDIT", "ERASE", 
			"images/commands/edit/erase.png", 
			"docs/functions/edit/erase.html", 
			STEP_MAPPING.get( EraseMethods.PICKED ) 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#getInputDialog()
	 */
	@Override
	public CxDialog getInputDialog() {
		return dialog;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		// get the current erase method
		final EraseMethods method = dialog.getEraseMethod();
		
		// invoke the appropriate handler
		final EraseMethodHandler handler = METHOD_MAPPING.get( method );
		handler.elementSelected( controller, element );
	}
	
	/** 
	 * This method is called when the erase method has been changed.
	 * @param controller the given {@link ApplicationController controller}
	 * @param method the given {@link EraseMethods erase method}
	 */
	public void methodChanged( final ApplicationController controller, final EraseMethods method ) {
		// lookup the new steps
		final Steps newSteps = STEP_MAPPING.get( method );
		
		// set the new steps
		setSteps( controller, newSteps );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// initialize the dialog
		dialog = EraseDialog.getInstance( controller, this );
		dialog.makeVisible();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the current erase method
		final EraseMethods method = dialog.getEraseMethod();
		
		// invoke the appropriate handler
		final EraseMethodHandler handler = METHOD_MAPPING.get( method );
		handler.processMouseClick( controller, mouseClick );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
	 */
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// allow the parent function to perform highlights
		super.processMouseMovement( controller, mousePos );
		
		// get the current erase method
		final EraseMethods method = dialog.getEraseMethod();
		
		// invoke the appropriate handler
		final EraseMethodHandler handler = METHOD_MAPPING.get( method );
		handler.processMouseMovement( controller, mousePos );
	}
	
	/** 
	 * Performs the erase operation to remove the given set of geometry from the model
	 * @param controller the given {@link ApplicationController controller}
	 * @param elements the given array of {@link ModelElement elements}
	 */
	private static void performErase( final ApplicationController controller, final ModelElement ... elements ) {
		performErase( controller, Arrays.asList( elements ) );
	}
	
	/** 
	 * Performs the erase operation to remove the given set of geometry from the model
	 * @param controller the given {@link ApplicationController controller}
	 * @param elements the given collection of {@link ModelElement elements}
	 */
	private static void performErase( final ApplicationController controller, final Collection<ModelElement> elements ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// erase the geometry
		final int count = model.erase( elements );
		controller.setStatusMessage( String.format( "%d element(s) deleted", count ) );
		
		// if the geometry was deleted
		if( count > 0 ) {
			model.addPhantoms( elements );
		}
		
		// request a redraw
		controller.requestRedraw();
	}
	
	/** 
	 * Creates an erase method to function steps mapping
	 * @return the {@link EraseMethods erase method} to {@link Steps function steps} mapping
	 */
	private static Map<EraseMethods, Steps> createStepsToEraseMethodMapping() {
		final Map<EraseMethods, Steps> map = new HashMap<EraseMethods, Steps>();
		map.put( EraseMethods.PICKED, 	new Steps( "Select the #element to erase" ) );
		map.put( EraseMethods.SELECTED, new Steps( "Select an #element or Indicate to erase the selected elements" ) );
		map.put( EraseMethods.WINDOWED, new Steps( "Indicate the starting #point of the erase window",
												   "Indicate the ending #point of the erase window" ) );
		map.put( EraseMethods.POINTS, 	new Steps( "Indicate to erase all points" ) );
		return map;
	}
	
	/** 
	 * Creates an erase method to method hander mapping
	 * @return the {@link EraseMethods erase method} to {@link EraseMethodHandler method hander} mapping
	 */
	private Map<EraseMethods, EraseMethodHandler> createStepsToMethodHandlerMapping() {
		final Map<EraseMethods, EraseMethodHandler> map = new HashMap<EraseMethods, EraseMethodHandler>();
		map.put( EraseMethods.PICKED, 	new ErasePickedHandler() );
		map.put( EraseMethods.SELECTED, new EraseSelectedHandler() );
		map.put( EraseMethods.WINDOWED, new EraseWindowedHandler() );
		map.put( EraseMethods.POINTS, 	new ErasePointsHandler() );
		return map;
	}
	
	/**
	 * Represents a generic Erase Method Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private interface EraseMethodHandler {
		
		/**
		 * This call back method is invoked upon the selection
		 * of a geometric element  
		 * @param controller the given {@link ApplicationController controller}
		 * @param selectedElement the selected {@link ModelElement drawing element}
		 */
		void elementSelected( ApplicationController controller, ModelElement selectedElement );
		
		/** 
		 * Processes the given mouse click
		 * @param controller the given {@link ApplicationController controller}
		 * @param mouseClick the given {@link MouseClick mouse click}
		 */
		void processMouseClick( ApplicationController controller, MouseClick mouseClick );
		
		/** 
		 * Processes the given mouse click
		 * @param controller the given {@link ApplicationController controller}
		 * @param mousePos the given mouse {@link Point position}
		 */
		void processMouseMovement( ApplicationController controller, Point mousePos );
		
	}
	
	/**
	 * Erase Selected Method Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class EraseSelectedHandler implements EraseMethodHandler {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
		 */
		public void elementSelected( final ApplicationController controller, final ModelElement element ) {
			if( element != null ) {
				// get the model instance
				final GeometricModel model = controller.getModel();
				
				// select the element
				model.selectGeometry( element );
			}
		}

		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
		 */
		public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
			// handle the mouse click
			switch( mouseClick.getButton() ) { 
				// select geometry
				case BUTTON_SELECT:
					// select the element
					final ModelElement element = 
						lookupElementByRegion( controller, mouseClick );
					
					// was something selected?
					if( element != null ) {
						elementSelected( controller, element );
					}
					break;
			
				// erase selected geometry
				case BUTTON_INDICATE:
					// get the model instance
					final GeometricModel model = controller.getModel();
					
					// get the selected elements
					final ModelElement[] elements = new ModelElement[ model.getSelectedElementCount() ];
					model.getSelectedGeometry( elements );
					
					// clear the current selection 
					model.clearSelectedElements();
					
					// delete the set of geometry
					performErase( controller, elements );
					break;
			}
		}

		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
		 */
		public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
			// do nothing
		}
	}
	
	/**
	 * Erase Picked Method Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class ErasePickedHandler implements EraseMethodHandler {

		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
		 */
		public void elementSelected( final ApplicationController controller, final ModelElement element ) {
			if( element != null ) {
				performErase( controller, element );
			}
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
		 */
		public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
			// handle the left mouse click only
			if( mouseClick.getButton() == BUTTON_SELECT ) { 
				// pick the element
				final ModelElement element = 
					lookupElementByRegion( controller, mouseClick );
				
				// delete the set of geometry
				if( element != null ) {
					performErase( controller, Arrays.asList( element ) );
				}
			}
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
		 */
		public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
			// do nothing
		}
		
	}
	
	/**
	 * Erase Points Method Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class ErasePointsHandler implements EraseMethodHandler {

		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
		 */
		public void elementSelected( final ApplicationController controller, final ModelElement element ) {
			
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
		 */
		public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
		
			// on secondary click
			switch( mouseClick.getButton() ) { 			
				// erase all points
				case BUTTON_INDICATE:
					// get the selected elements
					final ModelElement[] elements = new ModelElement[ model.getSelectedElementCount() ];
					model.getSelectedGeometry( elements );
					
					// clear the current selection 
					model.clearSelectedElements();
					
					// delete the set of geometry.
					performErase( controller, elements );
					break;
			}
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
		 */
		public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// if the selected geometry is empty, look for some
			if( model.getSelectedElementCount() == 0 ) {
				// select all visible points
				final Collection<ModelElement> points = new LinkedList<ModelElement>();
				model.getVisibleElements( points, POINT );
				model.selectGeometry( points );
				
				// notify the user
				controller.setStatusMessage( format( "%d point(s) selected", points.size() ) );
				
				// request a redraw
				controller.requestRedraw();
			}
		}
	}
	
	/**
	 * Erase Windowed Method Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class EraseWindowedHandler implements EraseMethodHandler {
		private PointXY anchorPoint;
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
		 */
		public void elementSelected( final ApplicationController controller, final ModelElement element ) {
			
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.edit.EraseFunction.EraseMethodHandler#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
		 */
		public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
			// Capture only 'Indicate' clicks
			if( mouseClick.getButton() == BUTTON_INDICATE ) {
				// get the model and click point
				final GeometricModel model = controller.getModel();
				final PointXY vertex = controller.untransform( mouseClick );
				
				// handle the click
				handleClickPoint( controller, model, vertex );
				advanceToNextStep( controller );
			}
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.functions.AbstractFunction#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
		 */
		public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
			// get the model
			final GeometricModel model = controller.getModel();
			
			// create the planar point
			final PointXY endPoint = controller.untransform( mousePos );
			
			// manipulate temporary geometry
			if( model.getTemporaryElement() != null ) {
				// create a rectangle around the geometry
				model.setTemporaryElement( createRectangle( anchorPoint, endPoint ) );
				
	 			// cache the coordinates
	 			final double xa = anchorPoint.getX();
	 			final double ya = anchorPoint.getY();
	 			final double xb = endPoint.getX();
	 			final double yb = endPoint.getY();
	 			
	 			// determine intersecting geometry
	 			final RectangleXY boundary = new RectangleXY( xa, ya, xb - xa, yb - ya );
	 			final Collection<ModelElement> returnSet = new LinkedList<ModelElement>();	
	 			lookupElementsByRegion( controller, boundary, returnSet );
	 			model.setHighlightedGeometry( returnSet );
	 			
	 			// request a redraw
	 			controller.requestRedraw();
			}
		}
		
		/**
		 * Handles the given click point
		 * @param controller the given {@link ApplicationController controller}
		 * @param model the given {@link GeometricModel model}
		 * @param endPoint the given {@link PointXY ending point}
		 */
		private void handleClickPoint( final ApplicationController controller,
									   final GeometricModel model, 
									   final PointXY endPoint ) {
			// does the rectangle already exist?
	 		if( model.getTemporaryElement() == null ) { 
	 			// set the anchor point
	 			anchorPoint = endPoint;
	 			
	 			// create a temporary rectangle
				model.setTemporaryElement( createRectangle( endPoint, endPoint ) );
			}
	 		
	 		// otherwise ...
	 		else {
	 			// clear the temporary geometry
	 			model.clearTemporaryElement();
	 			
	 			// cache the coordinates
	 			final double xa = anchorPoint.getX();
	 			final double ya = anchorPoint.getY();
	 			final double xb = endPoint.getX();
	 			final double yb = endPoint.getY();
	 			
	 			// determine intersecting geometry
	 			final RectangleXY boundary = new RectangleXY( xa, ya, xb - xa, yb - ya );
	 			
	 			// get the set of elements within the bounds 
	 			final Collection<ModelElement> returnSet = new LinkedList<ModelElement>();	
	 			lookupElementsByRegion( controller, boundary, returnSet );
	 			
	 			// erase the geometry
	 			performErase( controller, returnSet );
	 		}
		}
		
	}

}
