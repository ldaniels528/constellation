package constellation.thirdparty.formats.iges.entities.x200;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;

/**
 * Represents a Ordinate Dimension
 * @author lawrence.daniels@gmail.com
 */
public class IGESOrdinateDimension implements IGESEntity {
	private final IGESGeneralNote note;
	private final IGESCopiousData witness;
	
	/**
	 * Creates a new ordinate dimension
	 * @param note the given {@link IGESGeneralNote note}
	 * @param witness the given {@link IGESCopiousData witness line}
	 */
	public IGESOrdinateDimension( final IGESGeneralNote note, 
								  final IGESCopiousData witness ) {
		this.note		= note;
		this.witness	= witness;
	}
	 
	/* 
	 * (non-Javadoc)
	 * @see constellation.thirdparty.formats.iges.entities.IGESEntity#toGeometry()
	 */
	public ModelElement[] toDrawingElements() {
		// create a geometry container
		final Collection<ModelElement> elements = new LinkedList<ModelElement>();
		
		// attach the witness line
		if( witness != null ) {
			elements.addAll( witness.toWitnessLines() );
		}
		
		// return the geometry
		return elements.toArray( new ModelElement[ elements.size() ] );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "note = %s, witness = %s", note, witness );
	}

}
