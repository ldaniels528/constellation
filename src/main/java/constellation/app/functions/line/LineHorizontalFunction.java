package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.functions.edit.EntityEditorDialog;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LINE::HORIZONTAL function
 * @author lawrence.daniels@gmail.com
 */
public class LineHorizontalFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the start #point of the horizontal line",
		"Select or Indicate the end #point of the horizontal line"
	);
	
	// internal fields
	private EntityEditorDialog dialog;
	private PointXY anchorPoint;

	/**
	 * Default constructor
	 */
	public LineHorizontalFunction() {
		super( 
			"LINE", "HORIZONTAL", 
			"images/commands/line/line-horizontal.png", 
			"docs/commands/line/horizontal.html", 
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
		dialog = EntityEditorDialog.getInstance( controller );
		dialog.setCallingFunction( this, EntityTypes.LINE );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onFinish(constellation.functions.ApplicationController)
	 */
	@Override
	public void onFinish( final ApplicationController controller ) {
		dialog.setVisible( false );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the model and click point
		final PointXY point = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				
				// handle the point
				handleClickedPoint( controller, pickedPt, false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
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
		// allow parent to perform dynamic highlighting
		super.processMouseMovement( controller, mousePos );
		
		// if we're on Step #2 ...
		if( steps.currentIndex() == STEP_2 ) {
			// get the model
			final GeometricModel model = controller.getModel();
			
			// create the planar point
			final PointXY endPoint = controller.untransform( mousePos );
			
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
			
			// get the temporary geometry
			switch( steps.currentIndex() ) {
				case STEP_1:
					// set the anchor point
					anchorPoint = EntityRepresentationUtil.getPoint( clickPoint );
					
					// add the point to the model?
					if( addToModel ) {
						model.addPhysicalElement( clickPoint );
					}
					break;
					
				case STEP_2:
					// add the line to the model
					final LineXY line = computeHorizontalLine( anchorPoint, EntityRepresentationUtil.getPoint( clickPoint ) );
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
	 * Creates a new horizontal line
	 * @return a new horizontal {@link LineXY line}
	 */
	private LineXY computeHorizontalLine( final PointXY p1, final PointXY p2 ) {
		return dialog.isInfinite()
				? LineXY.createInfiniteHorizontalLine( p1, p2 )
				: LineXY.createHorizontalLine( p1, p2 );
	}
	
	/**
	 * Creates the temporary line geometry
	 * @return the {@link RenderableElement temporary elements}
	 */
	private RenderableElement createTemporaryLine( final PointXY p1, final PointXY p2 ) {
		// create the line
		final LineXY line = computeHorizontalLine( p1, p2 );
		
		// create the text
		final TextNoteXY note = new TextNoteXY( line.getMidPoint(), format( "%3.2f", line.length() ) );
		
		// return the composition
		return new HUDXY( line, p1, line.getEndPoint(), note );
	}
	
}