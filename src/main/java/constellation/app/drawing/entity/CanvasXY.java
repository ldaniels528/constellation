package constellation.app.drawing.entity;

import static java.lang.Math.abs;
import static constellation.drawing.LinePatternDefs.DASHED_STROKE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.RenderableElement;
import constellation.drawing.ScratchPad;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Represents the drawing canvas
 * @author lawrence.daniels@gmail.com
 */
public class CanvasXY implements RenderableElement {
	private final PointXY originPt;

	/**
	 * Default constructor
	 */
	public CanvasXY() {
		this.originPt = new PointXY( 0d, 0d );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color)
	 */
	public void render( final ApplicationController controller,
						final GeometricModel model,
						final MatrixWCStoSCS matrix,
						final Rectangle clipper, 
						final Graphics2D g, 
						final Color color ) {
		// get the preferences instance
		final SystemPreferences preferences = controller.getSystemPreferences();
		
		// is there a background image set?
		final Image backgroundImage = model.getBackgroundImage();
		if( backgroundImage != null ) {
			renderBackground( backgroundImage, clipper, g );
		}
		
		// otherwise, just clear the buffer
		else {
			g.setColor( preferences.getBackgroundColor() );
			g.fillRect( 0, 0, clipper.width, clipper.height );
		}
		
		// if grids are visible, display them
		if( preferences.showGrids() ) {
			renderGrid( controller, model, matrix, clipper, g, color );
		}
	}
	
	/**
	 * Renders the geometry onto the given graphics context
	 * @param image the given background {@link Image image}
	 * @param clipper the given {@link Rectangle clipping boundary}
	 * @param g the given {@link Graphics2D graphics context}
	 */
	private void renderBackground( final Image image,
								   final Rectangle clipper,
								   final Graphics2D g ) {
		if( image != null ) {
			final int width	 	= image.getWidth( null );
			final int height	= image.getHeight( null );
			
			// tile the background
			for( int y = 0; y < clipper.height; y += height ) {
				for( int x = 0; x < clipper.width; x += width ) {
					g.drawImage( image, x, y, null );
				}
			}
		}
	}
	
	/**
	 * Renders the geometry onto the given graphics context
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param matrix the given {@link MatrixWCStoSCS transformational matrix}
	 * @param clipper the given {@link Rectangle clipping boundary}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param color the rendering {@link Color color} for the grid
	 */
	private void renderGrid( final ApplicationController controller,
							 final GeometricModel model,
							 final MatrixWCStoSCS matrix,
							 final Rectangle clipper, 
							 final Graphics2D g, final Color color ) {
		// determine the space coordinates
		final RectangleXY rx = matrix.untransform( clipper );
		
		// get the line spacing
		final double spacing = model.getUnit().getGridSpacing();
		
		// get the nearest grid X & Y coordinates
		final double startX = rx.getX() - ( rx.getX() % spacing );
		final double startY = rx.getY() - ( rx.getY() % spacing );
		final double endX	= rx.getX() + rx.getWidth();
		final double endY	= rx.getY() + rx.getHeight();
		
		// get a screen point from the scratch pad
		final Point screenPt = ScratchPad.getProjectionPoint();
		
		// is the grid spacing wide enough?
		if( isGridWideEnough( matrix, spacing ) ) {	
			// draw the grid lines
			g.setColor( color );
			g.setStroke( DASHED_STROKE );
			originPt.y = 0;
			for( double x = startX; x <= endX; x += spacing ) {
				originPt.x = x;
				matrix.transform( originPt, screenPt );
				g.drawLine( screenPt.x, 0, screenPt.x, clipper.height );
			}
			originPt.x = 0;
			for( double y = startY; y <= endY; y += spacing ) {
				originPt.y = y;
				matrix.transform( originPt, screenPt );
				g.drawLine( 0, screenPt.y, clipper.width, screenPt.y );
			}
		}
	}
	
	/**
	 * Tests to insure that there is adequate space between grid lines
	 * @param matrix the given {@link MatrixWCStoSCS transformational matrix}
	 * @param spacing the spatial distance between grid lines
	 * @return true, if there is at least 10 pixels between the on-screen grid lines
	 */
	private boolean isGridWideEnough( final MatrixWCStoSCS matrix, final double spacing ) {
		// get the space position of the next grid line
		final PointXY nextSpacePt	= new PointXY( originPt.x + spacing, originPt.y + spacing );
		final Point nextScreenPt	= new Point();
		
		// get a screen point from the scratch pad
		final Point screenPt = ScratchPad.getProjectionPoint();
		
		// transform the points
		matrix.transform( originPt, screenPt );
		matrix.transform( nextSpacePt, nextScreenPt );
		
		// the space between screen points must be at least 10
		return ( abs( screenPt.x - nextScreenPt.x ) >= 10 );
	}

}