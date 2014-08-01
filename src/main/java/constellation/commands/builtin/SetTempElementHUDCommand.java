package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.SET_TEMP_HUD;
import static constellation.commands.CxCommandManager.encodeHUD;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.drawing.entities.HUDXY;
import constellation.model.GeometricModel;

/** 
 * Set Temporary Element HUD Command
 * @see constellation.drawing.entities.HUDXY
 * @author lawrence.daniels@gmail.com
 */
public class SetTempElementHUDCommand extends AbstractCommand {
	private HUDXY hud;

	/**
	 * Creates a new "Set Temporary Element HUD" command
	 * @param hud the given {@link HUDXY HUD}
	 */
	SetTempElementHUDCommand( final HUDXY hud ) {
		this.hud = hud;
	}

	/** 
	 * Creates a new "Set Temporary Element HUD" command
	 * @param hud the given {@link HUDXY HUD}
	 * @return the {@link SetTempElementHUDCommand command}
	 */
	public static SetTempElementHUDCommand create( final HUDXY hud ) {
		return new SetTempElementHUDCommand( hud );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// write the opCode
		stream.writeInt( SET_TEMP_HUD );
		
		// encode the HUD
		encodeHUD( stream, hud );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.setTemporaryElement( hud );
	}
	
	/**
	 * Returns the HUD instance
	 * @return the {@link HUDXY HUD}
	 */
	public HUDXY getHUD() {
		return hud;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SET TEMP [HUD]", SET_TEMP_HUD );
	}

}
