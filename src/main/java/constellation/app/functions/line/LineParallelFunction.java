package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static constellation.functions.Steps.STEP_3;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LINE::PARALLEL function
 * @author lawrence.daniels@gmail.com
 */
public class LineParallelFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps( 
		"Select the host line",
		"Select or Indicate the starting #point of the line",
		"Select or Indicate the ending #point of the line"
	);
	private LineXY hostLine;
	private PointXY startPt;
	
	/**
	 * Default constructor
	 */
	public LineParallelFunction() {
		super( 
			"LINE", "PARALLEL", 
			"images/commands/line/line-parallel.gif", 
			"docs/functions/line/parallel.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1_LineSelection( controller, element ); break;
			case STEP_2: handleStep2_PointSelection( controller, element, false ); break;
			case STEP_3: handleStep3_PointSelection( controller, element, false ); break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		switch( steps.currentIndex() ) {
			case Steps.STEP_1: handleStep1( controller, mouseClick ); break;
			case Steps.STEP_2: handleStep2( controller, mouseClick ); break;
			case Steps.STEP_3: handleStep3( controller, mouseClick ); break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// allow parent to perform highlighting
		super.processMouseMovement( controller, mousePos );
		
		// if STEP #3, display the dynamic parallel line
		if( steps.currentIndex() == Steps.STEP_3 ) {
			// get the end point
			final PointXY endPt = controller.untransform( mousePos );
			
			// set the parallel line
			final GeometricModel model = controller.getModel();
			model.setTemporaryElement( createParallelLine( hostLine, startPt, endPt ) );
			
			// request a redraw
			controller.requestRedraw();
		}	
	}

	/**
	 * Handles step #1: Select a Line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// get the selected line
			final ModelElement line = ElementDetectionUtil.lookupLineByRegion( controller, mouseClick );
			if( line != null ) {
				handleStep1_LineSelection( controller, line );
			}
		}
	}
	
	/**
	 * Handles the selection of a line
	 * @param controller the given {@link ApplicationController controller}
	 * @param line the given {@link ModelElement line}
	 */
	private void handleStep1_LineSelection( final ApplicationController controller, final ModelElement line ) {
		if( line != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the line
			model.selectGeometry( line );
			
			// capture the line
			hostLine = EntityRepresentationUtil.getLine( line );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}

	/**
	 * Handles step #2: Select or Indicate the starting point of the line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the model and click point
		final PointXY vertex = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// Select a point
			case BUTTON_SELECT:
				// lookup the selected point
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep2_PointSelection( controller, pickedPt, false );
				break;
				
			// Indicate a point
			case BUTTON_INDICATE:
				handleStep2_PointSelection( controller, new CxModelElement( vertex ), true );
				break;
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleStep2_PointSelection( final ApplicationController controller, 
											 final ModelElement point, 
											 final boolean addToModel ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( point );
			}
			
			// select the clicked point
			model.selectGeometry( point );
			
			// capture the start point
			startPt = EntityRepresentationUtil.getPoint( point );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}

	/**
	 * Handles step #3: Select or Indicate the ending point of the line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep3( final ApplicationController controller, final MouseClick mouseClick ) {
		// get clicked point
		final PointXY vertex = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// Select a point
			case BUTTON_SELECT:
				// lookup the selected point
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep3_PointSelection( controller, pickedPt, false );
				break;
				
			// Indicate a point
			case BUTTON_INDICATE:
				handleStep3_PointSelection( controller, new CxModelElement( vertex ), true );
				break;
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleStep3_PointSelection( final ApplicationController controller, 
											 final ModelElement point, 
											 final boolean addToModel ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( point );
			}
			
			// create the parallel line
			final LineXY parallelLine = createParallelLine( hostLine, startPt, EntityRepresentationUtil.getPoint( point ) );
			model.addPhysicalElement( new CxModelElement( parallelLine ) );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Creates the parallel line based on the 3 selected elements
	 * @param hostLine the given {@link LineXY host line}
	 * @param startPt the given {@link PointXY start point}
	 * @param endPt the given {@link PointXY end point}
	 * @return the {@link LineXY line}
	 */
	private LineXY createParallelLine( final LineXY hostLine, final PointXY startPt, final PointXY endPt ) {
		return LineXY.createParallelLine( hostLine, startPt, endPt );
	}
	
}