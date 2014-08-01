package constellation.thirdparty.formats.iges.entities.x200;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;

/**
 * Represents a Linear Dimension
 * @author lawrence.daniels@gmail.com
 */
public class IGESLinearDimension implements IGESEntity {
	private final IGESGeneralNote note;
	private final IGESLeaderArrow arrow1; 
	private final IGESLeaderArrow arrow2;
	private final IGESCopiousData witness1; 
	private final IGESCopiousData witness2;

	/**
	 * Creates a new linear dimension
	 * @param note the given {@link IGESGeneralNote note}
	 * @param arrow1 the given {@link IGESLeaderArrow leader arrow #1}
	 * @param arrow2 the given {@link IGESLeaderArrow leader arrow #2}
	 * @param witness1 the given {@link IGESCopiousData witness line #1}
	 * @param witness2 the given {@link IGESCopiousData witness line #2}
	 */
	public IGESLinearDimension( final IGESGeneralNote note, 
								final IGESLeaderArrow arrow1, 
								final IGESLeaderArrow arrow2, 
								final IGESCopiousData witness1, 
								final IGESCopiousData witness2 ) {
		this.note		= note;
		this.arrow1		= arrow1;
		this.arrow2		= arrow2;
		this.witness1	= witness1;
		this.witness2	= witness2;
	}
	 
	/* 
	 * (non-Javadoc)
	 * @see constellation.thirdparty.formats.iges.entities.IGESEntity#toGeometry()
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
		
		// return the geometry
		return drawingElements.toArray( new ModelElement[ drawingElements.size() ] );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "note = %s, arrow1 = %s, arrow2 = %s, witness1 = %s, witness2 = %s", note, arrow1, arrow2, witness1, witness2 );
	}

}
