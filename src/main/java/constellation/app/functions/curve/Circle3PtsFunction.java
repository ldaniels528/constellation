package constellation.app.functions.curve;

import static constellation.drawing.EntityRepresentationUtil.getPoint;
import static constellation.drawing.EntityRepresentationUtil.toDrawingElement;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/** 
 * The CIRCLE::3-PTS function
 * @author lawrence.daniels@gmail.com
 */
public class Circle3PtsFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the 1st #point of the circle",
		"Select or Indicate the 2nd #point of the circle",
		"Select or Indicate the 3rd #point of the circle"
	);
	
	/**
	 * Default constructor
	 */
	public Circle3PtsFunction() {
		super( 
			"CIRCLE", "3-PTS", 
			"images/commands/curve/circle-3pts.png", 
			"docs/functions/curve/circle_3pts.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// get the click point
		final PointXY vertex = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				
				// handle the selection
				handlePointSelection( controller, pickedPt, false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// handle the indicated selection
				handlePointSelection( controller, toDrawingElement( vertex ), true );
				break;		
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	public void elementSelected( final ApplicationController controller, final ModelElement point ) {
		handlePointSelection( controller, point, false );
	}

	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handlePointSelection( final ApplicationController controller, 
									   final ModelElement point, 
									   final boolean addToModel ) {
		if( point != null && EntityTypes.POINT.equals( point.getType() ) ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( point );
			}
			
			// select the point
			model.selectGeometry( point );
			
			// if there are 3 points
			if( model.getSelectedElementCount() >= 3 ) {
				// get the selected elements
				final RenderableElement[] elements = new RenderableElement[3];
				model.getSelectedGeometry( elements );
				
				// get the points 
				final PointXY point1 = getPoint( elements[0] );
				final PointXY point2 = getPoint( elements[1] );
				final PointXY point3 = getPoint( elements[2] );
				
				// create the circle
				final CircleXY circle = CircleXY.createCircleThru3Points( point1, point2, point3 );
	 			model.addPhysicalElement( circle );
				model.clearSelectedElements();
			}
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
}