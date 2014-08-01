package constellation.drawing.entities;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.ScratchPad;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Represents a Picture (image) on a two-dimensional plane
 * @author lawrence.daniels@gmail.com
 */
public class PictureXY implements EntityRepresentation {
	private static final Image THUMB_NAIL = CxContentManager.getInstance().getImage( "images/camera/thumbnail.gif" );
	private static final Logger logger = Logger.getLogger( PictureXY.class );
	private final UserImage image;
	private final PointXY location;
	
	/**
	 * Creates a new 2D picture (image)
	 * @param x the given x-coordinate
	 * @param y the given x-coordinate
	 * @param image the given {@link UserImage image}
	 */
	public PictureXY( final double x, final double y, final UserImage image ) {
		this.location	= new PointXY( x, y ); 
		this.image		= image;
	}
	
	/**
	 * Creates a new 2D picture (image)
	 * @param point the {@link PointXY location} on the canvas
	 * @param image the given {@link UserImage image}
	 */
	public PictureXY( final PointXY point, final UserImage image ) {
		this.image		= image;
		this.location	= point; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#duplicate(double, double)
	 */
	public EntityRepresentation duplicate( final double dx, final double dy ) {
		return new PictureXY( location.getOffset( dx, dy ), image );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		// get a screen point
		final Point screenPoint = ScratchPad.getProjectionPoint();
		
		// transform the spatial point to screen coordinates
		matrix.transform( location, screenPoint );
		
		// compute the screen region around the text
		final Rectangle rsceen = new Rectangle( screenPoint.x, screenPoint.y, getWidth(), getHeight() );
		
		// transform the screen region to a space region
		return matrix.untransform( rsceen );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#getLocation()
	 */
	public PointXY getLocation() {
		return location;
	}
	
	/**
	 * Returns the image width
	 * @return the image width
	 */
	public int getWidth() {
		return image.getWidth();
	}

	/**
	 * Returns the image height
	 * @return the image height
	 */
	public int getHeight() {
		return image.getHeight();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.PICTURE;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.IMAGE;
	}
	
	/**
	 * Returns the user image
	 * @return the {@link UserImage user image}
	 */
	public UserImage getUserImage() {
		return image;
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
	public PictureXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the ellipse
		return new PictureXY( p, image );
	}

	/** 
	 * Moves the picture relative to the given delta X and Y coordinates
	 * @param dx the given delta X coordinate
	 * @param dy the given delta Y coordinate
	 */
	public void moveRelative( final double dx, final double dy ) {
		location.x += dx; 
		location.y += dy;
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
		// get a screen point
		final Point screenPoint = ScratchPad.getProjectionPoint();
		
		// translate the point to screen coordinates
		matrix.transform( location, screenPoint );
		
		// get the scaled image
		final Image image = getScaledImage( matrix, clipper );
		if( image != null ) {
			// draw the image
			g.drawImage( image, screenPoint.x, screenPoint.y, null );
			
			// is the image currently being highlighted?
			final SystemPreferences preferences = controller.getSystemPreferences();
			if( color.equals( preferences.getHighlightedGeometryColor() ) ) {
				final int width = image.getWidth( null );
				final int height = image.getHeight( null );
				
				g.setColor( color );
				g.drawRect( screenPoint.x, screenPoint.y, width, height );
				g.drawRect( screenPoint.x - 1, screenPoint.y - 1, width + 2, height + 2 );
			}
		}
	}
	
	/** 
	 * Return the scaled image (based on the scale of the matrix)
	 * @param matrix the given {@link MatrixWCStoSCS SCS to MCS matrix}
	 * @param clipper the {@link Rectangle clipping boundary}
	 * @return the {@link Image image} to render
	 */
	private Image getScaledImage( final MatrixWCStoSCS matrix, final Rectangle clipper ) {
		int width = 0;
		int height = 0;
		
		// get the source image
		final BufferedImage bimage = image.getImage();

		// get the scale
		final double scale = matrix.getScale();
	
		try {	
			// if the scale is normal ... 
			if( scale == 1.0d ) {
				return bimage;
			}
			
			// draw the scaled image 
			else {
				// determine the scaled image dimensions
				width = (int)( scale * (double)bimage.getWidth() );
				height = (int)( scale * (double)bimage.getHeight() );
				
				// is the image too small?
				if( ( width <= 3 ) || ( height <= 3 ) ) {
					return THUMB_NAIL;
				}
				
				// is the image too large?
				else if( ( width >= clipper.width ) || ( height >= clipper.height ) ) {
					return createPlaceHolder( clipper, width, height );
				}
					
				// scale & draw the image
				else {
					return bimage.getScaledInstance( width, height, Image.SCALE_FAST );
				}
			}
		}
		catch( final RuntimeException e ) {
			logger.error( format( "scale = %3.2f, width = %d, height = %d\n", scale, this.getWidth(), this.getHeight() ), e );
			return createPlaceHolder( clipper, width, height );
		}
	}
	
	/**
	 * Creates a place holder image when the requested image's width or height exceeds the clipping plane
	 * @param clipper the given {@link Rectangle clipping plane}
	 * @param requestedWidth the requested width
	 * @param requestedHeight the requested height
	 * @return the place holder {@link BufferedImage image}
	 */
	private BufferedImage createPlaceHolder( final Rectangle clipper, final int requestedWidth, final int requestedHeight ) {
		// insure that the width and height don't exceed the maximums
		final int width = ( requestedWidth <= clipper.width ) ? requestedWidth : clipper.width;
		final int height = ( requestedHeight <= clipper.height ) ? requestedHeight : clipper.height;
		
		// create the image
		final BufferedImage tmpImage = new BufferedImage( width, height, BufferedImage.TYPE_3BYTE_BGR );
		
		// draw a rectangle to represent the image
		final Graphics2D g = (Graphics2D)tmpImage.getGraphics();
		g.setColor( WHITE );
		g.fillRect( 0, 0, width, height );
		g.setColor( BLACK );
		g.drawRect( 0, 0, width-1, height-1 );
		
		// draw the image's label
		/*
		final FontMetrics fontMetrics = g.getFontMetrics();
		final RectangleXY r = fontMetrics.getStringBounds( getLabel(), g );
		final int cx = ( width - (int)r.getWidth() ) / 2;
		final int cy = ( height - (int)r.getHeight() ) / 2;
		g.drawString( getLabel(), cx, cy );
		*/
		// return the image
		return tmpImage;
	}
}
