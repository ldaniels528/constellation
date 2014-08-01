package constellation.thirdparty.formats.iges.entities.x200;

import static java.awt.Color.BLUE;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES General Note
 * @author lawrence.daniels@gmail.com
 */
public class IGESGeneralNote implements IGESEntity {
	private final Collection<IGESNote> notes;
	
	/**
	 * Default constructor
	 */
	public IGESGeneralNote() {
		this.notes = new LinkedList<IGESNote>();
	}
	
	/**
	 * Adds a note to the notes collection
	 * @param x the x-coordinate of the position of the note
	 * @param y the y-coordinate of the position of the note
	 * @param z the z-coordinate of the position of the note
	 * @param textString the text string of the note
	 */
	public void addNote( final double x, 
						 final double y, 
						 final double z, 
						 final String textString ) {
		notes.add( new IGESNote( x, y, z, textString ) );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.thirdparty.formats.iges.entities.IGESEntity#toGeometry()
	 */
	public ModelElement[] toDrawingElements() {
		final ModelElement[] elements = new ModelElement[ notes.size() ];
		int n = 0;
		for( IGESNote note : notes ) {
			elements[n++] = note.toDrawingElement();
		}
		return elements;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return notes.toString();
	}
	
	/**
	 * IGES Note
	 * @author lawrence.daniels@gmail.com
	 */
	private static class IGESNote {
		private final String textString;
		private final double x;
		private final double y;
		private final double z;
		
		/**
		 * Creates a new note 
		 * @param x the x-coordinate of the position of the note
		 * @param y the y-coordinate of the position of the note
		 * @param z the z-coordinate of the position of the note
		 * @param textString the text string of the note
		 */
		public IGESNote( final double x, 
					 	 final double y, 
					 	 final double z, 
					 	 final String textString ) {
			this.x 			= x;
			this.y 			= y;
			this.z 			= z;
			this.textString = textString;
		}
		
		/**
		 * Returns the drawing element represented by the host IGES entity
		 * @return the {@link ModelElement element}
		 */
		public ModelElement toDrawingElement() {
			final ModelElement note = new CxModelElement( new TextNoteXY( new PointXY( x, y ), textString ) );
			note.setColor( BLUE );
			return note;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return String.format( "'%s' at (%.2f,%.2f,%.2f)", textString, x, y, z );
		}
	}

}
