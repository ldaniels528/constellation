package constellation.app.ui.statusbar;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.LinkedList;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import constellation.ApplicationController;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;

/** 
 * Constellation Event Manager
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class EventManager extends CxDialog {
	// the singleton instance
	private static EventManager instance = null;
	
	// internal fields
	private final LinkedList<String> messages;
	private final StringBuilder scratch;
	private JEditorPane messagePane;
	
	/**
	 * Creates a new event manager instance
	 * @param controller the given {@link ApplicationController controller}
	 */
	public EventManager( final ApplicationController controller ) {
		super( controller, "Event Manager" );
		super.setDefaultCloseOperation( HIDE_ON_CLOSE );
		super.setContentPane( createContentPane( controller ) );
		super.pack();
		
		// initialize the message collection
		this.messages = new LinkedList<String>();
		this.scratch = new StringBuilder( 8192 );
	}
	
	/**
	 * Returns the singleton instance of the class
	 * @param controller the given {@link ApplicationController controller}
	 * @return the singleton {@link EventManager instance}
	 */
	public static EventManager getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new EventManager( controller ); 
		}
		return instance;
	}
	
	/** 
	 * Appends a new status message to the event manager
	 * @param statusMessage the given status message
	 */
	public void append( final String statusMessage ) {
		synchronized( messages ) {
			// attach the new message
			messages.add( statusMessage );
			while( messages.size() > 250 ) {
				messages.removeFirst();
			}
			
			// create a combined text string
			scratch.delete( 0, scratch.length() );
			for( final String message : messages  ) {
				scratch.append( message );
				scratch.append( '\n' );
			}
				
			// set the complete messages
			messagePane.setText(scratch.toString() );
		}
	}
	
	/**
	 * Creates the content pane
	 * @param controller the given {@link ApplicationController controller}
	 * @return the {@link CxPanel content pane}
	 */
	private CxPanel createContentPane( final ApplicationController controller ) {
		// create the scroll pane
		final JScrollPane scrollPane = new JScrollPane( messagePane = new JEditorPane() );
		scrollPane.setPreferredSize( controller.getFrameDimensions( 0.50, 0.50 ) );
		
		// create the panel
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 8, 8, 8, 8 );
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.attach( 0, 0, scrollPane, GridBagConstraints.NORTHWEST );
		return cp;
	}

}
