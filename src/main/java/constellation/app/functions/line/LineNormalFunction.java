package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static constellation.functions.Steps.STEP_3;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.functions.edit.EntityEditorDialog;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LINE::NORMAL function
 * @author lawrence.daniels@gmail.com
 */
public class LineNormalFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select the host #line",
		"Select or Indicate the starting #point of the normal line",
		"Select or Indicate the ending #point of the normal line"
	);
	
	// internal fields
	private EntityEditorDialog dialog;
	private LineXY hostLine;
	private PointXY startPoint;
	
	/**
	 * Default constructor
	 */
	public LineNormalFunction() {
		super( 
			"LINE", "NORMAL", 
			"images/commands/line/line-normal.gif", 
			"docs/functions/line/normal.html", 
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
			case STEP_2: handleStep2_PointSelection( controller, element ); break;
			case STEP_3: handleStep3_PointSelection( controller, element ); break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.StructuredSelectionFunction#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event
		super.onStart( controller );
		
		// setup the dialog
		dialog = EntityEditorDialog.getInstance( controller );
		dialog.setCallingFunction( this, EntityTypes.LINE );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the current step
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1( controller, mouseClick ); break;
			case STEP_2: handleStep2( controller, mouseClick ); break;
			case STEP_3: handleStep3( controller, mouseClick ); break;
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
		
		// if we're on Step #2 ...
		if( steps.currentIndex() == STEP_3 ) {
			// get the model
			final GeometricModel model = controller.getModel();
			
			// create the planar point
			final PointXY endPoint = controller.untransform( mousePos );
			
			// create the temporary line
			model.setTemporaryElement( computeNormalLine( hostLine, startPoint, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
	}
	
	/**
	 * Handles step #1: Select the host line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, 
							  final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// get the line in the boundary
			final ModelElement line = 
				ElementDetectionUtil.lookupLineByRegion( controller, mouseClick );
			
			// if a line was click ...
			handleStep1_LineSelection( controller, line );
		}
	}
	
	/**
	 * Handles the selection of an element
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
	 * Handles step #2: Select or Indicate the point for the normal line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the mouse click
		final ModelElement point = 
			ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
		
		// if a point was selected, continue to the next step
		handleStep2_PointSelection( controller, point );
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 */
	private void handleStep2_PointSelection( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {	
			// get the model instance 
			final GeometricModel model = controller.getModel();
			
			// add the point
			model.addPhysicalElement( point );
			
			// select the point
			model.selectGeometry( point );
			
			// capture the point
			startPoint = EntityRepresentationUtil.getPoint( point );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Handles step #3: Select or Indicate the starting point of the normal line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep3( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selecting a point
			case BUTTON_SELECT:
				// use the click boundary to determine the selected point
				final ModelElement selPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep3_PointSelection( controller, selPt );
				break;
				
			// indicating a point
			case BUTTON_INDICATE:
				// create the indicated point
				final PointXY vertex = controller.untransform( mouseClick );
				handleStep3_PointSelection( controller, new CxModelElement( vertex ) );
				break;
		}	
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 */
	private void handleStep3_PointSelection( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the clicked point to the model
			model.addPhysicalElement( point );
			
			// select the clicked point
			model.selectGeometry( point );
			
			// create the normal line
			createNormalLine( controller, model, point );
		}
	}
	
	/**
	 * Creates the normal line
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param point the given {@link ModelElement point}
	 */
	private void createNormalLine( final ApplicationController controller, 
								   final GeometricModel model, 
								   final ModelElement point ) {
		// get the selected elements
		final RenderableElement[] elements = new RenderableElement[3];
		model.getSelectedGeometry( elements );
		
		// compute the normal line
		final LineXY normalLine = computeNormalLine( elements );
		model.addPhysicalElement( new CxModelElement( normalLine ) );
		
		// continue to the next step
		advanceToNextStep( controller );
	}
	
	/**
	 * Creates a new normal line
	 * @param elements the given array of {@link RenderableElement selected elements}
	 * @return a new normal {@link LineXY line}
	 */
	private LineXY computeNormalLine( final RenderableElement[] elements ) {
		// get the arguments
		final LineXY line 		= EntityRepresentationUtil.getLine( elements[0] );
		final PointXY startPt 	= EntityRepresentationUtil.getPoint( elements[1] );
		final PointXY endPt 	= EntityRepresentationUtil.getPoint( elements[2] );
		
		// create the perpendicular line
		return computeNormalLine( line, startPt, endPt );
	}
	
	/**
	 * Creates a new normal line
	 * @param line the given {@link LineXY host line}
	 * @param startPt the given {@link PointXY start point}
	 * @param endPt the given {@link PointXY end point}
	 * @return a new normal {@link LineXY line}
	 */
	private LineXY computeNormalLine( final LineXY line, final PointXY startPt, final PointXY endPt ) {
		// create the perpendicular line
		final LineXY normalLine = LineXY.createNormalLine( line, startPt, endPt );
		return dialog.isInfinite() ? LineXY.createInfiniteLine( normalLine ) : normalLine;
	}
	
}