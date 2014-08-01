package constellation.ui.components;

import java.awt.Cursor;


/**
 * This interface is to be implemented by all UI classes
 * wish to be installed in the information bar as a plug-in.
 * @author lawrence.daniels@gmnail.com
 */
@SuppressWarnings("serial")
public abstract class CxPlugIn extends CxPanel {
	
	/**
	 * Default Constructor
	 */
	public CxPlugIn() {
		super.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
	}
	
}
