package constellation.tools.demopro.plugin;

import static constellation.ui.components.buttons.CxButton.createBorderlessButton;
import static java.io.File.separator;
import static java.lang.String.format;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;

import constellation.ApplicationController;
import constellation.CxConfigurationUtil;
import constellation.CxContentManager;
import constellation.PluginManager;
import constellation.commands.VirtualModel;
import constellation.model.GeometricModel;
import constellation.tools.demopro.PlaybackModel;
import constellation.tools.demopro.RecordingModel;
import constellation.ui.components.CxPlugIn;
import constellation.ui.components.buttons.CxIconToggleButton;
import constellation.ui.components.choosers.CxFileChooser;
import constellation.ui.components.fields.CxTimerField;

/**
 * Constellation DemoPro Recording/Playback Plug-in
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class RecordingPlugin extends CxPlugIn {
	// record status enumerations
	private static enum RecorderStatus { STOPPED, RECORDING, PLAYBACK, PAUSED };
	
	// record status to status description mapping
	private static Map<RecorderStatus, String> STATUS_MAPPING = createRecorderStatusMapping();
	
	// singleton instance
	private static RecordingPlugin instance = null;
	
	// default recording name
	private static final String DEFAULT_RECORDING_NAME	= "Untitled.recording";
	
	// get the content manager instance
	private final CxContentManager contentManager = CxContentManager.getInstance();
	
	// icon declarations
	private final Icon LOAD_ICON	= contentManager.getIcon( "images/extensions/demopro/load.png" );
	private final Icon SAVE_ICON	= contentManager.getIcon( "images/extensions/demopro/save.png" );
	private final Icon PAUSE_ICON	= contentManager.getIcon( "images/extensions/demopro/pause.png" );
	private final Icon PLAY_ICON	= contentManager.getIcon( "images/extensions/demopro/play.png" );
	private final Icon RECORD_ICON	= contentManager.getIcon( "images/extensions/demopro/record.png" );	
	private final Icon SESSION_ICON	= contentManager.getIcon( "images/extensions/demopro/tape.png" );
	private final Icon STOP_ICON	= contentManager.getIcon( "images/extensions/demopro/stop.png" );
	private final Icon QUIT_ICON	= contentManager.getIcon( "images/extensions/demopro/eject.png" );
	
	// internal fields
	private final ApplicationController controller;
	private final CxIconToggleButton playButton;
	private RecorderStatus status;
	private File recordingFile;
	private StatusLine statusLine;
	private CxTimerField timerF;
	
	/**
	 * Creates a new Recording Plug-in instance
	 * @param controller the given {@link ApplicationController controller}
	 */
	private RecordingPlugin( final ApplicationController controller ) {
		this.controller = controller;
		
		// attach the components
		int col = -1; 
		super.gbc.anchor = GridBagConstraints.CENTER;
		super.attach( ++col, 0, statusLine = new StatusLine() );
		super.attach( ++col, 0, createBorderlessButton( STOP_ICON, new StopPlaybackOrRecordingAction(), "Stop playback/recording" ) );
		super.attach( ++col, 0, playButton = new PlaybackButton() );
		super.attach( ++col, 0, createBorderlessButton( RECORD_ICON, new StartRecordingAction(), "Start recording" ) );
		super.attach( ++col, 0, timerF = new CxTimerField() ); 
		super.attach( ++col, 0, createBorderlessButton( LOAD_ICON, new OpenSessionAction(), "Open recording" ) );
		super.attach( ++col, 0, createBorderlessButton( SAVE_ICON, new SaveSessionAction(), "Save recording" ) );
		super.attach( ++col, 0, createBorderlessButton( QUIT_ICON, new QuitSessionAction(), "Eject this plugin" ), GridBagConstraints.SOUTHEAST );
		
		// set the default recording file
		recordingFile = new File( format( "%s%s%s", 
								  CxConfigurationUtil.getRecordingsDirectory().getAbsolutePath(), 
								  separator, DEFAULT_RECORDING_NAME ) );
		
		// set the default status
		setStatus( RecorderStatus.STOPPED );
	}
	
	/**
	 * Returns the singleton instance 
	 * @param controller the given {@link ApplicationController controller}
	 * @return the singleton instance 
	 */
	public static RecordingPlugin getInstance( final ApplicationController controller ) {
		// instantiate the singleton
		if( instance == null ) {
			instance = new RecordingPlugin( controller );
		}		
		return instance;
	}
	
	/**
	 * Creates an "Open" file chooser instance
	 * @param parentDirectory the given {@link File parent directory}
	 * @return the {@link CxFileChooser file chooser}
	 */
	private CxFileChooser createOpenFileChooser( final File parentDirectory ) {
		// create a file chooser instance
		final CxFileChooser chooser = new CxFileChooser( parentDirectory );

		// create the file filter
		final RecordingFileFilter fileFilter = new RecordingFileFilter();

		// add the filter
		chooser.addChoosableFileFilter( fileFilter );
		
		// preselect the file filter
		chooser.setFileFilter( fileFilter );
		return chooser;
	}
	
	/** 
	 * Sets the current recorder status
	 * @param status the given {@link RecorderStatus recorder status}
	 */
	private void setStatus( final RecorderStatus status ) {
		// set the status
		this.status = status;
		
		// update the status line
		updateStatusLine();
	}
	
	/**
	 * Updates the status line
	 */
	private void updateStatusLine() {
		// get the file name without the extension
		String fileName = recordingFile.getName();
		if( fileName.contains( "." ) ) {
			fileName = fileName.substring( 0, fileName.indexOf( '.' ) );
		}
		
		// set the status line
		statusLine.setToolTipText( format( "Recording '%s' is %s", fileName, STATUS_MAPPING.get( status ) ) );
	}
	
	/**
	 * Stops any current play-back or recording
	 */
	public void stopPlaybackOrRecording() {
		// if recorder is in play-back or recording mode, stop it
		if( status != RecorderStatus.STOPPED ) {
			// stop the timer
			timerF.stop();
			timerF.reset();
			
			// switch back to the standard model
			switchToStandardModel();
			
			// update the play button
			playButton.setSelected( false );
			playButton.updateIcon();
			
			// set the status to stopped
			setStatus( RecorderStatus.STOPPED );
		}
	}

	/**
	 * Switches the current controller's virtual model to original host model
	 */
	private void switchToStandardModel() {
		// get the host model
		final GeometricModel model = controller.getModel();
		
		// get the host model
		if( model instanceof VirtualModel ) {
			// cast to a virtual model
			final VirtualModel virtualModel = (VirtualModel)model;
			
			// close the virtual model
			virtualModel.close();
			
			// get the host model
			final GeometricModel hostModel = virtualModel.getHostModel();
			
			// switch the controller's model
			controller.setModel( hostModel );
		}
	}

	/** 
	 * Creates a record status mapping
	 * @return a {@link RecorderStatus record status} mapping
	 */
	private static Map<RecorderStatus, String> createRecorderStatusMapping() {
		final Map<RecorderStatus, String> map = new HashMap<RecorderStatus, String>();
		map.put( RecorderStatus.PAUSED, 	"Paused" );
		map.put( RecorderStatus.PLAYBACK,	"Playing" );
		map.put( RecorderStatus.RECORDING, 	"Recording" );
		map.put( RecorderStatus.STOPPED, 	"Stopped" );
		return map;
	}
	
	/**
	 * Play-back Button
	 * @author lawrence.daniels@gmail.com
	 */
	private class PlaybackButton extends CxIconToggleButton {
		
		/**
		 * Default Constructor
		 */
		public PlaybackButton() {
			super( PAUSE_ICON, PLAY_ICON, new PlaybackAction(), "Pause Playback", "Start Playback" );
		}
	}
	
	/**
	 * Status Line Component
	 * @author lawrence.daniels@gmail.com
	 */
	private class StatusLine extends JLabel {
		
		/**
		 * Default Constructor
		 */
		public StatusLine() {
			super( SESSION_ICON );
			super.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		}
	}
	
	/**
	 * Start Recording Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class StartRecordingAction implements ActionListener {

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			if( status == RecorderStatus.STOPPED ) {				
				try {
					// get the host model
					final GeometricModel hostModel = controller.getModel();
					
					// create the recording model
					final RecordingModel model = new RecordingModel( hostModel, new FileOutputStream( recordingFile ) );
					
					// swap the controller's model instance
					controller.setModel( model );
					
					// set the recorder status
					setStatus( RecorderStatus.RECORDING );
					
					// start the timer
					timerF.start();
					
					
				}
				catch( final IOException cause ) {
					// set status back to stopped
					setStatus( RecorderStatus.STOPPED );
					
					// report the error
					controller.showErrorDialog( "Recording Error", cause );
				}
			}
		}
	}
	
	/**
	 * Stop Playback/Recording Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class StopPlaybackOrRecordingAction implements ActionListener {

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			stopPlaybackOrRecording();
		}
	}
	
	/**
	 * Play-back Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class PlaybackAction implements ActionListener {

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			switch( status ) {
				// if playing, pause it
				case PLAYBACK:
					// set the recorder status
					// setStatus( RecorderStatus.PAUSED );
					// TODO pause?
					break;
					
				// if stopped, start it
				case STOPPED:				
					try {
						// update the toggle button
						final CxIconToggleButton button = (CxIconToggleButton)event.getSource();
						button.updateIcon();
						
						// get the host model
						final GeometricModel hostModel = controller.getModel();
						
						// open the output stream
						final InputStream in = new FileInputStream( recordingFile );
						
						// create the play-back model
						final PlaybackModel model = new PlaybackModel( hostModel, controller, RecordingPlugin.this, in );
						
						// swap the controller's model instance
						controller.setModel( model );
						
						// set the recorder status
						setStatus( RecorderStatus.PLAYBACK );
						
						// start the timer
						timerF.start();
					}
					catch( final IOException cause ) {
						// set status back to stopped
						stopPlaybackOrRecording();
						
						// report the error
						controller.showErrorDialog( "Playback Error", cause );
					}
					break;
			}
		}
	}
	
	/**
	 * Open Session Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class OpenSessionAction implements ActionListener {

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the parent directory
			final File parentDirectory = recordingFile.getParentFile();
			
			// create a file chooser instance
			final JFileChooser chooser = createOpenFileChooser( parentDirectory );
			
			// open the file dialog
			final int returnVal = chooser.showOpenDialog( (Component)event.getSource() );
		    if( returnVal == JFileChooser.APPROVE_OPTION ) {
		    	final File chosenFile = chooser.getSelectedFile();
		    	if( chosenFile != null ) {
		    		// set the recording file
		    		recordingFile = ( chosenFile.getName().contains( ".") ) 
		    							? chosenFile 
		    							: new File( format( "%s.recording", chosenFile.getAbsolutePath() ) );
		    		
		    		// update the status line
		    		updateStatusLine();
		    	}
		    }
		}
	}
		
	/**
	 * Save Session Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class SaveSessionAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the parent directory
			final File parentDirectory = recordingFile.getParentFile();
			
			// create a file chooser instance
			final JFileChooser chooser = createOpenFileChooser( parentDirectory );
			
			// open the file dialog
			final int returnVal = chooser.showSaveDialog( (Component)event.getSource() );
		    if( returnVal == JFileChooser.APPROVE_OPTION ) {
		    	final File chosenFile = chooser.getSelectedFile();
		    	if( chosenFile != null ) {
		    		// set the recording file
		    		recordingFile = ( chosenFile.getName().contains( ".") ) 
		    							? chosenFile 
		    							: new File( format( "%s.recording", chosenFile.getAbsolutePath() ) );
		    		
		    		// update the status line
		    		updateStatusLine();
		    	}
		    }
		}
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
			// stop play-back or recording
			stopPlaybackOrRecording();
			
			// unload the plugin
			final PluginManager pluginManager = controller.getPluginManager();
			pluginManager.unloadPlugin();
		}
	}

	/** 
	 * Recording File Filter
	 * @author lawrence.daniels@gmail.com
	 */
	private class RecordingFileFilter extends FileFilter {
		
		/** 
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept( final File file ) {
			return file.getName().toLowerCase().endsWith( ".recording" );
		}

		/** 
		 * {@inheritDoc}
		 */
		@Override
		public String getDescription() {
			return "Recording files";
		}
	}
	
}