package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;
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
import constellation.math.CxIntersectionUtil;
import constellation.model.GeometricModel;

/**
 * The LINE::RELIMIT function
 * @author lawrence.daniels@gmail.com
 */
public class LineRelimitFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select the #line to relimit",
		"Select or Indicate a #point or #line"
	);
	private ModelElement hostLine;
	
	/**
	 * Default constructor
	 */
	public LineRelimitFunction() {
		super( 
			"LINE", "RELIMIT", 
			"images/commands/line/line-relimit.gif", 
			"docs/functions/line/relimit.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.AbstractFunction#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1_LineSelection( controller, element ); break;
			case STEP_2: handleStep2_PointOrLineSelection( controller, element ); break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// reset the function
		hostLine = null;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the current step
		switch( steps.currentIndex() ) {
			case Steps.STEP_1: handleStep1( controller, mouseClick ); break;
			case Steps.STEP_2: handleStep2( controller, mouseClick ); break;
		}
	}
	
	/**
	 * Handles step #1: Select the Line to re-limit
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// determine the line to select
			final ModelElement line = 
				ElementDetectionUtil.lookupLineByRegion( controller, mouseClick );
			
			// handle the selection
			handleStep1_LineSelection( controller, line );
		}
	}
	
	/** 
	 * Handles the selection of the host line
	 * @param controller the given {@link ApplicationController controller}
	 * @param line the host {@link ModelElement line}
	 */
	private void handleStep1_LineSelection( final ApplicationController controller, final ModelElement line ) {
		if( line != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the line
			model.selectGeometry( line );
			
			// remember this line
			this.hostLine = line;
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Handles step #1: Select/Indicate a Point or Select a Line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the model and click point
		final GeometricModel model = controller.getModel();
		final PointXY clickPt = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point or line
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement element = 
					ElementDetectionUtil.lookupElementByRegion( controller, mouseClick );
				
				// handle the selection
				handleStep2_PointOrLineSelection( controller, element );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				model.addPhysicalElement( new CxModelElement( clickPt ) );
				relimit( model, hostLine, clickPt );
				advanceToNextStep( controller );
				break;
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleStep2_PointOrLineSelection( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// determine the re-limit plane
			switch( element.getType() ) {
				// was it a point?
				case POINT:
					relimit( model, hostLine, EntityRepresentationUtil.getPoint( element ) );
					advanceToNextStep( controller );
					break;
					
				// was it a line?
				case LINE:
					relimit( controller, model, hostLine, element );
					advanceToNextStep( controller );
					break;
					
				default:
					controller.setStatusMessage( format( "%s '%s' is not compatible; select a line or point", 
													element.getType(), element.getLabel() ) );
			}
		}
	}
	
	/**
	 * Extends or trims the given line so that it intersects
	 * the given point.
	 * @param model the given {@link GeometricModel model}
	 * @param line the given {@link ModelElement line}
	 * @param point the given {@link PointXY point}
	 */
	private void relimit( final GeometricModel model, 
						  final ModelElement line, 
						  final PointXY point ) {	
		// get the line's substance
		final LineXY lineXY = EntityRepresentationUtil.getLine( line );
		
		// create the new trimmed or extended line
		line.setRepresentation( lineXY.trimOrExtendTo( point ) );
	}
	
	/**
	 * Extends or trims the given lines until the lines intersect.
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel model}
	 * @param lineA the given {@link ModelElement Line A}
	 * @param lineB the given {@link ModelElement Line B}
	 */
	private void relimit( final ApplicationController controller, 
						  final GeometricModel model, 
						  final ModelElement lineA, 
						  final ModelElement lineB ) {	
		// get the representation of the lines
		final LineXY line1	= EntityRepresentationUtil.getLine( lineA );
		final LineXY line2	= EntityRepresentationUtil.getLine( lineB );
		
		// get the intersection point
		final PointXY intersectionPt = CxIntersectionUtil.getIntersectionPoint( line1, line2 );
		if( intersectionPt == null ) {
			controller.showErrorDialog( format( "No intersection found between lines '%s' and '%s'", lineA, lineB ), "Intersection Error" );
			return;
		}
		
		// trim or extended the line representation for Line A
		lineA.setRepresentation( line1.trimOrExtendTo( intersectionPt ) );
		
		// trim or extended the line representation for Line B
		lineB.setRepresentation( line2.trimOrExtendTo( intersectionPt ) );
	}
	
}