package constellation.ui.components.comboboxes;

import javax.swing.JComboBox;

import constellation.drawing.LinePatterns;

/**
 * Constellation Line Pattern ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class LinePatternBox extends JComboBox {
	
	/**
	 * Default constructor
	 */
	public LinePatternBox() {
		super( new String[] { "Default", "Solid", "Dashed" } );
	}
	
	/**
	 * Resets the component to it's initial state
	 */
	public void reset() {
		super.setSelectedIndex( 0 );
	}

	/** 
	 * Returns the selected pattern
	 * @return the given {@link LinePatterns pattern}
	 */
	public LinePatterns getSelectedPattern() {
		// get the selected index
		final int index = super.getSelectedIndex();
		
		// set the appropriate pattern reference
		return LinePatterns.values()[ index ];
	}
	
	/**
	 * Sets the selected pattern
	 * @param pattern the given {@link LinePatterns pattern}
	 */
	public void setSelectedPattern( final LinePatterns pattern ) {
		switch( pattern ) {
			case PATTERN_SOLID: 	setSelectedIndex( 1 ); break;
			case PATTERN_DASHED:	setSelectedIndex( 2 ); break;
			default:
				setSelectedIndex( 0 );
		}
	}

}
