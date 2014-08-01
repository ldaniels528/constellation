package constellation.ui.components.buttons;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import constellation.ui.components.icons.CxColorCubeIcon;

/**
 * Constellation Custom Color Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxCustomColorButton extends JButton 
implements ActionListener {
	private final CxColorCubeIcon colorCube;
	
	/** 
	 * Default constructor
	 */
	public CxCustomColorButton( final Color initialColor ) {
		this.colorCube = new CxColorCubeIcon( initialColor );
		super.setIcon( colorCube );
		super.addActionListener( this );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed( final ActionEvent event ) {
		// get the color
		final Color color = JColorChooser.showDialog( this, "Custom Color", colorCube.getColor() );
		if( color != null ) {
			colorCube.setColor( color );
		}
	}
	
	/** 
	 * Returns the selected color
	 * @return the selected {@link Color color}
	 */
	public Color getSelectedColor() {
		return colorCube.getColor();
	}
	
	/** 
	 * Sets the selected color
	 * @param color the selected {@link Color color}
	 */
	public void setSelectedColor( final Color color ) {
		colorCube.setColor( color );
	}
	
}