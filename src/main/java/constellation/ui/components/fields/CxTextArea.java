package constellation.ui.components.fields;

import javax.swing.JTextArea;

import constellation.CxFontManager;

/**
 * Constellation Text Area
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxTextArea extends JTextArea {
	private static final String BLANK = "";
	
	/**
	 * Default constructor
	 */
	public CxTextArea() {
		super();
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}

	/**
	 * Creates a new text area with the given number of columns and rows
	 * @param columns the given number of display columns
	 * @param rows the given number of display rows
	 */
	public CxTextArea( final int columns, final int rows ) {
		super( columns, rows );
	}
	
	/**
	 * Creates a new text area with the given number of columns and rows
	 * @param columns the given number of display columns
	 * @param rows the given number of display rows
	 * @param lineWrap indicates whether lines should wrap
	 */
	public CxTextArea( final int columns, final int rows, final boolean lineWrap ) {
		super( columns, rows );
		super.setLineWrap( lineWrap );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	public void setText( final String text ) {
		super.setText( ( text != null ) ? text : BLANK );
	}
	
}
