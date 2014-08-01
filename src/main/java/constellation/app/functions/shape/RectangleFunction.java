package constellation.app.functions.shape;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The SHAPE::RECTANGLE function
 * @author lawrence.daniels@gmail.com
 */
public class RectangleFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the starting #point of the rectangle",
		"Select or Indicate the ending #point of the rectangle"
	);
	private PointXY anchorPt;

	/**
	 * Default constructor
	 */
	public RectangleFunction() {
		super( 
			"SHAPE", "RECTANGLE", 
			"images/commands/shape/rectangle.png", 
			"docs/functions/shape/rectangle.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.PickListObserver#elementSelected(constellation.ApplicationController, constellation.drawing.ModelElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			handleClickPoint( controller, EntityRepresentationUtil.getPoint( element ), false );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleClickPoint( controller, EntityRepresentationUtil.getPoint( pickedPt ), false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// get the model and click point
				final PointXY newPoint = controller.untransform( mouseClick );
				handleClickPoint( controller, newPoint, true );
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {		
		// allow parent to perform dynamic highlighting
		super.processMouseMovement( controller, mousePos );
		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// create the planar point
		final PointXY endPoint = controller.untransform( mousePos );
		
		// manipulate temporary geometry
		final RenderableElement temporaryGeometry = model.getTemporaryElement();
		if( temporaryGeometry != null ) {			
			// create the temporary line
			model.setTemporaryElement( CompositionXY.createRectangle( anchorPt, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link PointXY click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleClickPoint( final ApplicationController controller, 
								   final PointXY clickPoint, 
								   final boolean addToModel ) {
		if( clickPoint != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( new CxModelElement( clickPoint ) );
			}
			
			// handle the current step
			switch( steps.currentIndex() ) {
				// set the anchor point
				case Steps.STEP_1:
					anchorPt = clickPoint;
					
					// create a temporary line
					model.setTemporaryElement( CompositionXY.createRectangle( clickPoint, clickPoint ) );
					break;
					
				case Steps.STEP_2:
					// add the rectangle
					model.addPhysicalElement( new CxModelElement( CompositionXY.createRectangle( anchorPt, clickPoint ) ) );
					break;
			}
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
}