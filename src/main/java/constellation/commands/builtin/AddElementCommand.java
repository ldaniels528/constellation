package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.ADD_ELEM;
import static constellation.commands.CxCommandManager.encodeColor;
import static constellation.commands.CxCommandManager.encodeRepresentation;
import static constellation.commands.CxCommandManager.encodeString;
import static java.lang.String.format;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.LinePatterns;
import constellation.drawing.elements.ModelElement;
import constellation.model.GeometricModel;

/**
 * Add Model Element Command
 * @author lawrence.daniels@gmail.com
 */
public class AddElementCommand extends AbstractCommand {
	private final EntityRepresentation entity;
	private final String label;
	private final Color color;
	private final int pattern;
	private final int layer;
	
	/**
	 * Default constructor
	 */
	AddElementCommand( final String label,
					   final Color color,
					   final int pattern,
					   final int layer,
					   final EntityRepresentation entity ) {
		this.label		= label;
		this.color		= color;
		this.pattern	= pattern;
		this.layer		= layer;
		this.entity		= entity;
	}
	
	/** 
	 * Creates a new "Add Model Element" command
	 * @param element the given {@link ModelElement model element}
	 * @return the new {@link AddElementCommand command}
	 */
	public static AddElementCommand create( final ModelElement element ) {
		// get the other data elements
		final String label	= element.getLabel();
		final Color color	= element.getColor();
		final int pattern	= element.getPattern().ordinal();
		final int layer		= element.getLayer();
		
		// return the command
		return new AddElementCommand( label, color, pattern, layer, element.getRepresentation() );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream buf ) 
	throws IOException {
		// write the command header
		buf.writeInt( ADD_ELEM );						
		encodeString( buf, label );
		encodeColor( buf, color );
		buf.writeByte( pattern );
		buf.writeByte( layer );
		
		// now write the representation
		encodeRepresentation( buf, entity );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {		
		// determine the appropriate element type
		final ModelElement element = EntityRepresentationUtil.toDrawingElement( entity );
		element.setLabel( label ); 
		element.setColor( color );
		element.setPattern( LinePatterns.values()[ pattern ] );
		element.setLayer( layer );
		
		// add the drawing element to the model
		model.addPhysicalElement( element );
	}
	
	/**
	 * Returns the entity representation
	 * @return the {@link EntityRepresentation entity representation}
	 */
	public EntityRepresentation getRepresentation() {
		return entity;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return format( "[%04X] ADD %s '%s' COLOR '#%06X' STYLE '%s' LAYER 'L%03d'", 
				ADD_ELEM, 
				entity.getType(), 
				label, 
				color.getRGB(), 
				LinePatterns.values()[ pattern ],
				layer );
	}
	
}
