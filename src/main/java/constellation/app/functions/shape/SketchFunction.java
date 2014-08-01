package constellation.app.functions.shape;

import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.PolyLineXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LAYOUT::SKETCH function
 * @author lawrence.daniels@gmail.com
 */
public class SketchFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Drag to draw or Indicate to create the #element"
	);
	private PolyLineXY polyLine;

	/**
	 * Default constructor
	 */
	public SketchFunction() {
		super( 
			"LAYOUT", "SKETCH", 
			"images/commands/curve/sketch.png", 
			"docs/functions/curve/sketch.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to do it's job
		super.onStart( controller );
		
		// initialize a new polyLine
		polyLine = new PolyLineXY();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// if secondary click ...
		if( mouseClick.getButton() == MouseClick.BUTTON_INDICATE ) {
			// create the element
			final ModelElement element = new CxModelElement( polyLine );
			
			// update the appearance
			// TODO dialog.update( element );
			
			// complete the polyLine
			final GeometricModel model = controller.getModel();
			model.addPhysicalElement( element );
			
			// set the status message
			controller.setStatusMessage( format( "Created poly line '%s'", element.getLabel() ) );
			
			// advance to next step
			advanceToNextStep( controller );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseDrag(constellation.functions.ApplicationController, constellation.functions.MouseClick, constellation.functions.MouseClick)
	 */
	@Override
	public boolean processMouseDrag( final ApplicationController controller, 
									 final MouseClick oldMousePos, 
									 final MouseClick newMousePos ) {
		// if primary button drag ...
		if( oldMousePos.getButton() == MouseClick.BUTTON_SELECT ) {
			// determine the real world position of the mouse
			final PointXY newPos = controller.untransform( newMousePos );
		
			// move the vertex
			polyLine.append( newPos );
			
			// reset the limits
			polyLine.resetCache();
			
			// set it as a temporary element
			final GeometricModel model = controller.getModel();
			model.setTemporaryElement( polyLine );
			
			// re-draw the scene
			controller.requestRedraw();
			
			// drag event successful
			return true;
		}
		return false;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#supportsDrag()
	 */
	@Override
	public boolean supportsDrag() {
		return true;
	}
	
}