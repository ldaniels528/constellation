package constellation.app.functions.line;

import static constellation.app.math.ElementDetectionUtil.lookupCurveByRegion;
import static constellation.drawing.EntityRepresentationUtil.getTypeName;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static constellation.functions.Steps.STEP_3;
import static constellation.functions.Steps.STEP_4;
import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CurveXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LINE::TANGENT function
 * @see http://gpwiki.org/index.php/Tangents_To_Circles_And_Ellipses
 * @author lawrence.daniels@gmail.com
 */
public class LineTangentFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select the host #curve",
		"Select or Indicate a #point on the curve",
		"Select or Indicate the starting #point of the normal line",
		"Select or Indicate the ending #point of the normal line"
	);

	/**
	 * Default constructor
	 */
	public LineTangentFunction() {
		super( 
			"LINE", "TANGENT", 
			"images/commands/line/line-tangent.png", 
			"docs/functions/line/tangent.html", 
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
			case STEP_1: handleStep1_SelectCurve( controller, element ); break;
			case STEP_2: handleStep2or3_SelectPoint( controller, element ); break;
			case STEP_3: handleStep2or3_SelectPoint( controller, element ); break;
			case STEP_4: handleStep4_SelectPoint( controller, element ); break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#process(constellation.commands.FunctionController, constellation.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the model instance 
		final GeometricModel model = controller.getModel();
		
		// handle the current step
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1( controller, model, mouseClick ); break;
			case STEP_2: handleStep2or3( controller, mouseClick ); break;
			case STEP_3: handleStep2or3( controller, mouseClick ); break;
			case STEP_4: handleStep4( controller, mouseClick ); break;
		}
	}
	
	/**
	 * Handles step #1: Select the host curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, 
							  final GeometricModel model, 
							  final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// determine the intersecting curve elements
			final ModelElement curve = lookupCurveByRegion( controller, mouseClick );
			handleStep1_SelectCurve( controller, curve );
		}
	}
	
	/**
	 * Selects the given curve (if it is circular)
	 * @param controller the given {@link ApplicationController controller}
	 * @param curve the given {@link ModelElement curve}
	 */
	private void handleStep1_SelectCurve( final ApplicationController controller, final ModelElement curve ) {
		if( curve != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the curve
			model.selectGeometry( curve );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}

	/**
	 * Handles steps #2 or #3: Select or Indicate a point on the curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2or3( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selecting a point
			case BUTTON_SELECT:
				// use the click boundary to determine the selected point
				final ModelElement point = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep2or3_SelectPoint( controller, point );
				break;
				
			// indicating a point
			case BUTTON_INDICATE:
				// return the indicated point
				final PointXY vertex = controller.untransform( mouseClick );
				handleStep2or3_SelectPoint( controller, new CxModelElement( vertex ) );
				break;
		}
	}
	
	/**
	 * Handles steps #2 or #3: Select or Indicate a point on the curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 */
	private void handleStep2or3_SelectPoint( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model
			model.addPhysicalElement( point );
			
			// select the point
			model.selectGeometry( point );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Handles step #4: Select or Indicate the starting point of the normal line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep4( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selecting a point
			case BUTTON_SELECT:
				// use the click boundary to determine the selected point
				final ModelElement point = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep4_SelectPoint( controller, point );
				break;
				
			// indicating a point
			case BUTTON_INDICATE:
				// return the indicated point
				final PointXY vertex = controller.untransform( mouseClick );
				handleStep4_SelectPoint( controller, new CxModelElement( vertex ) );
				break;
		}
	}
	
	private void handleStep4_SelectPoint( final ApplicationController controller, final ModelElement point ) {
		// if a point was selected, create the normal line
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model
			model.addPhysicalElement( point );
			
			// select the point
			model.selectGeometry( point );
				
			// compute the normal line
			final LineXY tangentLine = computeTangentLine( controller );
			if( tangentLine != null ) {
				model.addPhysicalElement( tangentLine );
				
				// continue to the next step
				advanceToNextStep( controller );
			}
			else {
				controller.setStatusMessage( "No tangent line was found." );
			}
		}
	}
	
	/**
	 * Creates a new tangent line
	 * @param controller the given {@link ApplicationController controller}
	 * @return a new tangent {@link ModelElement line} or <tt>null</tt> if not found
	 */
	private LineXY computeTangentLine( final ApplicationController controller ) {
		try {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// get the 4 selected elements
			final ModelElement[] selectedElements = new ModelElement[4];
			model.getSelectedGeometry( selectedElements );
			
			// get the selected elements
			final CurveXY curve 	= EntityRepresentationUtil.getCurve( selectedElements[0] );
			final PointXY tangentPt = EntityRepresentationUtil.getPoint( selectedElements[1] );
			final PointXY startPt 	= EntityRepresentationUtil.getPoint( selectedElements[2] );
			final PointXY endPt 	= EntityRepresentationUtil.getPoint( selectedElements[3] );
			
			// if the curve is a supported curve ...
			switch( curve.getType() ) {
				case ARC:		
					return LineXY.createTangentLine( (ArcXY)curve, tangentPt, startPt, endPt );
				
				case CIRCLE:		
					return LineXY.createTangentLine( (CircleXY)curve, tangentPt, startPt, endPt );
					
				case ELLIPSE:
					return LineXY.createTangentLine( (EllipseXY)curve, tangentPt, startPt, endPt );
					
				// all other types are invalid
				default:
					controller.setStatusMessage( format( "Curve type '%s' is not supported by this function", 
							getTypeName( curve.getType() ) ) );
					return null;
			}
		}
		catch( final Exception e ) {
			logger.error( e );
			return null;
		}
	}
	
}