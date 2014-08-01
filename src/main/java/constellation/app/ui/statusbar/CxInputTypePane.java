package constellation.app.ui.statusbar;

import static constellation.app.ui.statusbar.CxInputTypes.KEYBOARD;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_LEFT;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_MIDDLE;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_RIGHT;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_WHEEL;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ThreadPool;

/** 
 * Constellation Input Label
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxInputTypePane extends JLabel {
	// icon definitions
	private static final CxContentManager contentMgr = CxContentManager.getInstance();
	private	static final Icon NONE	= contentMgr.getIcon( "images/informationbar/input/waiting.gif" );
	private	static final Icon KBD	= contentMgr.getIcon( "images/informationbar/input/KBD.png" );
	private	static final Icon LMB	= contentMgr.getIcon( "images/informationbar/input/LMB.png" );
	private	static final Icon MMB	= contentMgr.getIcon( "images/informationbar/input/MMB.png" );
	private	static final Icon RMB	= contentMgr.getIcon( "images/informationbar/input/RMB.png" );
	private	static final Icon MWL	= contentMgr.getIcon( "images/informationbar/input/MWL.png" );
	
	// define the input type to icon mapping
	private static final Map<CxInputTypes, Icon> ICONS;
	static {
		ICONS = new HashMap<CxInputTypes, Icon>();
		ICONS.put( MOUSE_LEFT, 		LMB );
		ICONS.put( MOUSE_RIGHT, 	RMB );
		ICONS.put( MOUSE_MIDDLE, 	MMB );
		ICONS.put( MOUSE_WHEEL, 	MWL );
		ICONS.put( KEYBOARD, 		KBD );
	}
	
	// input type constants
	private static final long INPUT_TYPE_DELAY = 500; // 1 seconds
	
	// internal fields
	private long inputStatusResetTime;
	
	/**
	 * Creates the input label
	 * @param controller the given {@link ApplicationController controller}
	 */
	public CxInputTypePane( final ApplicationController controller ) {
		super.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		super.setIcon( NONE );
		
		// add the task
		final ThreadPool threadPool = controller.getThreadPool();
		threadPool.schedule( new ResetTask(), 250 );
	}
	
	/** 
	 * Resets the input type
	 */
	public void reset() {
		super.setIcon( NONE );
	}
	
	/** 
	 * Sets the icon based on the given input type
	 * @param type the given {@link CxInputTypes input type}
	 */
	public void setType( final CxInputTypes type ) {
		// set the appropriate icon
		super.setIcon( ICONS.get( type ) );
		
		// set the reset delay
		inputStatusResetTime = System.currentTimeMillis() + INPUT_TYPE_DELAY;
	}
	
	/**
	 * Input Type Reset Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class ResetTask extends TimerTask {
		
		/* 
		 * (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// does the input type need resetting?
			if( ( inputStatusResetTime > 0 ) && 
					( System.currentTimeMillis() >= inputStatusResetTime ) ) {
				reset();
				inputStatusResetTime = 0;
			}
		}
		
	}
}