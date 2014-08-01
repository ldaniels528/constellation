package constellation.ui.components.fields;

import javax.swing.JTextField;

import constellation.CxFontManager;
import constellation.util.StringUtil;

/**
 * Constellation Element Name/ID Field
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxIDField extends JTextField {
	
	/**
	 * Default constructor
	 */
	public CxIDField() {
		super( 10 );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}
	
	/**
	 * Indicates whether the field is blank
	 * @return true, if field is blank
	 */
	public boolean isBlank() {
		return StringUtil.isBlank( getText() );
	}
	
	/**
	 * Clears the contents of the text field
	 */
	public void reset() {
		super.setText( "" );
	}

}
