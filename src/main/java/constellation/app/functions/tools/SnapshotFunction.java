package constellation.app.functions.tools;

import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static javax.swing.JFileChooser.APPROVE_OPTION;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.Camera;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.choosers.CxImageFileChooser;
import constellation.ui.components.choosers.CxImageFileChooser.ImageFileFilter;

/**
 * The SYSTEM::SNAPSHOT function
 * @author lawrence.daniels@gmail.com
 */
public class SnapshotFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Indicate to take the snapshot"
	);
	
	/**
	 * Default constructor
	 */
	public SnapshotFunction() {
		super( "TOOLS", "SNAPSHOT", null, null, STEPS );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#process(constellation.FunctionController, constellation.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// Capture only 'Indicate' clicks
		if( mouseClick.getButton() == BUTTON_INDICATE ) {
			takeSnapshot( controller );
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Takes a snapshot of the drawing pane 
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static void takeSnapshot( final ApplicationController controller ) {		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the drawing dimensions
		final Dimension dimension = controller.getDrawingDimensions();
		
		// get the camera instance
		final Camera camera = controller.getCamera();
		
		// create a buffered image
		final BufferedImage image = 
			new BufferedImage( dimension.width, dimension.height, TYPE_3BYTE_BGR );
		
		// get the snapshot of the model
		camera.render( controller, model, image );
		
		// open the 'Save As' dialog
		final CxImageFileChooser chooser = CxImageFileChooser.getInstance();
		final int returnVal = chooser.showSaveDialog( (Component)controller );
	    if( returnVal == APPROVE_OPTION ) {
	    	final ImageFileFilter fileFilter = (ImageFileFilter)chooser.getFileFilter();
	    	try {
	    		fileFilter.write( image, chooser.getSelectedFile() );
			} 
	    	catch( final IOException e ) {
				controller.showErrorDialog( "Image Save Error", e );
			}
	    }
	}

}
