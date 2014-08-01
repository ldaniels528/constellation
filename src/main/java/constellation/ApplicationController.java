package constellation;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JFrame;

import constellation.drawing.Camera;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.functions.Function;
import constellation.functions.Steps;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Constellation Function Controller
 * @author lawrence.daniels@gmail.com
 */
public interface ApplicationController {
	
	////////////////////////////////////////////////////////////////////
	//		Camera-related Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the camera instance
	 * @return the {@link Camera camera} instance
	 */
	Camera getCamera();
	
	/**
	 * Returns the world coordinate system (WCS) to screen coordinate system (SCS)
	 * @return the {@link MatrixWCStoSCS WCS to SCS matrix}
	 */
	MatrixWCStoSCS getMatrix();
	
	/**
	 * Requests a re-painting of the drawing canvas
	 */
	void requestRedraw();
	
	/**
	 * Translates the given screen coordinates to model space
	 * @param screenPoint the given {@link Point screen point}
	 * @return a {@link PointXY point} in two-dimensional space
	 */
	PointXY untransform( Point screenPoint );
	
	/**
	 * Transforms the given screen boundary to model space
	 * @param boundary the given {@link Rectangle screen boundary}
	 * @return the {@link RectangleXY selection boundary} in two-dimensional space
	 */
	RectangleXY untransform( Rectangle boundary );
	
	/**
	 * Performs a zoom of the camera to fit the region of two-dimensional 
	 * space represented by the given boundary.
	 * @param boundary the given {@link RectangleXY boundary}
	 */
	void zoomToFit( RectangleXY boundary );
	
	/**
	 * Performs a zoom of the camera to fit the region of two-dimensional 
	 * space represented by the given coordinates.
	 * @param camera the given {@link Camera camera}
	 * @param x1 the given minimum X coordinate
	 * @param y1 the given minimum Y coordinate
	 * @param x2 the given maximum X coordinate
	 * @param y2 the given maximum Y coordinate
	 */
	void zoomToFit( double x1, double y1, double x2, double y2 );
	
	////////////////////////////////////////////////////////////////////
	//		Dialog-related Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the top-level window
	 * @return the {@link JFrame frame}
	 */
	JFrame getFrame();
	
	/** 
	 * Returns the dimension of the drawing pane
	 * @return the {@link Dimension dimension} 
	 */
	Dimension getDrawingDimensions();
	
	/** 
	 * Returns the dimension of the area represented by the given percentages
	 * @param pctWidth the given percentage of the width
	 * @param pctHeight the given percentage of the height
	 * @return the {@link Dimension dimension}
	 */
	Dimension getFrameDimensions( double pctWidth, double pctHeight );
	
	/** 
	 * Returns the center anchor point in the middle of the window
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	Point getCenterAnchorPoint( JDialog dialog );
	
	/** 
	 * Returns the left-most anchor point just above the navigation bar
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	Point getLowerLeftAnchorPoint( JDialog dialog );
	
	/** 
	 * Returns the anchor point for the given dialog
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	Point getLowerRightAnchorPoint( JDialog dialog );
	
	/** 
	 * Returns the left-most anchor point just below the information bar
	 * @return the anchor {@link Point point}
	 */
	Point getUpperLeftAnchorPoint();
	
	/** 
	 * Returns the anchor point for the given dialog
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	Point getUpperRightAnchorPoint( JDialog dialog );
	
	////////////////////////////////////////////////////////////////////
	//		Display Preference-related Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Indicates whether the highlights are being displayed
	 * @return true, if the highlights are being displayed
	 */
	boolean showHighlights();
	
	/**
	 * Sets the highlight display state
	 * @param highlights the given highlight display state
	 */
	void showHighlights( boolean highlights );
	
	/** 
	 * Returns the system preferences
	 * @return the {@link SystemPreferences system preferences}
	 */
	SystemPreferences getSystemPreferences();
	
	////////////////////////////////////////////////////////////////////
	//		Function-related Methods
	////////////////////////////////////////////////////////////////////
	
	/** 
	 * Returns the "active" function 
	 * @return the currently "active" {@link Function function}
	 */
	Function getActiveFunction();
	
	/** 
	 * Sets the "active" function 
	 * @param function the given {@link Function function}
	 */
	void setActiveFunction( Function function );
	
	/**
	 * Displays an informational message dialog
	 * @param message the given message
	 * @param title the message title
	 */
	void showMessageDialog( String message, String title );
	
	/**
	 * Displays an error message dialog
	 * @param message the given message
	 * @param title the message title
	 */
	void showErrorDialog( String message, String title );
	
	/**
	 * Displays an error message dialog
	 * @param message the given message
	 * @param title the message title
	 * @param cause the {@link Throwable cause} of the error
	 */
	void showErrorDialog( String message, String title, Throwable cause );
	
	/**
	 * Displays an error message dialog
	 * @param message the given message
	 * @param title the message title
	 */
	void showErrorDialog( String title, Throwable cause );
	
	/**
	 * Displays a on-screen instructional step 
	 * @param steps the given {@link Steps steps}
	 */
	void setInstructionalSteps( Steps steps );
	
	/**
	 * Displays a on-screen message 
	 * @param message the given status message
	 */
	void setStatusMessage( String message );
	
	////////////////////////////////////////////////////////////////////
	//		Model Methods
	////////////////////////////////////////////////////////////////////
		
	/** 
	 * Returns the geometric model
	 * @return the {@link GeometricModel model}
	 */
	GeometricModel getModel();
	
	/** 
	 * Merges the given model into the current model
	 * @param model the {@link GeometricModel model} to merge from
	 */
	void mergeModel( GeometricModel model );
	
	/** 
	 * Sets the geometric model
	 * @param model the {@link GeometricModel model}
	 */
	void setModel( GeometricModel model );
	
	/**
	 * Updates the title of the top-level window
	 * @param model the given {@link GeometricModel model}
	 */
	void updateTitle( GeometricModel model );
	
	////////////////////////////////////////////////////////////////////
	//		Miscellaneous Methods
	////////////////////////////////////////////////////////////////////
		
	/**
	 * Returns the plug-in manager
	 * @return the {@link PluginManager plug-in manager} instance
	 */
	PluginManager getPluginManager();
	
	/**
	 * Retrieves the thread pool instance
	 * @return the {@link ThreadPool thread pool} instance
	 */
	ThreadPool getThreadPool();
	
	/**
	 * Shuts down the application
	 */
	void shutdown();
	
	////////////////////////////////////////////////////////////////////
	//		Selection-related Methods
	////////////////////////////////////////////////////////////////////
	
	/** 
	 * Returns the selection mode
	 * @return the {@link SelectionMode selection mode}
	 */
	SelectionMode getSelectionMode();
	
	/** 
	 * Sets the selection mode
	 * @param mode the {@link SelectionMode selection mode}
	 */
	void setSelectionMode( SelectionMode mode );
	
	/** 
	 * Returns the selection boundary at the given (x,y) coordinate
	 * @param p the given (x,y) coordinate point
	 * @return the selection {@link Rectangle boundary}
	 */
	Rectangle getSelectionBoundary( Point p );
	
	/**
	 * Returns the selection boundary at the current mouse position
	 * @return the selection {@link Rectangle boundary}
	 */
	Rectangle getSelectionBoundary();
	
}
