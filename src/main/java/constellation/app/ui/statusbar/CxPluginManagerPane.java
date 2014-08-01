package constellation.app.ui.statusbar;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import constellation.CxContentManager;
import constellation.CxFontManager;
import constellation.PluginManager;
import constellation.ui.components.CxPanel;
import constellation.ui.components.CxPlugIn;

/** 
 * Information Plug-in Manager
 * @see constellation.ui.components.CxPlugIn
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxPluginManagerPane extends CxPanel implements PluginManager {
	// define the default plug-in
	private static final JComponent DEFAULT_PLUGIN = new DefaultPlugin();
	
	// icon definitions
	private final CxContentManager contentManager = CxContentManager.getInstance();
	private final Icon PLUGIN_ICON 	= contentManager.getIcon( "images/informationbar/plugin.png" );
	
	// internal fields
	private final Logger logger = Logger.getLogger( getClass() );
	
	/**
	 * Creates a new plug-in pane
	 */
	public CxPluginManagerPane() {
		super.gbc.fill = GridBagConstraints.NONE;
		super.gbc.insets = new Insets( 0, 1, 0, 1 );
		super.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		
		// set the default plug-in
		setPlugin( null );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.PluginManager#setPlugin(constellation.ui.components.CxPlugIn)
	 */
	public void setPlugin( final JComponent plugin ) {
		// remove all components
		super.removeAll();
		
		// attach the new one
		if( plugin != null ) {
			super.attach( 0, 0, plugin );
			logger.info( format( "%s is %dx%d", plugin.getClass().getSimpleName(), plugin.getWidth(), plugin.getHeight() ) );
		}
		
		// attach the default plug-in
		else {
			super.attach( 0, 0, new JLabel( PLUGIN_ICON ) );
			super.attach( 1, 0, DEFAULT_PLUGIN );
		}
		
		// update the panel
		super.updateUI();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.PluginManager#unloadPlugin()
	 */
	public void unloadPlugin() {
		setPlugin( null );
	}

	/** 
	 * Default Plug-in
	 * @author lawrence.daniels@gmail.com
	 */
	private static class DefaultPlugin extends CxPlugIn {
		
		/**
		 * Default Constructor
		 */
		public DefaultPlugin() {
			super.setPreferredSize( new Dimension( 300, 24 ) );
			
			// create the label
			final JLabel label = new JLabel( "No active plug-in" );
			label.setForeground( Color.GRAY );
			CxFontManager.setItalicFont( label );
			
			// attach the component
			super.attach( 0, 0, label, GridBagConstraints.NORTHWEST );
		}
		
	}

}
