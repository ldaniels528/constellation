package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.DEL_ELEM;
import static constellation.commands.CxCommandManager.encodeString;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.elements.ModelElement;
import constellation.model.GeometricModel;

/**
 * Delete Element Command
 * @author lawrence.daniels@gmail.com
 */
public class DeleteElementCommand extends AbstractCommand {
	private final String label;
	
	/**
	 * Default constructor
	 */
	DeleteElementCommand( final String label ) {
		this.label = label;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.navigationbar.sharing.ops.Operation#createOperation(java.lang.Object)
	 */
	public static DeleteElementCommand create( final ModelElement geometry ) {
		// extract the attributes
		final String label = geometry.getLabel();
		
		// return the command
		return new DeleteElementCommand( label );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.pdm.server.io.PDMCommand#encode()
	 */
	public void encode( final DataOutputStream buf ) 
	throws IOException {
		// write the OpCode
		buf.writeInt( DEL_ELEM );
		
		// append the label
		encodeString( buf, label );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.app.navigationbar.sharing.ops.AbstractOperation#evaluate(constellation.geometry.model.CxModel, java.util.List)
	 */
	public void evaluate( final GeometricModel model ) {
		// delete the element from the model
		final ModelElement element = model.lookupElementByLabel( label );
		if( element != null ) {
			model.erase( element );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.model.collaboration.objects.AbstractOperation#toString()
	 */
	public String toString() {
		return format( "[%04X] DEL_ELEM '%s'", DEL_ELEM, label );
	}
	
}
