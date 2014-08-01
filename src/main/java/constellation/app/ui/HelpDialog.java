package constellation.app.ui;

import static constellation.ui.components.buttons.CxButton.createBorderlessButton;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.functions.Function;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;

/**
 * Constellation Help Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class HelpDialog extends CxDialog {
	private static HelpDialog instance = null;
	
	// icon declarations
	private final CxContentManager cxm = CxContentManager.getInstance();
	private final Icon HOME_ICON 	= cxm.getIcon( "images/dialog/help/home.gif" );
	private final Icon BACK_ICON 	= cxm.getIcon( "images/dialog/help/back.gif" );
	private final Icon FORWARD_ICON = cxm.getIcon( "images/dialog/help/forward.gif" );
	private final Icon RELOAD_ICON	= cxm.getIcon( "images/dialog/help/reload.gif" );
	
	// page declarations
	private static final String HOME_PAGE	 = "docs/index.html";
	private static final String NO_HELP_PAGE = "docs/functions/noHelp.html";
	
	// window constants
	private static final int MIN_WIDTH	= 500;
	private static final int MAX_WIDTH	= 700;
	private static final int MIN_HEIGHT	= 400;
	
	// internal fields
	private final LinkedList<URL> history;
	private JEditorPane htmlPane;
	private int position;
	
	/**
	 * Creates a new Help Dialog instance
	 * @param controller the given {@link ApplicationController controller}
	 */
	private HelpDialog( final ApplicationController controller ) {
		super( controller, "Constellation Help" );
		
		// create the history list
		this.history = new LinkedList<URL>();
		
		// setup the dialog
		super.setDefaultCloseOperation( HIDE_ON_CLOSE );
		super.setAlwaysOnTop( false );
		super.setContentPane( createContentPane() );
		super.setSize( controller.getFrameDimensions( 0.50d, 0.80d ) );
		super.setResizable( true );
		super.addComponentListener( new ComponentResizeHandler() );
	}
	
	/**
	 * Returns the singleton instance of the class
	 * @param controller the given {@link ApplicationController controller}
	 * @return the singleton instance of the {@link HelpDialog class}
	 */
	public static HelpDialog getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new HelpDialog( controller );
			instance.setLocation( controller.getUpperRightAnchorPoint( instance ) );
			instance.loadPage( HOME_PAGE );
		}
		return instance;
	}
	
	/**
	 * Loads the given resource path of the page (e.g. '/docs/index.html')
	 * @param resourcePath the given resource path
	 */
	public void loadPage( final String resourcePath ) {
		loadPage( cxm.getResourceURL( resourcePath ), true );
	}
	
	/**
	 * Loads the help documentation for the given function.
	 * @param function the given {@link Function function}
	 */
	public void loadPage( final Function function ) {
		// get the active function's help path
		final String resourcePath = function.getHelpPath();
		
		// if a help path is available ...
		if( resourcePath != null ) {
			loadPage( cxm.getResourceURL( resourcePath ), true );
		}
		else {
			// load the error page
			loadPage( cxm.getResourceURL( NO_HELP_PAGE ), true );
		}
	}
	
	/**
	 * Loads the given Page URL
	 * @param url the given {@link URL Page URL}
	 * @param addToHistory indicates whether the URL should be added to the history cache
	 */
	public void loadPage( final URL url, final boolean addToHistory ) {
		try {
			// set the page
			htmlPane.setPage( url );
			
			// record the URL?
			if( addToHistory ) {
				synchronized( history ) {
					// has the user move the cursor back?
					if( position < history.size() - 1 ) {
						// get all entries we plan to keep
						final List<URL> keepList = new ArrayList<URL>( history.subList( 0, position ) );
						
						// remove the entries
						history.retainAll( keepList );
					}
					
					// add the URL to history
					history.add( url );
					position = history.size() - 1;
				}
			}
		} 
		catch( final IOException cause ) {
			controller.showErrorDialog( "Unabled to open page", cause );
		} 
	}

	/**
	 * Creates the content pane of the dialog
	 * @return the content pane of the dialog
	 */
	private JComponent createContentPane() {
		// create the HTML
		htmlPane = new JEditorPane();
		htmlPane.setEditorKit( new HTMLEditorKit() ); 
		htmlPane.setEditable( false );
		htmlPane.addHyperlinkListener( new HyperLinkHandler() );
		
		// create the scroll pane
		final JScrollPane scrollPane = new JScrollPane( htmlPane );
		scrollPane.setPreferredSize( controller.getFrameDimensions( 0.50, 0.95 ) );
		
		// create the panel
		final CxPanel cp = new CxPanel();
		
		// heading
		int col = 0;
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		cp.attach( col++, 0, createBorderlessButton( HOME_ICON, new HomeAction(), "Home" ) );
		cp.attach( col++, 0, createBorderlessButton( BACK_ICON, new BackAction(), "Back" ) );
		cp.attach( col++, 0, createBorderlessButton( FORWARD_ICON, new ForwardAction(), "Forward" ) );
		cp.gbc.anchor = GridBagConstraints.EAST;
		cp.attach( col++, 0, createBorderlessButton( RELOAD_ICON, new ReloadAction(), "Reload" ) );
		
		// HTML display area
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.gbc.insets = new Insets( 8, 8, 8, 8 );
		cp.gbc.gridwidth = 4;
		cp.attach( 0, 1, scrollPane, GridBagConstraints.NORTHWEST );
		cp.gbc.gridwidth = 1;
		return cp;
	}
	
	/**
	 * HyperLink Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class HyperLinkHandler implements HyperlinkListener {

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
		 */
		public void hyperlinkUpdate( final HyperlinkEvent event ) {
			if( event.getEventType() == EventType.ACTIVATED ) {
				loadPage( event.getURL(), true );
			}
		}	
	}
	
	/**
	 * Home Navigation Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class HomeAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			loadPage( HOME_PAGE );
		}
	}
	
	/**
	 * Back Navigation Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class BackAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			synchronized( history ) {
				if( position > 0 ) {
					loadPage( history.get( --position ), false );
				}
			}
		}
	}
	
	/**
	 * Forward Navigation Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class ForwardAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			synchronized( history ) {
				if( position + 1 < history.size() ) {
					loadPage( history.get( ++position ), false );
				}
			}
		}
	}
	
	/**
	 * Reload Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class ReloadAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the active function
			final Function function = controller.getActiveFunction();
			
			// load the page for the function
			loadPage( function );
		}
	}
	
	/** 
	 * Component Resize Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class ComponentResizeHandler implements ComponentListener {
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
		 */
		public void componentHidden( final ComponentEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
		 */
		public void componentMoved( final ComponentEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
		 */
		public void componentResized( final ComponentEvent event ) {
			// get the proposed size the of dialog
			final Dimension proposedSize = HelpDialog.this.getSize();
			
			// control the minimum and maximum window size
			if( proposedSize.width < MIN_WIDTH || 
				proposedSize.width > MAX_WIDTH || 
				proposedSize.height < MIN_HEIGHT ) {
				final int newWidth	= ( proposedSize.width < MIN_WIDTH ) ? MIN_WIDTH : ( ( proposedSize.width > MAX_WIDTH ) ? MAX_WIDTH : proposedSize.width );
				final int newHeight = ( proposedSize.height < MIN_HEIGHT ) ? MIN_HEIGHT : proposedSize.height;
				HelpDialog.this.setSize( newWidth, newHeight );
			}
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
		 */
		public void componentShown( final ComponentEvent event ) {
			// do nothing
		}
	}

}
