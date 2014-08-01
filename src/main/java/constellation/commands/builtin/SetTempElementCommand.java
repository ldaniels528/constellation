package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.SET_TEMP;
import static constellation.commands.CxCommandManager.encodeRepresentation;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.EntityRepresentation;
import constellation.drawing.RenderableElement;
import constellation.model.GeometricModel;

/** 
 * Set Temporary Element Command
 * @author lawrence.daniels@gmail.com
 */
public class SetTempElementCommand extends AbstractCommand {
	private RenderableElement temporaryElement;

	/**
	 * Creates a new "Set Temporary Element" command
	 * @param temporaryElement the given {@link RenderableElement temporary element}
	 */
	SetTempElementCommand( final RenderableElement temporaryElement ) {
		this.temporaryElement = temporaryElement;
	}

	/** 
	 * Creates a new "Set Temporary Element" command
	 * @param temporaryElement the given {@link RenderableElement temporary element}
	 * @return the {@link SetTempElementCommand command}
	 */
	public static SetTempElementCommand create( final RenderableElement temporaryElement ) {
		return new SetTempElementCommand( temporaryElement );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// is the element a geometric representation?
		if( temporaryElement instanceof EntityRepresentation ) {
			// write the opCode
			stream.writeInt( SET_TEMP );
			
			// write the representation
			final EntityRepresentation rep = (EntityRepresentation)temporaryElement;
			encodeRepresentation( stream, rep );
		}
					
		// not sure what it is!
		else {	
			throw new IllegalArgumentException( format( "Unhandled type '%s'", 
					( temporaryElement != null ) ? temporaryElement.getClass().getSimpleName() : "???" ) );
		}
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.setTemporaryElement( temporaryElement );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SET TEMP '%s'", SET_TEMP, temporaryElement.getClass().getSimpleName() );
	}

}
