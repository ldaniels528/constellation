package constellation.tools.weather;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.PluginManager;
import constellation.ui.components.menu.CxMenuItem;

/**
 * Constellation Weather Forecast Menu Item
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class WeatherForecastMenuItem extends CxMenuItem {
	// define images
	private static final CxContentManager CONTENT_MANAGER = CxContentManager.getInstance();
	private static final Icon WEATHER = CONTENT_MANAGER.getIcon( "images/extensions/weather/weather-clear.png" );
	
	/**
	 * Default Constructor
	 */
	public WeatherForecastMenuItem( final ApplicationController controller ) {
		super( "Weather Forecast", WEATHER, new LaunchWeatherForecastAction( controller ) );
		super.setToolTipText( "7-day weather forecast" );
	}
	
	/**
	 * Launch Weather Forecast Action
	 * @author lawrence.daniels@gmail.com
	 */
	private static class LaunchWeatherForecastAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new recording device launch action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public LaunchWeatherForecastAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the plug-in manager
			final PluginManager pluginManager = controller.getPluginManager();
			
			// set the plug-in
			pluginManager.setPlugin( WeatherForecastPlugin.getInstance( controller ) );
		}
	}

}
