package constellation.tools.weather;

import java.util.Calendar;

import javax.swing.Icon;

/** 
 * Constellation Weather Condition
 * @author lawrence.daniels@gmail.com
 */
public class WeatherCondition {
	// internal fields
	private String description;
	private Icon iconNight;
	private Icon iconDay;
	
	/**
	 * Default Constructor
	 */
	public WeatherCondition( final Icon iconDay, 
							 final Icon iconNight,
							 final String description ) {
		this.iconDay 		= iconDay;
		this.iconNight		= iconNight;
		this.description	= description;
	}
	
	/**
	 * Returns the icon
	 * @return the {@link Icon icon}
	 */
	public Icon getIcon() {
		// get the time of day
		final Calendar calendar = Calendar.getInstance();
		final int hour = calendar.get( Calendar.HOUR_OF_DAY );
		
		// return the daytime or night icon
		return ( hour >= 7 && hour <= 19 ) ? iconDay : iconNight;
	}
	
	/**
	 * Returns the description
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
