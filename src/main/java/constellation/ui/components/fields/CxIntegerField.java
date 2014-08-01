package constellation.ui.components.fields;

import javax.swing.JTextField;

/**
 * Constellation Integer Field
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxIntegerField extends JTextField {

	/**
	 * Default constructor
	 */
	public CxIntegerField() {
		super( 8 );
	}
	
	/**
	 * Creates a new integer input field
	 * @param value the given integer value
	 */
	public CxIntegerField( final int value ) {
		this();
		setInteger( value );
	}
	
	/**
	 * Returns the integer value contained within 
	 * the text field
	 * @return the integer value
	 */
	public int getInteger() {
		return Integer.parseInt( super.getText() );
	}
	
	/**
	 * Sets the integer value
	 * @param value the given integer value
	 */
	public void setInteger( final int value ) {
		super.setText( String.valueOf( value ) );
	}
	
	/**
	 * Clears the contents of the text field
	 */
	public void reset() {
		super.setText( "" );
	}
	
}