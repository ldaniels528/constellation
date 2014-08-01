package constellation.app.functions.line;

import static constellation.app.math.ElementDetectionUtil.lookupGeometricElementByRegion;
import static constellation.drawing.elements.CxModelElement.copyProperties;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.math.CxIntersectionUtil;
import constellation.math.CxIntersectionUtil.Intersection;
import constellation.model.GeometricModel;

/**
 * The LINE::SPLIT function
 * @author lawrence.daniels@gmail.com
 */
public class LineSplitFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select the #line to split",
		"Select the split element (#point or #line or #curve)"
	);
	private ModelElement hostLine;

	/**
	 * Default constructor
	 */
	public LineSplitFunction() {
		super( 
			"LINE", "SPLIT", 
			"images/commands/line/line-split.png", 
			"docs/functions/line/split.html", 
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
			case STEP_2: handleStep2_LineSplit( controller, hostLine, element );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
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
	 * Handles step #1: Select the Line to split
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// get the selected line
			final ModelElement line = ElementDetectionUtil.lookupLineByRegion( controller, mouseClick );
			handleStep1_LineSelection( controller, line );
		}
	}
	
	/**
	 * Handles the selection of a line
	 * @param controller the given {@link ApplicationController controller}
	 * @param line the given {@link ModelElement line}
	 */
	private void handleStep1_LineSelection( final ApplicationController controller, final ModelElement line ) {
		if( line != null ) {
			// get the geometric model instance
			final GeometricModel model = controller.getModel();
			
			// capture the host line
			this.hostLine = line;
			
			// select the line
			model.selectGeometry( line );
			advanceToNextStep( controller );
		}
	}

	/**
	 * Handles step #2: Select/Indicate a Point or Select a Line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the geometric model 
		final GeometricModel model = controller.getModel();
		
		// get the click point
		final PointXY clickPt = controller.untransform( mouseClick );
		
		// get the selected elements
		final ModelElement[] elements = new ModelElement[1];
		model.getSelectedGeometry( elements );
		
		// get the host line
		final ModelElement hostLine = elements[0];
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point or line
			case BUTTON_SELECT:
				// get the selected elements
				final ModelElement element = 
					lookupGeometricElementByRegion( controller, mouseClick );
				
				// handle the element
	 			handleStep2_LineSplit( controller, hostLine, element );  			
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				model.addPhysicalElement( new CxModelElement( clickPt ) );
				splitUsingPoint( model, hostLine, clickPt );
				advanceToNextStep( controller );
				break;
		}
	}
	
	/** 
	 * Handles the splitting of the line
	 * @param controller the given {@link ApplicationController controller}
	 * @param hostLine the given {@link ModelElement line to split}
	 * @param splitElement the given {@link ModelElement splitting element}
	 */
	private void handleStep2_LineSplit( final ApplicationController controller, 
								  		final ModelElement hostLine,
								  		final ModelElement splitElement ) {
		if( splitElement != null ) {
			// get the geometric model instance
			final GeometricModel model = controller.getModel();
			
			// examine the geometry
			switch( splitElement.getType() ) {
				// was it a point?
				case POINT:
					final ModelElement splitPoint = splitElement;
					splitUsingPoint( model, hostLine, EntityRepresentationUtil.getPoint( splitPoint ) );
					advanceToNextStep( controller );
					break;
					
				// was it a line?
				case LINE:
					final ModelElement splitLine = splitElement;
					splitUsingLine( controller, hostLine, EntityRepresentationUtil.getLine( splitLine ) );
					advanceToNextStep( controller );
					break;
					
				// was it a line?
				case CIRCLE:
					final ModelElement splitCurve = splitElement;
					splitUsingCurve( controller, hostLine, EntityRepresentationUtil.getCircle( splitCurve ) );
					advanceToNextStep( controller );
					break;
					
				// all other curves ...
				case ARC:;
				case ELLIPSE:;
				case ELLIPTIC_ARC:;
				case SPIRAL:;
				case SPLINE:;
				case USER_DEFINED:
					// curve must be a circle
					controller.setStatusMessage( "Incompatible curve selected; curve must be circular" );
					break;
			}
		}
	}
	
	/**
	 * Splits the given line so that it intersects the given point
	 * @param model the given {@link GeometricModel model}
	 * @param hostLineElem the given {@link ModelElement host line}
	 * @param splitPoint the given {@link PointXY break point}
	 */
	private void splitUsingPoint( final GeometricModel model, 
								  final ModelElement hostLineElem, 
								  final PointXY splitPoint ) {	
		LineXY newLine1;
		LineXY newLine2;
		
		// get the math equivalent of the host line
		final LineXY hostLine = EntityRepresentationUtil.getLine( hostLineElem );
		
		// cache the point coordinates of the host line
		final double x1 = hostLine.getX1();
		final double y1 = hostLine.getY1();
		final double x2 = hostLine.getX2();
		final double y2 = hostLine.getY2();
		
		// get the split point coordinates
		final double sx = splitPoint.getX();
		final double sy = splitPoint.getY();
		
		// is the line horizontal?
		if( hostLine.isHorizontal() ) {
			newLine1 = new LineXY( x1, y1, sx, y2 );
			newLine2 = new LineXY( sx, y1, x2, y2 );
		}
		
		// is the line vertical?
		else if( hostLine.isVertical() ) {
			newLine1 = new LineXY( x1, y1, x2, sy );
			newLine2 = new LineXY( x1, sy, x2, y2 );
		}
		
		// must be an angular line
		else {
			newLine1 = new LineXY( x1, y1, sx, sy );
			newLine2 = new LineXY( sx, sy, x2, y2 );
		}
		
		// copy the properties of the host element into the new line elements
		final ModelElement newLineElemA = new CxModelElement( newLine1 );
		final ModelElement newLineElemB = new CxModelElement( newLine2 );
		copyProperties( hostLineElem, newLineElemA );
		copyProperties( hostLineElem, newLineElemB );
		
		// add the lines to the model
		model.addPhysicalElement( newLineElemA, newLineElemB );
		
		// remove the previous (host) line
		model.erase( hostLineElem );
	}
	
	/**
	 * Splits the given lines until the lines intersect
	 * @param controller the given {@link ApplicationController controller}
	 * @param hostLine the given {@link ModelElement host line}
	 * @param splitLine the given {@link LineXY split line}
	 */
	private void splitUsingLine( final ApplicationController controller, 
								 final ModelElement hostLineElem, 
								 final LineXY splitLine ) {
		// get the host line
		final LineXY hostLine = EntityRepresentationUtil.getLine( hostLineElem );
		
		// compute the intersection point
		final PointXY intersectionPoint = 
			CxIntersectionUtil.getIntersectionPoint( hostLine, splitLine );
		
		// no intersection found
		if( intersectionPoint == null ) {
			controller.setStatusMessage( "No intersection between the lines was found" );
		}
		
		// lines are the same
		else if( hostLine.equals( splitLine ) ) {
			controller.setStatusMessage( "The lines are not compatible for splitting" );
		}
		
		// perform the split
		else {
			// get the geometric model instance
			final GeometricModel model = controller.getModel();
			
			// get the end-points for the host line
			final PointXY startPt = hostLine.getBeginPoint();
			final PointXY endPt = hostLine.getEndPoint();
			
			// create the new lines
			final LineXY newLine1 = new LineXY( startPt, intersectionPoint );
			final LineXY newLine2 = new LineXY( intersectionPoint, endPt );
			
			// copy the properties of the host element into the new line elements
			final ModelElement newLineElemA = new CxModelElement( newLine1 );
			final ModelElement newLineElemB = new CxModelElement( newLine2 );
			copyProperties( hostLineElem, newLineElemA );
			copyProperties( hostLineElem, newLineElemB );
			
			// add the lines to the model
			model.addPhysicalElement( newLineElemA, newLineElemB );
			
			// remove the previous (host) line
			model.erase( hostLineElem );
		}
	}
	
	/**
	 * Splits the given lines until the lines intersect
	 * @param controller the given {@link ApplicationController controller}
	 * @param hostLine the given {@link ModelElement host line}
	 * @param splitCurve the given {@link CircleXY split curve}
	 */
	private void splitUsingCurve( final ApplicationController controller, 
								  final ModelElement hostLineElem, 
								  final CircleXY splitCurve ) {
		// get the line
		final LineXY hostLine = EntityRepresentationUtil.getLine( hostLineElem ); 
		
		// compute the intersection point
		final Intersection intersection = 
			PointXY.intersection( splitCurve, hostLine );
		
		// no intersection found
		if( intersection.hasError() ) {
			controller.setStatusMessage( intersection.getErrorMessage() );
		}
		
		// perform the split
		else {
			// get the geometric model instance
			final GeometricModel model = controller.getModel();
			
			// get the end-points for the host line
			final PointXY startPt = hostLine.getBeginPoint();
			final PointXY endPt = hostLine.getEndPoint();
			
			// get the intersection points
			final PointXY[] points = intersection.getPointArray();
			
			// create the new lines
			ModelElement[] newLines = null;
			switch( points.length ) {
				case 1:
					newLines = new ModelElement[2];
					newLines[0] = new CxModelElement( new LineXY( startPt, points[0] ) );
					newLines[1] = new CxModelElement( new LineXY( points[0], endPt ) );
					break;
					
				case 2:
					newLines = new ModelElement[3];
					newLines[0] = new CxModelElement( new LineXY( startPt, points[0] ) );
					newLines[1] = new CxModelElement( new LineXY( points[0], points[1] ) );
					newLines[2] = new CxModelElement( new LineXY( points[1], endPt ) );
					break;
			}
			
			// if lines were created ...
			if( newLines != null ) {
				// add the lines to the model
				model.addPhysicalElement( newLines );
				
				// remove the previous (host) line
				model.erase( hostLineElem );
			}
			else {
				controller.setStatusMessage( "No splitting occurred" );
			}
		}
	}	
	
}