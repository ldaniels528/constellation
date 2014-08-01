package constellation.app.functions.layout;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.functions.UserInputDialog;
import constellation.app.functions.UserInputObserver;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxDialog;

/**
 * The LAYOUT::TEXT NOTE function
 * @author lawrence.daniels@gmail.com
 */
public class TextNoteFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
				"Key the note text",
				"Select or Indicate the locating #point of the note" 
			);
	
	// internal fields
	private UserInputDialog<String> inputDialog;
	private String inputText;
	
	/**
	 * Default constructor
	 */
	public TextNoteFunction() {
		super( 
			"LAYOUT", "TEXTNOTE", 
			"images/commands/layout/textnote.png", 
			"docs/functions/layout/textnote.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#getInputDialog()
	 */
	public CxDialog getInputDialog() {
		return inputDialog;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.StructuredSelectionFunction#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// clear the old element
		//controller.getModel().clearTemporaryElement();
		
		// create the user input dialog
		if( inputDialog == null ) {
			inputDialog = new UserInputDialog<String>( controller, "Text Note", 4, 40, new STEP1_TextInputHandler(), "" );
		}
		inputDialog.makeVisible();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onFinish(constellation.functions.ApplicationController)
	 */
	@Override
	public void onFinish( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onFinish( controller );
		
		// hide the dialog
		inputDialog.setVisible( false );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		switch( steps.currentIndex() ) {
			case Steps.STEP_2: handleStep2( controller, mouseClick ); break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// allow the parent update
		super.processMouseMovement( controller, mousePos );
		
		// if we're on STEP #2, allow the user to move around the text
		if( steps.currentIndex() == Steps.STEP_2 ) {			
			// get the current space position of the mouse cursor
			final PointXY p = controller.untransform( mousePos );
			
			// get the model instance
			final GeometricModel model = controller.getModel();
						
			// get the current temporary drawing element
			final RenderableElement tempElement = model.getTemporaryElement();
			
			// if the temporary element isn't set ...
			if( tempElement == null ) {				
				// set the temporary element
				model.setTemporaryElement( new TextNoteXY( p, inputText ) );
			}
			
			// otherwise, just move the current element
			else {
				final TextNoteXY element = (TextNoteXY)tempElement;
				element.getLocation().setLocation( p ); 
			}
			
			// request a redraw
			controller.requestRedraw();
		}
	}

	/** 
	 * Handles STEP 1: Key the note text
	 * @param controller the given {@link ApplicationController controller}
	 * @param inputText the given user inputed text
	 */
	private void handleStep1( final ApplicationController controller, final String inputText ) {
		// capture the user input text
		this.inputText = inputText;
		
		// dispose of the input dialog
		inputDialog.dispose();
		
		// advance to the next step
		advanceToNextStep( controller );
	}

	/** 
	 * Handles STEP 2: Select or Indicate to the starting point of the note
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the current space position of the mouse cursor
		final PointXY p = controller.untransform( mouseClick );
		
		// get the model instance
		final GeometricModel model = controller.getModel();
						
		// set the temporary element
		model.addPhysicalElement( new CxModelElement( new TextNoteXY( p, inputText ) ) );
	
		// clear the temporary element
		this.inputText = null;
		model.clearTemporaryElement();
		
		// advance to the next step
		advanceToNextStep( controller );
	}
	
	/**
	 * STEP 1: Retrieve input from the user
	 * @author lawrence.daniels@gmail.com
	 */
	private class STEP1_TextInputHandler implements UserInputObserver<String> {

		/* 
		 * (non-Javadoc)
		 * @see constellation.app.functions.UserInputObserver#update(constellation.functions.ApplicationController, java.lang.Object)
		 */
		public void update( final ApplicationController controller, final String inputText ) {
			handleStep1( controller, inputText );
		}
	}

}
