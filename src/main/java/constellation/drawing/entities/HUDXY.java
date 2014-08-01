package constellation.drawing.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents the Heads Up Display (HUD)
 * @author lawrence.daniels@gmail.com
 */
public class HUDXY implements RenderableElement {
	private static final int RADIUS = 20;
	private static final Color DARK_BLUE = new Color( 0x0000A0 );
	private final LinkedList<String> lines;
	private final LinkedList<RenderableElement> elements;
	
	/**
	 * Default Constructor
	 */
	public HUDXY() {
		this.lines 		= new LinkedList<String>();
		this.elements	= new LinkedList<RenderableElement>();
	}
	
	/**
	 * Default Constructor
	 */
	public HUDXY( final RenderableElement ... elements ) {
		this();
		this.elements.addAll( Arrays.asList( elements ) );
	}
	
	//////////////////////////////////////////////////////////////////////
	//		Renderable Elements
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds a new renderable element to the HUD
	 * @param element the given renderable element
	 */
	public void add( final RenderableElement element ) {
		elements.add( element );
	}
	
	/**
	 * Adds an array of renderable element to the HUD
	 * @param elementArray the given array {@link RenderableElement renderable elements}
	 */
	public void addAll( final RenderableElement ... elementArray ) {
		elements.addAll( Arrays.asList( elementArray ) );
	}
	
	/**
	 * Adds an array of renderable element to the HUD
	 * @param elementCol the given collection {@link RenderableElement renderable elements}
	 */
	public void addAll( final Collection<RenderableElement> elementCol ) {
		elements.addAll( elementCol );
	}
	
	/**
	 * Returns the collection of elements contained within the HUD
	 * @return the collection of {@link RenderableElement elements}
	 */
	public Collection<RenderableElement> getElements() {
		return elements;
	}
	
	//////////////////////////////////////////////////////////////////////
	//		Line Items
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds an array of line items to the HUD
	 * @param lineItems the given array line items
	 */
	public void append( final String ... lineItems ) {
		// if just one element, access it via its index
		if( lineItems.length == 1 ) {
			lines.add( lineItems[0] );
		}
		
		// otherwise, turn it into an array
		else {
			lines.addAll( Arrays.asList( lineItems ) );
		}
	}
	
	/**
	 * Adds an array of line items to the HUD
	 * @param lineItems the given collection line items
	 */
	public void append( final Collection<String> lineItems ) {
		lines.addAll( lineItems );
	}
	
	/**
	 * Appends a line item separator to the HUD
	 */
	public void appendSeparator() {
		lines.add( "" );
	}
	
	/**
	 * Returns the collection of text string
	 * @return the collection of text string
	 */
	public Collection<String> getLines() {
		return lines;
	}
	
	//////////////////////////////////////////////////////////////////////
	//		Rendering Methods
	//////////////////////////////////////////////////////////////////////

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
		// draw the rendering element
		g.setStroke( EntityRepresentationUtil.getStroke( LinePatterns.PATTERN_SOLID ) );
		g.setColor( color );
		for( final RenderableElement element : elements ) {
			element.render( controller, model, matrix, clipper, g, color );
		}
		
		// has HUD text been specified?
		if( !lines.isEmpty() ) {
			// draw the background
			final int width = 200;
			final int height = lines.size() * 20 + 15;
			g.setColor( Color.WHITE );
			g.fillRoundRect( 15, 10, width, height, RADIUS, RADIUS );
			
			// draw the outline
			g.setStroke( EntityRepresentationUtil.getStroke( LinePatterns.PATTERN_DASHED ) );
			g.setColor( DARK_BLUE );
			g.drawRoundRect( 15, 10, width, height, RADIUS, RADIUS );
			
			// draw the HUD line items
			int x = 30;
			int y = 30;
			g.setColor( DARK_BLUE );
			for( final String line : lines ) {
				g.drawString( line, x, y );
				
				y += 20;
			}
		}
	}

}
