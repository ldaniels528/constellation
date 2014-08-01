package constellation.app.functions.curve;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/** 
 * The ELLIPSE::3-PTS function
 * @author lawrence.daniels@gmail.com
 */
public class Ellipse3PtsFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the 1st #point of the ellipse",
		"Select or Indicate the 2nd #point of the ellipse",
		"Select or Indicate the 3rd #point of the ellipse"
	);
	
	/**
	 * Default constructor
	 */
	public Ellipse3PtsFunction() {
		super( 
			"ELLIPSE", "3-PTS", 
			"images/commands/curve/ellipse-3pts.png", 
			"docs/functions/curve/ellipse_3pts.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handlePointSelection( controller, element );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the click point
		final PointXY vertex = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handlePointSelection( controller, pickedPt ); 
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				final ModelElement point = new CxModelElement( vertex );
				
				// create the point
				model.addPhysicalElement( point );
				
				// handle the click
				handleClickPoint( model, point );
				advanceToNextStep( controller );
				break;		
		}
	}
	
	/** 
	 * Handle the click point
	 * @param model the given {@link GeometricModel model}
	 * @param pickedPt the given {@link ModelElement click point}
	 */
	private void handleClickPoint( final GeometricModel model, final ModelElement pickedPt ) {
		// add the point
		model.selectGeometry( pickedPt );
		
		// if there are 3 points
		if( model.getSelectedElementCount() >= 3 ) {
			// get the selected elements
			final RenderableElement[] elements = new RenderableElement[2];
			model.getSelectedGeometry( elements );
			
			// get the points 
			final PointXY p1 = EntityRepresentationUtil.getPoint( elements[0] );
			final PointXY p2 = EntityRepresentationUtil.getPoint( elements[1] );
			final PointXY p3 = EntityRepresentationUtil.getPoint( elements[2] );
			
			// create the circle
 			model.addPhysicalElement( new CxModelElement( EllipseXY.createEllipseThru3Points( p1, p2, p3 ) ) );
			model.clearSelectedElements();
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 */
	private void handlePointSelection( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// handle the clicked point
			handleClickPoint( model, point );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
}