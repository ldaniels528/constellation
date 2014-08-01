package constellation.thirdparty.formats.iges;

import static java.awt.Color.*;
import java.awt.Color;

/**
 * IGES Constants
 * @author lawrence.daniels@gmail.com
 */
public interface IGESConstants {	
	// IGES Color to Java Color mapping
	Color[] IGES_COLORS = {
			null, BLACK, RED, GREEN, BLUE,
			YELLOW, MAGENTA, CYAN, WHITE, GRAY, ORANGE
	};
	
	// Color Constants
	int IGES_COLOR_UNSPEC			= 0;
	int IGES_COLOR_BLACK			= 1;
	int IGES_COLOR_RED				= 2;
	int IGES_COLOR_GREEN			= 3;
	int IGES_COLOR_BLUE				= 4;
	int IGES_COLOR_YELLOW			= 5;
	int IGES_COLOR_MAGENTA			= 6;
	int IGES_COLOR_CYAN				= 7;
	int IGES_COLOR_WHITE			= 8;
	
	// Drafting Standard Constants
	int IGES_STD_UNSPEC				= 0; // No standard specified 
	int IGES_STD_ISO				= 1; // International Organization for Standardization
	int IGES_STD_AFNOR				= 2; // French Association for Standardization
	int IGES_STD_ANSI				= 3; // American National Standards Institute 
	int IGES_STD_BSI				= 4; // British Standards Institute
	int IGES_STD_CSA				= 5; // Canadian Standards Association
	int IGES_STD_DIN				= 6; // German Institute for Standardization
	int IGES_STD_JIS				= 7; // Japanese_Institute_for_Standardization
	
	// Line Pattern Constants
	int IGES_PATTERN_UNSPEC			= 0;
	int IGES_PATTERN_SOLID			= 1;
	int IGES_PATTERN_DASHED			= 2;
	int IGES_PATTERN_PHANTOM		= 3;
	int IGES_PATTERN_CENTERLINE		= 4;
	int IGES_PATTERN_DOTTED			= 5;
	
	// Unit Constants
	int IGES_UNITS_DEFAULT			= 0;
	int IGES_UNITS_INCHES			= 1;
	int IGES_UNITS_MILLIMETERS		= 2;
	int IGES_UNITS_FEET				= 3;
	int IGES_UNITS_MILES			= 4;
	int IGES_UNITS_METERS			= 5;
	int IGES_UNITS_KILOMETERS		= 6;
	int IGES_UNITS_MILS				= 7;
	int IGES_UNITS_MICRONS			= 8;
	int IGES_UNITS_CENTIMETERS		= 9;
	int IGES_UNITS_MICROINCHES		= 10;
	
}
