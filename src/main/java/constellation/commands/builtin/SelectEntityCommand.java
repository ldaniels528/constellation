package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.SEL_ENTITY;
import static constellation.commands.CxCommandManager.encodeRepresentation;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.EntityRepresentation;
import constellation.model.GeometricModel;

/** 
 * "Select Entity" Command
 * @author lawrence.daniels@gmail.com
 */
public class SelectEntityCommand extends AbstractCommand {
	private final EntityRepresentation entity;
	
	/**
	 * Creates a new "Select Entity" command
	 * @param entity the given {@link EntityRepresentation entity}
	 */
	SelectEntityCommand( final EntityRepresentation entity ) {
		this.entity = entity;
	}
	
	/**
	 * Creates a new "Select Entity" command
	 * @param entity the given {@link EntityRepresentation entity}
	 * @return a new {@link SelectEntityCommand "Select Entity" command}
	 */
	public static SelectEntityCommand create( final EntityRepresentation entity ) {
		return new SelectEntityCommand( entity );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// write the OpCode
		stream.writeInt( SEL_ENTITY );
		
		// append the label
		encodeRepresentation( stream, entity );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.selectGeometry( entity );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SELECT ENTITY '%s'", SEL_ENTITY, entity.getClass().getSimpleName() );
	}

}
