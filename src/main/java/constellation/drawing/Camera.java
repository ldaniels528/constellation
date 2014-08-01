package constellation.drawing;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JComponent;

import constellation.ApplicationController;
import constellation.model.GeometricModel;

/**
 * Constellation Camera
 * @author lawrence.daniels@gmail.com
 */
public interface Camera { 	
	
	/**
	 * Returns the camera's view port clipping plane
	 * @return the {@link Rectangle clipping plane}
	 */
	Rectangle getClippingPlane();
	
	/**
	 * Initializes the camera and sets the focus at the center of the screen
	 * @param drawingComp the given drawing {@link JComponent component}
	 */
	void init( JComponent drawingComp );
	
	/**
	 * Copies the off-screen graphics context to the content pane (virtual screen)
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel model}
	 * @param image the given {@link Image off-screen image buffer}
	 */
	void render( ApplicationController controller, GeometricModel model, Image image );
	
	/**
	 * Repositions the camera to the given (x,y) coordinates
	 */
	void reset();
	
	/**
	 * Moves the camera relative to the specified delta values
	 * @param deltaX the given delta x-coordinate 
	 * @param deltaY the given delta y-coordinate
	 */
	void pan( double deltaX, double deltaY );
	
	/**
	 * Moves the camera to the specified location
	 * @param x the given x-coordinate
	 * @param y the given y-coordinate
	 */
	void panTo( double x, double y );
	
	/**
	 * Rotates the camera by the given angles in the X-, Y- and Z-axis 
	 * @param angleDX the given rotation in the X-axis
	 * @param angleDY the given rotation in the Y-axis
	 */
	void adjustRotation( double angleDX, double angleDY );
	
	/**
	 * Sets the zoom factor
	 * @param zoomFactor the given zoom factor
	 */
	void setZoomFactor( double zoomFactor );
	
}
