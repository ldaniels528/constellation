package constellation.app.drawing;

import static constellation.drawing.LinePatternDefs.SOLID_STROKE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import constellation.ApplicationController;
import constellation.app.drawing.entity.AxisXY;
import constellation.app.drawing.entity.CanvasXY;
import constellation.app.preferences.CxSystemPreferences;
import constellation.drawing.Camera;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Constellation Default Camera 
 * @author lawrence.daniels@gmail.com
 */
public class DefaultCamera implements Camera { 		
	private final List<RenderableElement> renderables;
	private final List<ModelElement> scratch;
	private final MatrixWCStoSCS matrix;
	private final CanvasXY canvas;
	private final AxisXY axis;
	private Rectangle clipper; 
	private boolean isDebug;
	
	/**
	 * Creates a new default camera instance
	 * @param matrix the given {@link MatrixWCStoSCS matrix}
	 */
	public DefaultCamera( final MatrixWCStoSCS matrix ) {
		this.matrix			= matrix;
		this.canvas			= new CanvasXY();
		this.axis			= new AxisXY();
		this.scratch		= new ArrayList<ModelElement>( 1000 );
		this.renderables	= new ArrayList<RenderableElement>( 250 );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.Camera#getClippingPlane()
	 */
	public Rectangle getClippingPlane() {
		return clipper;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.Camera#init(javax.swing.JComponent)
	 */
	public void init( final JComponent drawingPane ) {			
		// get the dimensions of the drawing pane
		final Dimension dimension = drawingPane.getSize();
		
		// create the clipping plane
		clipper = new Rectangle( dimension );
		
		// set the center of the screen
		final double cx = dimension.getWidth() / 2.0d;
		final double cy = dimension.getHeight() / 2.0d;
		matrix.setOrigin( cx, cy );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.Camera#pan(double, double)
	 */
	public void pan( final double deltaX, final double deltaY ) {
		matrix.moveBy( deltaX, deltaY );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.Camera#moveTo(double, double)
	 */
	public void panTo( final double x, final double y ) {
		matrix.moveTo( x, y );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.Camera#render(constellation.functions.ApplicationController, constellation.model.GeometricModel, constellation.drawing.OperatingModes, java.awt.Image)
	 */
	public void render( final ApplicationController controller, 
						final GeometricModel model, 
						final Image surface ) {		
		synchronized( scratch ) {
			// get the system preferences
			final SystemPreferences preferences = controller.getSystemPreferences();
			
			// is the camera in debug mode?
			isDebug = CxSystemPreferences.getInstance().isDebugMode(); 
			
			// get the graphics context of the rendering surface
			final Graphics2D g = (Graphics2D)surface.getGraphics();
			
			// set some rendering hints
			if( preferences.isAntiAliasing() ) {
				g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			}
			
			// draw the canvas + grid
			drawRenderable( controller, model, canvas, g, preferences.getGridColor() );

			// draw the axis
			drawRenderable( controller, model, axis, g, preferences.getAxisColor() );
			
			// display the physical & phantom elements
			switch( controller.getSelectionMode() ) {
				case PHANTOM_ELEMENTS:
					// draw the "physical" elements (as inactive)
					model.getVisibleElements( scratch );
					drawElements( controller, model, scratch, g, preferences.getPhantomColor() );
				
					// draw the "phantom" elements (as active)
					model.getPhantomElements( scratch );
					drawElements( controller, model, scratch, g );	
					break;
					
				case PHYSICAL_ELEMENTS:			
					// draw the "phantom" elements (as active)
					if( preferences.showPhantoms() ) {
						model.getPhantomElements( scratch );
						drawElements( controller, model, scratch, g, preferences.getPhantomColor() );	
					}
				
					// draw the "physical" elements (as inactive)
					model.getVisibleElements( scratch );
					drawElements( controller, model, scratch, g );
					break;
			}
	
			// draw the "highlighted" elements
			if( controller.showHighlights() ) {
				final Collection<ModelElement> highlightedGeometry = model.getHighlightedGeometry();
				if( highlightedGeometry != null && !highlightedGeometry.isEmpty() ) {
					drawElements( controller, model, highlightedGeometry, g, preferences.getHighlightedGeometryColor() );
				}
			}
			
			// draw the "selected" elements
			renderables.clear();
			model.getSelectedGeometry( renderables );
			if( !renderables.isEmpty() ) {
				drawRenderables( controller, model, renderables, g, preferences.getSelectedGeometryColor() );
			}
			
			// draw the "temporary" elements
			final RenderableElement temporaryGeometry = model.getTemporaryElement();
			if( temporaryGeometry != null ) {
				drawRenderable( controller, model, temporaryGeometry, g, preferences.getTemporaryElementColor() );
			}
			
			// draw the "picked" elements
			final ModelElement pickedGeometry = model.getPickedElement();
			if( pickedGeometry != null ) {
				drawElement( controller, model, pickedGeometry, g, preferences.getPickedElementColor(), SOLID_STROKE );
			}
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.Camera#reset()
	 */
	public void reset() {
		matrix.reset();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.Camera#adjustRotation(double, double, double)
	 */
	public void adjustRotation( final double angleDX, final double angleDY ) {
		matrix.adjustRotation( angleDX, angleDY );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.Camera#setZoomFactor(double)
	 */
	public void setZoomFactor( double zoomFactor ) {
		matrix.setScale( zoomFactor );
	}
	
	/**
	 * Draws a single rendering entity
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param element the given {@link RenderableElement element}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param color the given {@link Color color}
	 */
	private void drawRenderable( final ApplicationController controller, 
							  	 final GeometricModel model, 
							  	 final RenderableElement element, 
							  	 final Graphics2D g, 
							  	 final Color color ) {
		element.render( controller, model, matrix, clipper, g, color );
	}
	
	/**
	 * Draws the collection of rendering entities
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param element the given {@link RenderableElement element}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param color the given {@link Color color}
	 */
	private void drawRenderables( final ApplicationController controller, 
							  	  final GeometricModel model, 
							  	  final Collection<RenderableElement> elements, 
							  	  final Graphics2D g, 
							  	  final Color color ) {
		for( final RenderableElement element : elements ) {
			element.render( controller, model, matrix, clipper, g, color );
		}
	}
	
	/**
	 * Draws a single model element
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param element the given {@link RenderableElement element}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param color the given {@link Color color}
	 * @param stroke the given {@link Stroke stroke}
	 */
	private void drawElement( final ApplicationController controller, 
							  final GeometricModel model, 
							  final ModelElement element, 
							  final Graphics2D g, 
							  final Color color, 
							  final Stroke stroke ) {
		// set the line stroke
		g.setStroke( stroke );
		
		// get the preferences instance
		final SystemPreferences preferences = controller.getSystemPreferences();
		
		// render the element
		switch( element.getType() ) {
			// draw a point?
			case POINT:
				final PointXY point = EntityRepresentationUtil.getPoint( element );
				if( preferences.showPointLabels() ) {
					point.renderLabel( controller, model, matrix, clipper, g, color, element.getLabel() );
				} else {
					point.render( controller, model, matrix, clipper, g, color );
				}
				break;
				
			// draw anything else
			default:
				// render the element
				element.render( controller, model, matrix, clipper, g, color );
				
				// if in debug mode, draw the boundary 
				if( isDebug ) {
					final RectangleXY rectM = element.getBounds( matrix );
					final Rectangle rectS = matrix.transform( rectM );
					if( clipper.intersects( rectS ) ) {
						g.setColor( Color.ORANGE );
						g.draw( rectS );
					}
				}
				break;
		}
	}

	/**
	 * Draws the set of elements in the specified color onto the screen
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param elements the set of {@link RenderableElement elements}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param color the color to render the geometry
	 */
	private void drawElements( final ApplicationController controller, 
							   final GeometricModel model, 
							   final Collection<ModelElement> elements, 
							   final Graphics2D g, 
							   final Color color ) {
		for( final ModelElement element : elements ) {
			// determine the element's stroke
			final Stroke stroke = EntityRepresentationUtil.getStroke( element );
			
			// draw the element
			drawElement( controller, model, element, g, color, stroke );
		}
	}

	/**
	 * Draws the set of elements onto the screen
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param elements the collection of {@link ModelElement elements}
	 * @param g the given {@link Graphics2D graphics context}
	 */
	private void drawElements( final ApplicationController controller, 
							   final GeometricModel model, 
							   final Collection<ModelElement> elements, 
							   final Graphics2D g ) {
		for( final ModelElement element : elements ) {
			// get the element's color
			final Color color = element.getColor();
			
			// determine the element's stroke
			final Stroke stroke = EntityRepresentationUtil.getStroke( element );

			// draw the element
			drawElement( controller, model, element, g, color, stroke );
		}
	}
	
}