package constellation.app.functions.view;

import static constellation.drawing.entities.CompositionXY.createRectangle;
import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The VIEW::ZOOM IN function
 * @author lawrence.daniels@gmail.com
 */
public class ZoomInFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Indicate the starting point of the window",
		"Indicate the ending point of the window"
	);
	private PointXY anchorPoint;
	
	/**
	 * Default constructor
	 */
	public ZoomInFunction() {
		super( "VIEW", "ZOOM IN", "images/zoom/zoom-window.png", null, STEPS );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#process(constellation.FunctionController, constellation.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// Capture only 'Indicate' clicks
		if( mouseClick.getButton() == BUTTON_INDICATE ) {
			// get the model and click point
			final GeometricModel model = controller.getModel();
			final PointXY point = controller.untransform( mouseClick );
			
			// handle the click
			handleClickPoint( controller, model, point );
			advanceToNextStep( controller );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#processMouseMovement(constellation.commands.FunctionController, int, int)
	 */
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// allow the parent function to perform highlights
		super.processMouseMovement( controller, mousePos );
		
		// get the model
		final GeometricModel model = controller.getModel();
		
		// create the planar point
		final PointXY endPoint = controller.untransform( mousePos );
		
		// manipulate temporary geometry
		if( model.getTemporaryElement() != null ) {
			// create the rectangular boundary
			model.setTemporaryElement( createRectangle( anchorPoint, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
		
		// request geometry update
		controller.requestRedraw();
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel model}
	 * @param clickPoint the given {@link PointXY click point}
	 */
	private void handleClickPoint( final ApplicationController controller,
								   final GeometricModel model, 
								   final PointXY clickPoint ) {
		// does the rectangle already exist?
 		if( model.getTemporaryElement() == null ) { 
 			// set the anchor point
 			anchorPoint = clickPoint;
 			
 			// create a temporary rectangle
			model.setTemporaryElement( createRectangle( clickPoint, clickPoint ) );
		}
 		
 		// otherwise ...
 		else {
 			// clear the temporary geometry
 			model.clearTemporaryElement();
 			
 			// get the area extents
 			final double minX 	= anchorPoint.getX();
 			final double minY 	= anchorPoint.getY();
 			final double maxX 	= clickPoint.getX();
 			final double maxY 	= clickPoint.getY();
 		 	
 			// perform the zoom
 			controller.zoomToFit( minX, minY, maxX, maxY );
 		}
	}
	
}
