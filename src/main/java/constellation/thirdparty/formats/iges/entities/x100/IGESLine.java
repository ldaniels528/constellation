package constellation.thirdparty.formats.iges.entities.x100;

import static constellation.thirdparty.formats.iges.IGESConstants.IGES_COLORS;
import constellation.drawing.LinePatterns;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.LineXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES Line Entity
 * @author lawrence.daniels@gmail.com
 */
public class IGESLine implements IGESEntity {
	private final int colorNumber;
	private final int lineFontPattern;
	private final double x1;
	private final double y1;
	private final double z1;
	private final double x2;
	private final double y2;
	private final double z2;
	
	/**
	 * Creates a new Line
	 * @param x1 the x-coordinate of line #1
	 * @param y1 the y-coordinate of line #1
	 * @param z1 the z-coordinate of line #1
	 * @param x2 the x-coordinate of line #2
	 * @param y2 the y-coordinate of line #2
	 * @param z2 the z-coordinate of line #2
	 * @param colorNumber the color of the line
	 * @param lineFontPattern the font/pattern of the line
	 */
	public IGESLine( final double x1, 
					 final double y1, 
					 final double z1, 
					 final double x2,
					 final double y2, 
					 final double z2, 
					 final int colorNumber, 
					 final int lineFontPattern) {
		super();
		this.colorNumber = colorNumber;
		this.lineFontPattern = lineFontPattern;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {		
		// create the line
		final ModelElement line = new CxModelElement( new LineXY( x1, y1, x2, y2 ) );
		line.setColor( IGES_COLORS[ colorNumber ] );
		line.setPattern( LinePatterns.values()[ lineFontPattern ] );
		return new ModelElement[] { line };
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format( "(%.2f,%.2f,%.2f)-(%.2f,%.2f,%.2f)", x1, y1, z1, x2, y2, z2 );
	}

}
