package constellation.drawing;

import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_MITER;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * Constellation Line Pattern Definitions
 * @author lawrence.daniels@gmail.com
 */
public class LinePatternDefs {
	
	public static final Stroke SOLID_STROKE  = 
		new BasicStroke( 1.0f );
	
	public static final Stroke CENTER_LINE_STROKE = 
 		new BasicStroke( 1.0f, CAP_SQUARE, JOIN_MITER, 1.0f, new float[] { 8.0f, 4.0f }, 5.0f );
	
 	public static final Stroke DASHED_STROKE = 
 		new BasicStroke( 1.0f, CAP_SQUARE, JOIN_MITER, 1.0f, new float[] { 8.0f, 4.0f }, 5.0f );
 	
 	public static final Stroke DOTTED_STROKE = 
 		new BasicStroke( 1.0f, CAP_SQUARE, JOIN_MITER, 1.0f, new float[] { 8.0f, 4.0f }, 5.0f );
 	
 	public static final Stroke PHANTOM_STROKE = 
 		new BasicStroke( 1.0f, CAP_SQUARE, JOIN_MITER, 1.0f, new float[] { 8.0f, 4.0f }, 5.0f );


}
