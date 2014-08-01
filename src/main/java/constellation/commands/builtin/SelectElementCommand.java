package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.SEL_ELEM;
import static constellation.commands.CxCommandManager.encodeString;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.elements.ModelElement;
import constellation.model.GeometricModel;

/** 
 * "Select Element" Command
 * @author lawrence.daniels@gmail.com
 */
public class SelectElementCommand extends AbstractCommand {
	private final String label;

	/**
	 * Creates a new "Select Element" command
	 * @param label the given element label
	 */
	SelectElementCommand( final String label ) {
		this.label = label;
	}
	
	/**
	 * Creates a new "Select Element" command
	 * @param element the given {@link ModelElement element}
	 */
	public static SelectElementCommand create( final ModelElement element ) {
		return new SelectElementCommand( element.getLabel() );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// write the OpCode
		stream.writeInt( SEL_ELEM );
		
		// append the label
		encodeString( stream, label );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		// select the element by label
		final ModelElement element = model.lookupElementByLabel( label );
		if( element != null ) {
			model.selectGeometry( element );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SELECT ELEM '%s'", SEL_ELEM, label );
	}

}
