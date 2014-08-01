package constellation.thirdparty.formats.iges.entities.x200;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES General Label
 * @author lawrence.daniels@gmail.com
 */
public class IGESGeneralLabel implements IGESEntity {
	private final Collection<IGESLeaderArrow> leaders;
	private final IGESGeneralNote note;
	
	/**
	 * Default constructor
	 */
	public IGESGeneralLabel( final IGESGeneralNote note ) {
		this.note	 = note;
		this.leaders = new LinkedList<IGESLeaderArrow>();
	}

	/** 
	 * Adds the given leader to the general label 
	 * @param leader the given {@link IGESLeaderArrow leader}
	 */
	public void add( final IGESLeaderArrow leader ) {
		leaders.add( leader );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.thirdparty.formats.iges.entities.IGESEntity#toGeometry()
	 */
	public ModelElement[] toDrawingElements() {
		// create a container for the geometry to return
		final Collection<ModelElement> drawingElements = new LinkedList<ModelElement>();
		
		// add the note geometry
		drawingElements.addAll( Arrays.asList( note.toDrawingElements() ) );
		
		// add the leaders
		for( final IGESLeaderArrow leader : leaders ) {
			drawingElements.addAll( Arrays.asList( leader.toDrawingElements() ) );
		}
		
		// return the entire set
		return drawingElements.toArray( new ModelElement[ drawingElements.size() ] );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "note = %s, leaders = %s", note, leaders );
	}

}
