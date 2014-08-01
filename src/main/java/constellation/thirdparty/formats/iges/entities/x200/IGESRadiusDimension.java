package constellation.thirdparty.formats.iges.entities.x200;

import static constellation.drawing.LinePatterns.PATTERN_PHANTOM;
import static java.awt.Color.BLUE;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.PointXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/** 
 * <h2>Radius Dimension Entity (Type 222)</h2>
 * <p>A Radius Dimension Entity consists of a general note, a leader, and an arc center point, (XT, YT).
 * Refer to Figure 68 for examples of radius dimensions. A second form of this entity accounts for the
 * occasional need to have two Leader (Arrow) Entities referenced. The definition of this second form
 * can be found in Appendix G (see Section G.20).</p>
 * 
 * <p>The arc center coordinates are used as reference in constructing the radius dimension but have no
 * effect on the dimension components.</p>
 * 
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1           DENOTE      Pointer   Pointer to the DE of the General Note Entity
 * 2           DEARRW      Pointer   Pointer to the DE of the first Leader Entity
 * 3           XT          Real      ArcXY center coordinates
 * 4           YT          Real
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGESRadiusDimension implements IGESEntity {
	private final IGESGeneralNote note;
	private final IGESLeaderArrow arrow;
	private final double xt;
	private final double yt;
	
	/**
	 * Creates a new radius dimension
	 * @param note the given {@link IGESGeneralNote note}
	 * @param arrow the given {@link IGESLeaderArrow leader arrow}
	 * @param xt the given X-coordinate of the center point of the arc
	 * @param yt the given Y-coordinate of the center point of the arc
	 */
	public IGESRadiusDimension( final IGESGeneralNote note, 
								final IGESLeaderArrow arrow,
								final double xt,
								final double yt ) {
		this.note	= note;
		this.arrow	= arrow;
		this.xt		= xt;
		this.yt		= yt;
	}
	 
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.formats.iges.entities.IGESEntity#toGeometry()
	 */
	public ModelElement[] toDrawingElements() {
		// create a geometry container
		final Collection<ModelElement> elements = new LinkedList<ModelElement>();
		
		// attach the center point
		final ModelElement point = new CxModelElement( new PointXY( xt, yt ) );
		point.setColor( BLUE );
		elements.add( point );
		
		// attach a circle to indicate the radius
		// TODO remove soon
		double radius = 2.85;
		final ModelElement circle = new CxModelElement( new CircleXY( xt, yt, radius ) );
		circle.setColor( BLUE );
		circle.setPattern( PATTERN_PHANTOM );
		
		// return the geometry
		return elements.toArray( new ModelElement[ elements.size() ] );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "note = %s, arrow = %s, XT = %3.2f, YT = %3.2f", note, arrow, xt, yt );
	}

}