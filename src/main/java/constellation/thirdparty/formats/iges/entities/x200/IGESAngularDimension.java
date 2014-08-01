package constellation.thirdparty.formats.iges.entities.x200;

import static constellation.drawing.LinePatterns.PATTERN_PHANTOM;
import static java.awt.Color.BLUE;
import static java.lang.String.format;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.PointXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;

/**
 * Represents a Angular Dimension
 * @author lawrence.daniels@gmail.com
 */
public class IGESAngularDimension implements IGESEntity {
	private final IGESGeneralNote note;
	private final IGESCopiousData witness1; 
	private final IGESCopiousData witness2;
	private final double xt;
	private final double yt;
	private final double radius;
	private final IGESLeaderArrow arrow1; 
	private final IGESLeaderArrow arrow2;	

	/**
	 * Creates a new angular dimension
	 * @param note the given {@link IGESGeneralNote note}
	 * @param witness1 the given {@link IGESCopiousData witness line #1}
	 * @param witness2 the given {@link IGESCopiousData witness line #2}
	 * @param xt the given X-coordinate of the vertex point
	 * @param yt the given Y-coordinate of the vertex point
	 * @param radius the given radius
	 * @param arrow1 the given {@link IGESLeaderArrow leader arrow #1}
	 * @param arrow2 the given {@link IGESLeaderArrow leader arrow #2}
	 */
	public IGESAngularDimension( final IGESGeneralNote note, 
								 final IGESCopiousData witness1, 
								 final IGESCopiousData witness2, 
								 final double xt,
								 final double yt,
								 final double radius,
								 final IGESLeaderArrow arrow1,
								 final IGESLeaderArrow arrow2 ) {
		this.note		= note;
		this.witness1	= witness1;
		this.witness2	= witness2;
		this.xt			= xt;
		this.yt			= yt;
		this.radius		= radius;
		this.arrow1		= arrow1;
		this.arrow2		= arrow2;
	}
	 
	/**
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		// create a geometry container
		final Collection<ModelElement> drawingElements = new LinkedList<ModelElement>();
		
		// attach witness lines 1
		if( witness1 != null ) {
			drawingElements.addAll( witness1.toWitnessLines() );
		}
		
		// attach witness lines 2 
		if( witness2 != null ) {
			drawingElements.addAll( witness2.toWitnessLines() );
		}
		
		// attach the vertex point
		final ModelElement vertex = new CxModelElement( new PointXY( xt, yt ) );
		vertex.setColor( BLUE );
		drawingElements.add( vertex );
		
		// attach a circle to indicate the radius
		// TODO remove soon
		final ModelElement circle = new CxModelElement( new CircleXY( xt, yt, radius ) );
		circle.setColor( BLUE );
		circle.setPattern( PATTERN_PHANTOM );
		
		// return the geometry
		return drawingElements.toArray( new ModelElement[ drawingElements.size() ] );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "note = %s, witness1 = %s, witness2 = %s, XT=%3.2f, YT=%3.2f, R=%3.2f, arrow1=%s, arrow2=%s", 
						note, witness1, witness2, xt, yt, radius, arrow1, arrow2 );
	}

}