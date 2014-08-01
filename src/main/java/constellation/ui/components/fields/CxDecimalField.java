package constellation.ui.components.fields;

import javax.swing.JTextField;

/**
 * Constellation Decimal Field
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxDecimalField extends JTextField {

	/**
	 * Default constructor
	 */
	public CxDecimalField() {
		this( 8 );
	}
	
	/**
	 * Default constructor
	 */
	public CxDecimalField( final double initialValue ) {
		this( 8 );
		this.setDecimal( initialValue );
	}
	
	/**
	 * Default constructor
	 */
	public CxDecimalField( final int length ) {
		super( length );
	}
	
	/**
	 * Returns the decimal value contained within 
	 * the text field
	 * @return the decimal value
	 */
	public Double getDecimal() {		
		return getDecimal( null );
	}
	
	/**
	 * Returns the decimal value contained within 
	 * the text field
	 * @return the decimal value
	 */
	public Double getDecimal( final Double defaultValue ) {		
		try {
			return Double.parseDouble( super.getText().trim() );
		}
		catch( final NumberFormatException e ) {
			return defaultValue;
		}
	}
	
	/**
	 * Sets the decimal value
	 * @param value the given decimal value
	 */
	public CxDecimalField setDecimal( final Double value ) {
		super.setText( ( value != null ) ? String.format( "%5.4f", value ) : "" );
		return this;
	}
	
	/**
	 * Clears the contents of the text field
	 */
	public void reset() {
		super.setText( "0.0" );
	}
	
}
