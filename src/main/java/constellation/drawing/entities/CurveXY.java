package constellation.drawing.entities;

import static java.lang.String.format;
import constellation.drawing.ComplexInternalRepresentation;
import constellation.drawing.EntityCategoryTypes;

/**
 * The mathematical representation of a curve
 * <div><a href="http://en.wikipedia.org/wiki/List_of_curves">Reference</a></div>
 * @author lawrence.daniels@gmail.com
 */
public abstract class CurveXY implements ComplexInternalRepresentation {
	
	/**
	 * Default constructor
	 */
	protected CurveXY() {
		super();
	} 
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} 
		catch( final CloneNotSupportedException cause ) {
			throw new IllegalStateException( format( "Error cloning class '%s'", getClass().getName() ), cause );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.CURVE;
	}
	
	/**
	 * Returns a new parallel curve using the given point as the offset. 
	 * @param offset the given offset {@link PointXY point}
	 * @return the parallel {@link CurveXY curve}
	 */
	public abstract CurveXY getParallelCurve( PointXY offset );

}
