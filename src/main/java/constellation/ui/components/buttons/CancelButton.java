package constellation.ui.components.buttons;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import constellation.CxContentManager;

/**
 * Constellation Cancel Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CancelButton extends JButton {
	
	/**
	 * Default constructor
	 */
	public CancelButton( final ActionListener listener ) {
		super( "Cancel" );
		super.setIcon( CxContentManager.getInstance().getIcon( "images/dialog/buttons/cancel.png" ) );
		super.addActionListener( listener );
	}

}