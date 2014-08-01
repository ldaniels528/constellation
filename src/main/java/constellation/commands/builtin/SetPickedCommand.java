package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.SET_PICK;
import static constellation.commands.CxCommandManager.encodeString;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.elements.ModelElement;
import constellation.model.GeometricModel;

/** 
 * Set Picked Element Command
 * @author lawrence.daniels@gmail.com
 */
public class SetPickedCommand extends AbstractCommand {
	private final String label;
	
	/**
	 * Creates a new "Set Picked Element" Command 
	 * @param label the given element label
	 */
	SetPickedCommand( final String label ) {
		this.label = label;
	}
	
	/**
	 * Creates a new "Set Picked Element" Command 
	 * @param label the given element label
	 * @return the {@link SetPickedCommand command}
	 */
	public static SetPickedCommand create( final ModelElement element ) {
		return new SetPickedCommand( element.getLabel() );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// write the opCode
		stream.writeInt( SET_PICK );
		
		// encode the label
		encodeString( stream, label );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		// pick the element by label
		final ModelElement element = model.lookupElementByLabel( label );
		if( element != null ) {
			model.setPickedElement( element );
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SET PICK '%s'", SET_PICK, label );
	}

}
