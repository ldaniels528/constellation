package constellation.ui.components.fields;

import javax.swing.JTextField;

import constellation.util.StringUtil;

/**
 * Constellation String Field
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxStringField extends JTextField {
	private static final String BLANK = "";

	/**
	 * Default constructor
	 */
	public CxStringField( final int length ) {
		super( length );
	}
	
	/**
	 * Default constructor
	 */
	public CxStringField( final int length, final boolean editable ) {
		super( length );
		super.setEditable( editable );
	}
	
	/**
	 * Creates a new integer input field
	 * @param value the given integer value
	 */
	public CxStringField( final int length, final String value ) {
		super( length );
		super.setText( value );
	}
	
	/**
	 * Determines whether the given string is blank (<tt>null</tt> or empty)
	 * @return true, if the string is <tt>null</tt> or empty
	 */
	public boolean isBlank() {
		return StringUtil.isBlank( super.getText() );
	}
	
	/**
	 * Clears the contents of the text field
	 */
	public void reset() {
		super.setText( BLANK );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	public void setText( final String text ) {
		super.setText( ( text != null ) ? text : BLANK );
	}
	
	public void setText( final int value ) {
		super.setText( String.valueOf( value ) );
	}
	
}