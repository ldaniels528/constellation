package constellation.ui.components.buttons;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import constellation.CxContentManager;

/**
 * Constellation Accept Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class AcceptButton extends JButton {
	
	/**
	 * Default constructor
	 */
	public AcceptButton( final ActionListener listener ) {
		super( "Accept" );
		super.setIcon( CxContentManager.getInstance().getIcon( "images/dialog/buttons/accept.gif" ) );
		
		if( listener != null ) {
			super.addActionListener( listener );
		}
	}

}