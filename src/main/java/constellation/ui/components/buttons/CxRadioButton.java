package constellation.ui.components.buttons;

import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import constellation.CxFontManager;

/**
 * Constellation Radio Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxRadioButton extends JRadioButton {
	
	/**
	 * Creates a new radio button with a text label
	 * @param label the given text label
	 * @param listener the given {@link ActionListener listener}
	 */
	public CxRadioButton( final String label, final ActionListener listener ) {
		super( label );
		super.addActionListener( listener );
		
		// use the default font
		CxFontManager.setDefaultFont( this );
	}

}
