package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;

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
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LINE::VERTICAL function
 * @author lawrence.daniels@gmail.com
 */
public class LineVerticalFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the start #point of the vertical line",
		"Select or Indicate the end #point of the vertical line"
	);
	private EntityEditorDialog dialog;
	private PointXY anchorPoint;

	/**
	 * Default constructor
	 */
	public LineVerticalFunction() {
		super( 
			"LINE", "VERTICAL", 
			"images/commands/line/line-vertical.gif", 
			"docs/functions/line/vertical.html", 
			STEPS, 
			EntityEditorDialog.class 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handleClickedPoint( controller, element, false );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// reset the values
		anchorPoint	= null;
		
		// setup the dialog
		if( dialog == null ) {
			dialog = EntityEditorDialog.getInstance( controller );
		}
		dialog.setCallingFunction( this, EntityTypes.LINE );
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
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleClickedPoint( controller, pickedPt, false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				final PointXY point = controller.untransform( mouseClick );
				handleClickedPoint( controller, new CxModelElement( point ), true );
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseMovement(constellation.functions.ApplicationController, java.awt.Point)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {		
		// allow parent to update
		super.processMouseMovement( controller, mousePos );
		
		// manipulate temporary geometry
		if( steps.currentIndex() == STEP_2 ) {
			// create the planar point
			final PointXY endPoint = controller.untransform( mousePos );
			
			// get the model
			final GeometricModel model = controller.getModel();
			
			// create the temporary line
			model.setTemporaryElement( createTemporaryLine( anchorPoint, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link ModelElement click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleClickedPoint( final ApplicationController controller, 
								   	 final ModelElement clickPoint, 
								   	 final boolean addToModel ) {
		if( clickPoint != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// get the point
			final PointXY point = EntityRepresentationUtil.getPoint( clickPoint );
			
			// get the temporary geometry
			switch( steps.currentIndex() ) {
				case STEP_1:
					// set the anchor point
					anchorPoint = point;
					
					// add the point to the model?
					if( addToModel ) {
						model.addPhysicalElement( clickPoint );
					}
					break;
					
				case STEP_2:
					// add the line to the model
					final LineXY line = computeVerticalLine( anchorPoint, point );
					model.addPhysicalElement( line );
					
					// add the point to the model?
					if( addToModel ) {
						model.addPhysicalElement( line.getEndPoint() );
					}
					break;
			}
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}

	/**
	 * Creates a new vertical line
	 * @return a new vertical {@link LineXY line}
	 */
	private LineXY computeVerticalLine( final PointXY p1, final PointXY p2 ) {
		return dialog.isInfinite()
					? LineXY.createInfiniteVerticalLine( p1, p2 )
					: LineXY.createVerticalLine( p1, p2 );
	}
	
	/**
	 * Creates the temporary line geometry
	 * @return the {@link RenderableElement temporary elements}
	 */
	private RenderableElement createTemporaryLine( final PointXY p1, final PointXY p2 ) {
		// create the line
		final LineXY line = computeVerticalLine( p1, p2 );
		
		// create the text
		final TextNoteXY note = new TextNoteXY( line.getMidPoint(), format( "%3.2f", line.length() ) );
		
		// return the composition
		return new HUDXY( line, p1, line.getEndPoint(), note );
	}
	
}