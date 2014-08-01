package constellation.tools.demopro.plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.PluginManager;
import constellation.ui.components.menu.CxMenuItem;

/**
 * Constellation DemoPro Menu Item
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class DemoProMenuItem extends CxMenuItem {
	// define images
	private static final CxContentManager CONTENT_MANAGER = CxContentManager.getInstance();
	private static final Icon TAPE = CONTENT_MANAGER.getIcon( "images/extensions/demopro/tape.png" );
	
	/**
	 * Default Constructor
	 */
	public DemoProMenuItem( final ApplicationController controller ) {
		super( "Demo Pro", TAPE, new LaunchRecordingDeviceAction( controller ) );
		super.setToolTipText( "Record and playback geometry creation" );
	}
	
	/**
	 * Launch Recording Device Action
	 * @author lawrence.daniels@gmail.com
	 */
	private static class LaunchRecordingDeviceAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new recording device launch action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public LaunchRecordingDeviceAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the plug-in manager
			final PluginManager pluginManager = controller.getPluginManager();
			
			// set the plug-in
			pluginManager.setPlugin( RecordingPlugin.getInstance( controller ) );
		}
	}
	
}
