package constellation.drawing.entities;

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
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents a Text Note on a two-dimensional plane
 * @author lawrence.daniels@gmail.com
 */
public class TextNoteXY implements EntityRepresentation {
	// internal fields
	private final PointXY location;
	private String textString;
	private int textWidth;
	private int textHeight;
	
	/**
	 * Creates a new text note
	 * @param point the given {@link PointXY position} of the note on the canvas
	 * @param textString the given text string
	 */
	public TextNoteXY( final PointXY point, final String textString ) {
		this.location		= point;
		this.textString		= textString;
		this.textWidth	 	= CxFontManager.getTextWidth( textString );
		this.textHeight		= CxFontManager.getTextHeight();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#duplicate(constellation.drawing.entities.representations.PointXY)
	 */
	public TextNoteXY duplicate( double dx, double dy ) {
		return new TextNoteXY( location.getOffset( dx, dy ), textString );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		// get a screen point
		final Point screenPt = ScratchPad.getProjectionPoint();
		
		// transform the spatial point to screen coordinates
		matrix.transform( location, screenPt );
		
		// get the (x,y) coordinates 
		final int x = screenPt.x - ( textWidth / 2 );
		final int y = screenPt.y - ( textHeight / 2 );
		
		// compute the screen region around the text
		final Rectangle rect = new Rectangle( x, y, textWidth, textHeight );
		
		// transform the screen region to a space region
		return matrix.untransform( rect );
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
	
	/** 
	 * Sets the text string
	 * @param textString the text string
	 */
	public void setTextString( final String textString ) {
		this.textString	= textString;
		this.textWidth	= CxFontManager.getTextWidth( textString );
		this.textHeight	= CxFontManager.getTextHeight();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.TEXTNOTE;
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
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public TextNoteXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the ellipse
		return new TextNoteXY( p, textString );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return boundary.intersects( getBounds( matrix ) );
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
		// get a screen point
		final Point screenPt = ScratchPad.getProjectionPoint();
		
		// transform the spatial point to screen coordinates
		matrix.transform( location, screenPt );
		
		// get the (x,y) coordinates 
		final int x = screenPt.x - ( textWidth / 2 );
		final int y = screenPt.y - ( textHeight / 2 );
		
		// if the note is visible ...
		if( clipper.contains( x, y, textWidth, textHeight ) ) {
			// draw the note
			g.setColor( color );
			g.drawString( textString, x , y );
		}
	}
	
}
