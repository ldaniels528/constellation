package constellation.app;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import constellation.CxConfigurationUtil;
import constellation.CxContentManager;
import constellation.app.datamgmt.DataManagement;
import constellation.tools.demopro.app.RecordingViewer;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;

/**
 * Constellation Application Module Launcher
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxLauncher extends JFrame implements Runnable {
	// log4j instance
	private static final Logger logger = Logger.getLogger( CxLauncher.class );
	
	// image definitions
	private final CxContentManager cxm = CxContentManager.getInstance();
	private final Icon DRAFTING_ICON 	= cxm.getIcon( "images/launcher/drafting.png" );
	private final Icon DATA_MGMT_ICON	= cxm.getIcon( "images/launcher/inventory.png" );
	private final Icon REC_VIEWER_ICON	= cxm.getIcon( "images/launcher/playback.png" );
	
	/**
	 * Default Constructor
	 */
	public CxLauncher() {
		super( "Constellation Launcher" );
		super.setContentPane( createContentPane() );
		super.pack();
		super.setVisible( true );
	}
	
	/**
	 * For stand alone operation
	 * @param args the given command line arguments
	 */
	public static void main( final String[] args ) {	
		// setup dynamic layout
		Toolkit.getDefaultToolkit().setDynamicLayout( true );
		
		// set the system look and feel
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} 
		catch( final Exception e ) {
			logger.error( "Error setting system look and feel", e );
		}
		
		// start the launcher
		final CxLauncher launcher = new CxLauncher();
		launcher.run();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {

	}
	
	/**
	 * Creates the content pane
	 * @return the {@link JPanel content pane}
	 */
	private JComponent createContentPane() {
		final CxPanel cp = new CxPanel();
		cp.setBackground( new Color( 0xC0, 0xC0, 0xFF ) );
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		int row = 0;
		
		cp.attach( 0, row++, new LaunchDraftingAppButton() );
		cp.attach( 0, row++, new LaunchDataManagementModuleButton() );
		cp.attach( 0, row++, new LaunchRecordingViewerButton() );
		return cp;
	}
	
	/**
	 * Launch Drafting Module Button
	 * @author lawrence.daniels@gmail.com
	 */
	private class LaunchDraftingAppButton extends CxButton 
	implements ActionListener {
		
		/**
		 * Default Constructor
		 */
		public LaunchDraftingAppButton() {
			super( DRAFTING_ICON );
			super.setToolTipText( "Launch the Drafting Module" );
			super.addActionListener( this );
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the XML configuration file
			final File configFile = CxConfigurationUtil.getXMLConfigurationFile();
			
			// load the application
			logger.info( "Launching the Drafting Module..." );
			final Constellation app = new Constellation( configFile );
			app.execute();
		}
	}
	
	/**
	 * Launch Data Management Module Button
	 * @author lawrence.daniels@gmail.com
	 */
	private class LaunchDataManagementModuleButton extends CxButton 
	implements ActionListener {
		
		/**
		 * Default Constructor
		 */
		public LaunchDataManagementModuleButton() {
			super( DATA_MGMT_ICON );
			super.setToolTipText( "Launch the Data Management Module" );
			super.addActionListener( this );
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			logger.info( "Launching the Data Management Module..." );
			final DataManagement app = new DataManagement();
			app.init();
			app.execute();
		}
	}
	
	/**
	 * Launch Recording Viewer Button
	 * @author lawrence.daniels@gmail.com
	 */
	private class LaunchRecordingViewerButton extends CxButton 
	implements ActionListener {
		
		/**
		 * Default Constructor
		 */
		public LaunchRecordingViewerButton() {
			super( REC_VIEWER_ICON );
			super.setToolTipText( "Launch the Recording Viewer Module" );
			super.addActionListener( this );
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			logger.info( "Launching the Recording Viewer Module..." );
			final RecordingViewer app = new RecordingViewer();
			app.init();
		}
	}

}
