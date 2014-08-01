package constellation.app.ui.statusbar;

import static constellation.ui.components.buttons.CxButton.createBorderlessButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ui.components.CxPanel;

/**
 * Constellation Status Bar Pane
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class StatusBarPane extends CxPanel {
	// icon definitions
	private final CxContentManager contentManager = CxContentManager.getInstance();
	private final Icon EVENT_MGR_ICON = contentManager.getIcon( "images/statusbar/eventManager.png" );
	
	// internal fields
	private static final String BLANK = "\t";
	private final CxPluginManagerPane pluginPane;
	private final CxInputTypePane inputPane;
	private JLabel statusLabel;
	
	/**
	 * Creates a new action panel
	 * @param controller the given {@link ApplicationController controller}
	 */
	public StatusBarPane( final ApplicationController controller ) {
		super.gbc.anchor = GridBagConstraints.WEST;
		super.gbc.fill = GridBagConstraints.BOTH;
				
		// create the pane
		final CxPanel statusArea = new CxPanel();
		statusArea.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		statusArea.gbc.anchor = GridBagConstraints.WEST;
		statusArea.gbc.fill = GridBagConstraints.VERTICAL;
		statusArea.attach( 0, 0, createBorderlessButton( EVENT_MGR_ICON, new LaunchEventManagerAction( controller ), "Launch the event manager" ) );
		statusArea.gbc.fill = GridBagConstraints.BOTH;
		statusArea.attach( 1, 0, statusLabel = new JLabel(), GridBagConstraints.WEST );
		
		// construct the components
		// column #1
		int col = 0;
		super.gbc.weightx = 2.0;
		super.attach( col++, 0, statusArea );
		
		// column #3
		super.gbc.weightx = 0.05;
		super.attach( col++, 0, pluginPane = new CxPluginManagerPane() );
		
		//  function instruction area
		super.gbc.weightx = 0.01;
		super.gbc.anchor = GridBagConstraints.EAST;
		super.attach( col++, 0, inputPane = new CxInputTypePane( controller ) );
	}
	
	/** 
	 * Resets the input type
	 */
	public void resetInputType() {
		inputPane.reset();
	}
	
	/**
	 * Sets the icon that corresponds to the given input type
	 * @param inputType the given {@link CxInputTypes input type}
	 */
	public void setInputType( final CxInputTypes inputType ) {
		inputPane.setType( inputType );
	}
	
	/** 
	 * Returns an instance of the plug-in manager
	 * @return the {@link CxPluginManagerPane plug-in manager}
	 */
	public CxPluginManagerPane getPluginManager() {
		return pluginPane;
	}
	
	/**
	 * Clears the status text
	 */
	public void clearStatus() {
		setStatus( BLANK );
	}
	
	/**
	 * Sets the status with the given text
	 * @param text the given text string
	 */
	public void setStatus( final String text ) {
		statusLabel.setText( text );
		statusLabel.updateUI();
	}
	
	/** 
	 * Launch Event Manager Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class LaunchEventManagerAction implements ActionListener {
		final ApplicationController controller;
		
		public LaunchEventManagerAction( final ApplicationController controller ) {
			this.controller = controller;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			final EventManager eventManager = EventManager.getInstance( controller );
			eventManager.makeVisible();
		}
		
	}
	
}
