package constellation.functions;

import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import constellation.ApplicationController;

/**
 * Represents a drawing function
 * @author lawrence.daniels@gmail.com
 */
public interface Function extends PickListObserver {
	
	////////////////////////////////////////////////////////////////////
	//		Identifying Attribute Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the family name of the function (e.g. "Curve")
	 * @return the family name of the function
	 */
	String getFamilyName();

	/**
	 * Returns the name of the function (e.g. "Circle Radius")
	 * @return the name of the function
	 */
	String getName();
	
	/**
	 * Returns the resource path to the "help" documents for
	 * the function (e.g. "docs/functions/lines/pt-pt.html")
	 * @return the resource path
	 */
	String getHelpPath();
	
	/**
	 * Returns the function icon
	 * @return the {@link Icon icon}
	 */
	Icon getIcon();
	
	////////////////////////////////////////////////////////////////////
	//		Life Cycle Methods
	////////////////////////////////////////////////////////////////////
	
	/** 
	 * This method is called upon clicking the function
	 * @param controller the given {@link ApplicationController controller}
	 */
	void onStart( ApplicationController controller ) ;
	
	/** 
	 * This method is called upon clicking a different function
	 * @param controller the given {@link ApplicationController controller}
	 */
	void onFinish( ApplicationController controller );
	
	////////////////////////////////////////////////////////////////////
	//		Dialog-related Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the component that enables function-specific
	 * parameters to be specified.
	 * @return the {@link FunctionDialogPlugIn plug-in} or <tt>null</tt> if
	 * no parameters are needed. 
	 */
	FunctionDialogPlugIn getParameterPlugin();
	
	/** 
	 * Toggles the visibility on/off of the function dialog
	 */
	void toggleDialogVisibility();
	
	////////////////////////////////////////////////////////////////////
	//		Input-related Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the function's pop-up menu 
	 * @param controller the given {@link ApplicationController controller}
	 * @return the {@link JPopupMenu pop-up menu}
	 */
	JPopupMenu getPopupMenu( ApplicationController controller );
	
	/** 
	 * Called when a key has been typed into the drawing pane
	 * @param controller the given {@link ApplicationController controller}
	 * @param event the given {@link KeyEvent key event}
	 * @param mousePos the last known {@link Point position} of the mouse
	 */
	void onKeyPressed( ApplicationController controller, KeyEvent event, Point mousePos );
	
	/** 
	 * Processes the given mouse click
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	void processMouseClick( ApplicationController controller, MouseClick mouseClick );
	
	/** 
	 * Processes the given mouse click
	 * @param controller the given {@link ApplicationController controller}
	 * @param mousePos the given mouse {@link Point position}
	 */
	void processMouseMovement( ApplicationController controller, Point mousePos );
	
	/** 
	 * Processes the given mouse drag
	 * @param controller the given {@link ApplicationController controller}
	 * @param oldMousePos the previous mouse {@link MouseClick position}
	 * @param newMousePos the current mouse {@link MouseClick position}
	 * @return true, if the drag event was consumed
	 */
	boolean processMouseDrag( ApplicationController controller, MouseClick oldMousePos, MouseClick newMousePos );
	
	/**
	 * Indicates whether the function supports mouse dragging
	 * @return true, if the function supports mouse dragging
	 */
	boolean supportsDrag();

	/**
	 * Indicates whether the function supports geometry selection
	 * @return true, if the function supports geometry selection
	 */
	boolean supportsSelection();
	
}
