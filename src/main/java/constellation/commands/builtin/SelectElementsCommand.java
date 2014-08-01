package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import constellation.drawing.elements.ModelElement;
import constellation.model.GeometricModel;

/** 
 * "Select Elements" Command
 * @author lawrence.daniels@gmail.com
 */
public class SelectElementsCommand extends AbstractCommand {
	private final String[] labels;
	
	/**
	 * Creates a new "Select Elements" command
	 * @param labels the given element labels
	 */
	SelectElementsCommand( final String[] labels ) {
		this.labels = labels;
	}
	
	/**
	 * Creates a new "Select Elements" command
	 * @param elements the given array of {@link ModelElement elements}
	 * @return a new {@link SelectElementsCommand "Select Elements" Command}
	 */
	public static SelectElementsCommand create( final ModelElement[] elements ) {
		// create the array of element labels
		final String[] labels = new String[ elements.length ];
		int n = 0;
		for( final ModelElement element : elements ) {
			labels[n++] = element.getLabel();
		}
		
		// return the command
		return new SelectElementsCommand( labels );
	}
	
	/**
	 * Creates a new "Select Elements" command
	 * @param elements the given collection of {@link ModelElement elements}
	 * @return a new {@link SelectElementsCommand "Select Elements" Command}
	 */
	public static SelectElementsCommand create( final Collection<ModelElement> elements ) {
		// create the array of element labels
		final String[] labels = new String[ elements.size() ];
		int n = 0;
		for( final ModelElement element : elements ) {
			labels[n++] = element.getLabel();
		}
		
		// return the command
		return new SelectElementsCommand( labels );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// write the OpCode
		stream.writeInt( SEL_ELEMS );
	
		// write the element count
		stream.writeShort( labels.length );
		
		// write the element labels
		for( final String label : labels ) {
			encodeString( stream, label );
		}
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		for( final String label : labels ) {
			// select each element by label
			final ModelElement element = model.lookupElementByLabel( label );
			if( element != null ) {
				model.selectGeometry( element );
			}
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SELECT ELEMS [%d]", SEL_ELEMS, labels.length );
	}

}
