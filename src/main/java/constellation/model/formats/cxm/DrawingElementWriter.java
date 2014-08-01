package constellation.model.formats.cxm;

import java.io.PrintWriter;

import constellation.drawing.EntityRepresentation;

/**
 * Represents a generic geometry writer
 * @author lawrence.daniels@gmail.com
 */
public interface DrawingElementWriter {
	
	/**
	 * Writes the given element to the given output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param element the given {@link EntityRepresentation drawing element}
	 */
	void write( PrintWriter out, EntityRepresentation element );

}
