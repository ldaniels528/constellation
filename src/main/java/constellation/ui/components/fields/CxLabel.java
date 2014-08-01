package constellation.ui.components.fields;

import javax.swing.JLabel;

import constellation.CxFontManager;

/**
 * Constellation Label
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxLabel extends JLabel {
	private static final String BLANK = " ";

	/**
	 * Default constructor
	 */
	public CxLabel() {
		super();
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}
	
	/**
	 * Default constructor
	 */
	public CxLabel( final String label ) {
		super( label );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}
	
	/**
	 * Default constructor
	 */
	public CxLabel( final String label, final boolean bold ) {
		super( label );
		
		// setup the appropriate font
		if( bold ) {
			CxFontManager.setBoldFont( this );
		}
		else {
			CxFontManager.setDefaultFont( this );
		}
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