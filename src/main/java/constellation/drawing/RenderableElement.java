package constellation.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * This interface is implemented by all classes
 * that are to be rendered by a Constellation Camera.
 * @author lawrence.daniels@gmail.com
 */
public interface RenderableElement {
	
	/**
	 * Renders the geometry onto the given graphics context
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param matrix the given {@link MatrixWCStoSCS WCS to SCS matrix}
	 * @param clipper the given {@link Rectangle clipping boundary}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param color the rendering {@link Color color} or <tt>null</tt> for the element's color
	 */
	void render( ApplicationController controller, 
				 GeometricModel model, 
				 MatrixWCStoSCS matrix, 
				 Rectangle clipper, 
				 Graphics2D g, 
				 Color color );

}
