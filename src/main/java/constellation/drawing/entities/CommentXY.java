package constellation.drawing.entities;

import static constellation.drawing.EntityTypes.COMMENT;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.CxFontManager;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.ScratchPad;
import constellation.drawing.elements.ModelElement;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Represents a Drawing Comment
 * @author lawrence.daniels@gmail.com
 */
public class CommentXY implements EntityRepresentation {
	// internal fields
	private final PointXY location;
	private String textString;
	private int textWidth;
	private int textHeight;
	
	/**
	 * Creates a new comment
	 * @param point the given {@link PointXY position} of the comment on the canvas
	 * @param textString the given text string
	 */
	public CommentXY( final PointXY point, final String textString ) {
		this.location		= point;
		this.textString		= textString;
		this.textWidth	 	= CxFontManager.getTextWidth( textString );
		this.textHeight		= CxFontManager.getTextHeight();
	}
	
	/**
	 * Initializes the entity
	 * @param element the given model element to initialize
	 */
	public void init( final ModelElement element ) {
		element.setColor( Color.BLUE );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getType()
	 */
	public EntityTypes getType() {
		return COMMENT;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.TEXT;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#duplicate(constellation.drawing.entities.representations.PointXY)
	 */
	public CommentXY duplicate( final double dx, final double dy ) {
		final PointXY p = location.getOffset( dx, dy );
		return new CommentXY( p, textString );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		// get a screen point
		final Point screenPos = ScratchPad.getProjectionPoint();
		
		// transform the spatial point to screen coordinates
		matrix.transform( location, screenPos );
		
		// get the center coordinate
		final int cx = screenPos.x - ( textWidth / 2 );
		final int cy = screenPos.y;
		
		// get the boundary coordinates
		final int bx = cx;
		final int by = cy - textHeight;
		final int bw = textWidth + 10;
		final int bh = textHeight * 5;
		
		// compute the screen region around the text
		final Rectangle r = new Rectangle( bx, by, bw, bh );
		
		// transform the screen region to a space region
		return matrix.untransform( r );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#getLocation()
	 */
	public PointXY getLocation() {
		return location;
	}
	
	/** 
	 * Returns the text string
	 * @return the text string
	 */
	public String getTextString() {
		return textString;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return boundary.intersects( getBounds( matrix ) );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public CommentXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the comment
		return new CommentXY( p, textString );
	}

	/** 
	 * Sets the text string
	 * @param textString the text string
	 */
	public void setTextString( final String textString ) {
		this.textString = textString;
		this.textWidth	= CxFontManager.getTextWidth( textString );
		this.textHeight	= CxFontManager.getTextHeight();
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
		// show comments?
		final SystemPreferences preferences = controller.getSystemPreferences();
		if( preferences.showComments() ) {	
			// get a screen point
			final Point screenPos = ScratchPad.getProjectionPoint();
			
	 		// transform the spatial point to screen coordinates
			matrix.transform( location, screenPos );
			
			// get the center coordinate
			final int cx = screenPos.x - ( textWidth / 2 );
			final int cy = screenPos.y;
			
			// draw the postIt note
			drawPostIt( g, cx, cy );
			
			// if the note is visible ...
			if( clipper.contains( cx, cy, textWidth, textHeight ) ) {
				// draw the note
				g.setColor( color );
				g.drawString( textString, cx , cy );
			}
		}
	}

	/** 
	 * Draws the Post-It note
	 * @param g the given {@link Graphics2D graphics context}
	 * @param cx the given X-axis center coordinate
	 * @param cy the given Y-axis center coordinate
	 */
	private void drawPostIt( final Graphics2D g, final int cx, final int cy ) {
		// get the boundary coordinates
		final int bx = cx;
		final int by = cy - textHeight;
		final int bw = textWidth + 10;
		final int bh = textHeight * 5;
		
		// paint the boundary yellow
		g.setColor( Color.YELLOW );
		g.fillRect( bx, by, bw, bh );
		
		// draw the border (right side only)
		g.setColor( new Color( 0xEF, 0xEF, 0x00 ) );
		g.drawLine( bx + bw, by, bx + bw, by + bh );
		
		// draw the horizontal lines
		g.setColor( Color.LIGHT_GRAY );
		for( int y = cy; y < cy + bh; y += 20 ) {
			g.drawLine( bx, y, bx + bw, y );
		}
	}
	
}
