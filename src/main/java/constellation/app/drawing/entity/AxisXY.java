package constellation.app.drawing.entity;

import static constellation.drawing.LinePatternDefs.SOLID_STROKE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.RenderableElement;
import constellation.drawing.ScratchPad;
import constellation.drawing.entities.PointXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Represents the two-dimensional axis located at the origin (0,0) 
 * @author lawrence.daniels@gmail.com
 */
public class AxisXY implements RenderableElement {
	private static final int WIDTH 	= 50;
	private static final int HEIGHT	= 50;
	private final PointXY originPt;

	/**
	 * Default constructor
	 */
	public AxisXY() {
		this.originPt = new PointXY( 0d, 0d );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.functions.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color, boolean)
	 */
	public void render( final ApplicationController controller,
						final GeometricModel model,
						final MatrixWCStoSCS matrix,
						final Rectangle clipper, 
						final Graphics2D g, 
						final Color color ) {
		// get the preferences instance
		final SystemPreferences preferences = controller.getSystemPreferences();
		
		// if the grid is shown ...
		if( preferences.showGrids() ) {
			// get a screen point from the scratch pad
			final Point screenPoint = ScratchPad.getProjectionPoint();
			
			// translate the point to screen coordinates
			matrix.transform( originPt, screenPoint );
			
			// draw the axis?
			if( clipper.intersectsLine( screenPoint.x, screenPoint.y, WIDTH, HEIGHT ) ) {
				final int x1 = screenPoint.x;
				final int y1 = screenPoint.y;
				final int x2 = x1 + WIDTH;
				final int y2 = y1 + HEIGHT;
				
				// set the color & stroke
				g.setStroke( SOLID_STROKE ); 
				g.setColor( color );	
				
				// draw the vertical line & arrow head
				g.drawLine( x1, y1, x1, y2 );
				g.fillPolygon( new int[] { x1, x1-3, x1, x1+3, x1 }, new int[] { y2, y2, y2+5, y2, y2 }, 5 );
				g.drawString( "y", x1 - 3, y2 + 17 );
				
				// draw the horizontal line & arrow head
				g.drawLine( x1, y1, x2, y1 );
				g.fillPolygon( new int[] { x2, x2, x2+5, x2, x2 }, new int[] { y1, y1-3, y1, y1+3, y1 }, 5 );
				g.drawString( "x", x2 + 9, y1 + 5 );
			}
		}
	}

}
