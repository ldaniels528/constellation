package constellation.tools.weather;

import static constellation.ui.components.buttons.CxButton.createBorderlessButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.PluginManager;
import constellation.ui.components.CxPlugIn;

/** 
 * Constellation Weather Widget
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class WeatherForecastPlugin extends CxPlugIn {
	// singleton instance
	private static WeatherForecastPlugin instance;
	
	// icon declarations
	private static final CxContentManager contentManager = CxContentManager.getInstance();
	private static final Icon CLEAR_DAY 		= contentManager.getIcon( "images/extensions/weather/weather-clear.png" );
	private static final Icon CLEAR_NIGHT		= contentManager.getIcon( "images/extensions/weather/weather-clear-night.png" );
	private static final Icon FEW_CLOUDS_DAY	= contentManager.getIcon( "images/extensions/weather/weather-overcast.png" );
	private static final Icon FEW_CLOUDS_NIGHT	= contentManager.getIcon( "images/extensions/weather/weather-few-clouds-night.png" );
	private static final Icon SHOWERS			= contentManager.getIcon( "images/extensions/weather/weather-showers.png" );
	private static final Icon SHOWERS_SCATTERED	= contentManager.getIcon( "images/extensions/weather/weather-showers-scattered.png" );
	private static final Icon SEVERE_ALERT		= contentManager.getIcon( "images/extensions/weather/weather-severe-alert.png" );
	private static final Icon SNOW				= contentManager.getIcon( "images/extensions/weather/weather-snow.png" );
	private static final Icon STORM				= contentManager.getIcon( "images/extensions/weather/weather-storm.png" );
	private static final Icon QUIT_ICON			= contentManager.getIcon( "images/extensions/weather/eject.png" );
	
	// forecast definitions
	private static final WeatherCondition[] CONDITIONS = {
		new WeatherCondition( CLEAR_DAY, CLEAR_NIGHT, "Clear" ), 
		new WeatherCondition( FEW_CLOUDS_DAY, FEW_CLOUDS_NIGHT, "Partly Cloudly" ), 
		new WeatherCondition( SEVERE_ALERT, SEVERE_ALERT, "Severe Storms" ), 
		new WeatherCondition( SHOWERS, SHOWERS, "Showers" ), 
		new WeatherCondition( SHOWERS_SCATTERED, SHOWERS_SCATTERED, "Scattered Showers" ), 
		new WeatherCondition( SNOW, SNOW, "Snow" ),
		new WeatherCondition( STORM, STORM, "Storm" )
	};
	
	// internal fields
	private final ApplicationController controller;
	private final WeatherCondition[] forecast;
	
	/**
	 * Default Constructor
	 */
	public WeatherForecastPlugin( final ApplicationController controller ) {
		this.controller = controller;
		
		// get the random instance
		final Random random = new Random( System.currentTimeMillis() );
		
		// build seven day forecast
		forecast = new WeatherCondition[7];
		for( int n = 0; n < forecast.length; n++ ) {
			forecast[n] = CONDITIONS[ random.nextInt( CONDITIONS.length ) ];
		}
		
		// attach the components
		int column = 0;
		super.gbc.weightx = 2;
		for( int n = 0; n < forecast.length; n++ ) {
			// create the icon label w/tool tip
			final JLabel label = new JLabel( forecast[n].getIcon() );
			label.setToolTipText( forecast[n].getDescription() );
			
			// attach the label
			super.attach( column++, 0, label );
		}	
		
		// attach eject button
		super.attach( column++, 0, createBorderlessButton( QUIT_ICON, new QuitSessionAction(), "Eject this plugin" ) );
	}
	
	/** 
	 * Returns the weather forecast plug-in
	 * @param controller the given {@link ApplicationController controller}
	 * @return the {@link WeatherForecastPlugin weather forecast plug-in}
	 */
	public static WeatherForecastPlugin getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new WeatherForecastPlugin( controller );
		}
		return instance;
	}
	
	/**
	 * Quit Session Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class QuitSessionAction implements ActionListener {

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// unload the plug-in
			final PluginManager pluginManager = controller.getPluginManager();
			pluginManager.unloadPlugin();
		}
	}

}
