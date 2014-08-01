package constellation.model;

/**
 * Represents the collection of available units
 * @author lawrence.daniels@gmail.com
 */
public class Units {	
	// define the English Conversions
	private static final double PIXEL_INCH			= 100.0; 
	private static final double PIXEL_MICRO_INCH	= PIXEL_INCH / 1000.0;
	private static final double PIXEL_FOOT			= PIXEL_INCH * 12.0;
	private static final double PIXEL_MILE			= PIXEL_FOOT * 5285.0;
	
	// define the Metric Conversions
	private static final double PIXEL_MM 			= PIXEL_INCH / 25.4;
	private static final double PIXEL_CM 			= PIXEL_MM * 10.0; 
	private static final double PIXEL_METER			= PIXEL_MM * 1000.0;
	private static final double PIXEL_KILOMETER		= PIXEL_METER * 1000.0;
	
	// define the unit constants
	public static final Unit UNITS_MILLIMETERS	= new Unit( 0, "MILLIMETERS", "mm", PIXEL_MM, 100 );
	public static final Unit UNITS_INCHES		= new Unit( 1, "INCHES", "in", PIXEL_INCH, 1 ); 
	public static final Unit UNITS_FEET			= new Unit( 2, "FEET", "ft", PIXEL_INCH, 1 );
	public static final Unit UNITS_MILES		= new Unit( 3, "MILES", "mi", PIXEL_MILE, 1 );
	public static final Unit UNITS_METERS		= new Unit( 4, "METERS", "m", PIXEL_METER, 1 );
	public static final Unit UNITS_KILOMETERS	= new Unit( 5, "KILOMETERS", "km", PIXEL_KILOMETER, 1 );
	public static final Unit UNITS_MILS			= new Unit( 6, "MILS", "mil", 1, 1 );
	public static final Unit UNITS_MICRONS		= new Unit( 7, "MICRONS", "mic", 1, 100 );
	public static final Unit UNITS_CENTIMETERS	= new Unit( 8, "CENTIMETERS", "cm", PIXEL_CM, 10 );
	public static final Unit UNITS_MICROINCHES	= new Unit( 9, "MICRO-INCHES", "min", PIXEL_MICRO_INCH, 1000 );
	
	// define the array of units
	public static final Unit[] UNITS = {
		UNITS_MILLIMETERS, UNITS_INCHES, UNITS_FEET, UNITS_MILES,
		UNITS_METERS, UNITS_KILOMETERS, UNITS_MILS, UNITS_MICRONS,
		UNITS_CENTIMETERS, UNITS_MICROINCHES
	};
	
	/**
	 * Default constructor
	 */
	private Units() {
		super();
	}
	
	/**
	 * Returns the array of defined units
	 * @return the array of {@link Unit units}
	 */
	public static Unit[] values() {
		UNITS_INCHES.getGridSpacing();
		return UNITS;
	}
	
}
