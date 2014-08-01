package constellation.ui.components;

import java.awt.Component;
import java.awt.GridBagConstraints;

/**
 * Constellation Icon Panel
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxIconPanel extends CxPanel {
	private final int columns;
	private int index;
	
	/**
	 * Default constructor
	 */
	public CxIconPanel( final int columns ) {
		this.columns	= columns;
		this.index		= 0;
		
		// setup the panel
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;
	}
	
	/** 
	 * Attaches the given component to the panel at the
	 * given grid coordinates
	 * @param gridx the given x-coordinate within the grid
	 * @param gridy the given y-coordinate within the grid
	 * @param comp the given {@link Component component}
	 */
	public void attach( final Component comp ) {
	    super.attach( col(index), row(index++), comp );
	}
	
	/**
	 * Computes the row that corresponds to the given index
	 * @param index the given index
	 * @return the calculated row
	 */
	public int row( final int index ) {
		return index / columns;
	}
	
	/**
	 * Computes the column that corresponds to the given index
	 * @param index the given index
	 * @return the calculated column
	 */
	public int col( final int index ) {
		return index % columns;
	}
	
}
