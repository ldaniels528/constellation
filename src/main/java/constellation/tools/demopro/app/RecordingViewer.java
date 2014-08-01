package constellation.tools.demopro.app;

import static java.awt.event.KeyEvent.VK_O;
import static java.lang.String.format;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.apache.log4j.Logger;

import constellation.CxContentManager;
import constellation.CxFontManager;
import constellation.commands.CxCommand;
import constellation.commands.CxCommandReader;
import constellation.commands.builtin.AddElementCommand;
import constellation.commands.builtin.ClearTempElementCommand;
import constellation.commands.builtin.DeleteElementCommand;
import constellation.commands.builtin.SetTempElementCommand;
import constellation.commands.builtin.SetTempElementHUDCommand;
import constellation.commands.builtin.WaitCommand;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.RenderableElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CommentXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.ui.components.CxPanel;
import constellation.ui.components.choosers.CxFileChooser;
import constellation.ui.components.menu.CxMenuItem;
import constellation.util.OSPlatformUtil;

/**
 * Constellation DemoPro Recording Viewer
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
@DemoProVersion("0.10")
public class RecordingViewer extends JFrame {
	private static final Logger logger = Logger.getLogger( RecordingViewer.class );
	private static final String VERSION = RecordingViewer.class.getAnnotation(DemoProVersion.class).value();
	
	// get the meta key for the host Operating System
	private static final int META_KEY = OSPlatformUtil.getMetaKey();
	
	// internal fields
	private final CxContentManager cxm = CxContentManager.getInstance();
	private MyCommandTree tree;
	private JTextArea messages;
	
	/**
	 * Default Constructor
	 */
	public RecordingViewer() {
		super( format( "Constellation Recording Viewer v%s", VERSION ) );
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setJMenuBar( new MyMenuBar() );
		super.setContentPane( new MyContentPane() );
		super.setAlwaysOnTop( false );
		super.pack();
		super.setVisible( true );
	}
	
	/** 
	 * For stand-alone operation
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
				
		// startup the debugger
		final RecordingViewer app = new RecordingViewer();
		app.init();
	}
	
	/**
	 * Initializes the application
	 */
	public void init() {
		// get the graphics context
		final Graphics g = super.getContentPane().getGraphics();
		
		// initialize the font manager
		CxFontManager.init( g );
	}
	
	/**
	 * Executes the debugger
	 * @param recordingFile the given recording {@link File file}
	 */
	public void loadRecording( final File recordingFile ) {
		CxCommandReader reader = null;
		InputStream in = null;

		try {
			// open the recording file
			in = new BufferedInputStream( new FileInputStream( recordingFile ), 8192 );
			
			// initialize the command reader
			reader = new CxCommandReader( in );
			
			// create the root tree node
			final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode( "ROOT" );
			
			// read the commands
			CxCommand command;
			int index = 0;
			while( ( in.available() > 0 ) && ( command = reader.read() ) != null ) {
				// attach the command to the tree
				attachCommand( rootNode, command );
				
				// set the message
				messages.append( format( "[%04d] %s\n", ++index, command ) );
			}
			
			// populate the tree
			tree.setModel( new DefaultTreeModel( rootNode ) );
			
			// close the command reader
			reader.close();
		}
		catch( final IOException e ) {
			logger.error( format( "Error debugging '%s'...", recordingFile.getName() ), e );
			JOptionPane.showMessageDialog( RecordingViewer.this, e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE );
		}
		finally {
			// close the reader
			if( reader != null ) {
				try { reader.close(); } catch( Exception e ) { }
			}
			
			// close the input stream
			else if( in != null ) {
				try { in.close(); } catch( Exception e ) { }
			}
		}
	}
	
	/** 
	 * Attaches the given command to the root node
	 * @param rootNode the root {@link DefaultMutableTreeNode node}
	 * @param command the given {@link CxCommand command}
	 */
	private void attachCommand( final DefaultMutableTreeNode rootNode, final CxCommand command ) {
		// create the new node
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode( command );
		rootNode.add( node );
		
		// is the command an "AddElementCommand"?
		if( command instanceof AddElementCommand ) {
			final AddElementCommand addCommand = (AddElementCommand)command;
			final EntityRepresentation entity = addCommand.getRepresentation();
			
			// attach the child node
			final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( entity );
			node.add( childNode );
		}
		
		// is the command an "SetTempElementHUDCommand"?
		else if( command instanceof SetTempElementHUDCommand ) {
			final SetTempElementHUDCommand setTempCmd = (SetTempElementHUDCommand)command;
			final HUDXY hud = setTempCmd.getHUD();
			
			// attach the child node
			final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( hud );
			node.add( childNode );
			
			// attach all of the HUD's children
			for( final RenderableElement element : hud.getElements() ) {
				childNode.add( new DefaultMutableTreeNode( element ) );
			}
		}
	}
	
	/**
	 * Recording File Viewer Content Pane
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyContentPane extends CxPanel {
		
		/**
		 * Default Constructor
		 */
		public MyContentPane() {
			// create the commands tree
			tree = new MyCommandTree();
			final JScrollPane treeSP = new JScrollPane( tree ); 
			treeSP.setPreferredSize( new Dimension( 200, 600 ) );
			
			// create the message display component
			messages = new JTextArea();
			final JScrollPane messagesSP = new JScrollPane( messages ); 
			messagesSP.setPreferredSize( new Dimension( 600, 600 ) );
			
			// create a split pane
			final JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
			splitPane.add( treeSP );
			splitPane.add( messagesSP );
			
			// attach the components
			super.gbc.anchor = GridBagConstraints.NORTHWEST;
			super.gbc.fill	 = GridBagConstraints.BOTH;
			super.attach( 0, 0, splitPane, GridBagConstraints.WEST );
		}
	}
	
	/**
	 * Recording File Viewer Command Tree
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyCommandTree extends JTree {
		
		/**
		 * Default Constructor
		 */
		public MyCommandTree() {
			super( new DefaultMutableTreeNode( "ROOT" ) );
			super.setCellRenderer( new MyTreeCellRenderer( super.getCellRenderer() ) );
		}
	}
	
	/**
	 * Recording File Viewer Command Tree Cell Renderer
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyTreeCellRenderer implements TreeCellRenderer {
		private final Map<Class<? extends CxCommand>, Icon> commandIcons;
		private final Map<Class<? extends RenderableElement>, Icon> entityIcons;
		private final Icon ROOT_ICON 	= cxm.getIcon( "images/extensions/demopro/tape.png" ); 
		private final Icon COMMAND_ICON = cxm.getIcon( "images/extensions/demopro/commands/command.png" ); 
		private final Icon ENTITY_ICON 	= cxm.getIcon( "images/extensions/demopro/commands/command.png" ); 
		private final TreeCellRenderer renderer;
		
		/**
		 * Creates a new tree cell render
		 * @param renderer the given default system {@link TreeCellRenderer render}
		 */
		public MyTreeCellRenderer( final TreeCellRenderer renderer ) {
			this.renderer		= renderer;
			this.commandIcons	= createCommandToIconMapping();
			this.entityIcons	= createEntityToIconMapping();
		}

		/** 
		 * {@inheritDoc}
		 */
		public Component getTreeCellRendererComponent( final JTree tree, 
													   final Object value,
													   final boolean selected, 
													   final boolean expanded, 
													   final boolean leaf, 
													   final int row,
													   final boolean hasFocus ) {
			// is it a tree node?
			if( value instanceof DefaultMutableTreeNode ) {
				// cast to a tree node
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				
				// get the user object
				final Object userObject = node.getUserObject();
				
				// get the corresponding component
				final JComponent comp = getComponent( userObject );
				if( comp != null ) {
					return comp;
				}
			}
		
			// allow the parent to handle it
			return renderer.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
		}

		/**
		 * Builds a mapping of commands to icons
		 * @return a mapping of commands to icons
		 */
		private Map<Class<? extends CxCommand>, Icon> createCommandToIconMapping() {
			final Map<Class<? extends CxCommand>, Icon> mapping = new HashMap<Class<? extends CxCommand>, Icon>();
			mapping.put( AddElementCommand.class, 		cxm.getIcon( "images/extensions/demopro/commands/add.png" ) );
			mapping.put( DeleteElementCommand.class, 	cxm.getIcon( "images/extensions/demopro/commands/delete.png" ) );
			mapping.put( ClearTempElementCommand.class, cxm.getIcon( "images/extensions/demopro/commands/tempElem.png" ) );
			mapping.put( SetTempElementCommand.class, 	cxm.getIcon( "images/extensions/demopro/commands/tempElem.png" ) );
			mapping.put( SetTempElementHUDCommand.class,cxm.getIcon( "images/extensions/demopro/commands/tempElem.png" ) );
			mapping.put( WaitCommand.class, 			cxm.getIcon( "images/extensions/demopro/commands/clock.png" ) );
			return mapping;
		}
		
		/**
		 * Builds a mapping of rendering elements to icons
		 * @return a mapping of rendering elements to icons
		 */
		private Map<Class<? extends RenderableElement>, Icon> createEntityToIconMapping() {
			final Map<Class<? extends RenderableElement>, Icon> mapping = new HashMap<Class<? extends RenderableElement>, Icon>();
			mapping.put( CircleXY.class, 	cxm.getIcon( "images/extensions/demopro/entities/circle.png" ) );
			mapping.put( CommentXY.class, 	cxm.getIcon( "images/extensions/demopro/entities/comment.png" ) );
			mapping.put( EllipseXY.class, 	cxm.getIcon( "images/extensions/demopro/entities/ellipse.png" ) );
			mapping.put( HUDXY.class, 		cxm.getIcon( "images/extensions/demopro/entities/hud.png" ) );
			mapping.put( LineXY.class, 		cxm.getIcon( "images/extensions/demopro/entities/line.png" ) );
			mapping.put( PointXY.class, 	cxm.getIcon( "images/extensions/demopro/entities/point.png" ) );
			mapping.put( TextNoteXY.class, 	cxm.getIcon( "images/extensions/demopro/entities/textnote.png" ) );
			return mapping;
		}

		/**
		 * Returns the name of the given command
		 * @param command the given {@link CxCommand command}
		 * @return the name of the command
		 */
		private String getCommandName( final CxCommand command ) {
			final String fullName = command.getClass().getSimpleName();
			
			final int index = fullName.indexOf( "Command" );
			return ( index != -1 ) ? fullName.substring( 0, index ) : fullName;
		}
		
		/**
		 * Returns the name of the given entity
		 * @param entity the given {@link RenderableElement entity}
		 * @return the name of the command
		 */
		private String getEntityName( final RenderableElement entity ) {
			final String fullName = entity.getClass().getSimpleName();
			
			final int index = fullName.indexOf( "XY" );
			return ( index != -1 ) ? fullName.substring( 0, index ) : fullName;
		}
		
		/** 
		 * Returns the component that corresponds to the given user object
		 * @param userObject the given user object
		 * @return the {@link JComponent component}
		 */
		private JComponent getComponent( final Object userObject ) {
			// is the user object a command?
			if( userObject instanceof CxCommand ) {
				// cast to a command
				final CxCommand command = (CxCommand)userObject;
				
				// lookup the icon
				final Icon icon = commandIcons.get( command.getClass() );
				if( icon != null ) {
					return new JLabel( getCommandName( command ), icon, JLabel.TRAILING );
				}
				else {
					// return a generic command
					return new JLabel( getCommandName( command ), COMMAND_ICON, JLabel.TRAILING );
				}
			}
			
			// is the user object a tree node?
			else if( userObject instanceof DefaultMutableTreeNode ) {
				// cast to a tree node
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)userObject;
				
				// get the corresponding component
				final JComponent comp = getComponent( node.getUserObject() );
				if( comp != null ) {
					return comp;
				}
			}
			
			// is the user object a entity representation?
			else if( userObject instanceof RenderableElement ) {
				// cast to an entity
				final RenderableElement entity = (RenderableElement)userObject;
				
				// lookup the icon
				final Icon icon = entityIcons.get( entity.getClass() );
				if( icon != null ) {
					return new JLabel( getEntityName( entity ), icon, JLabel.TRAILING );
				}
				else {
					// return a generic command
					return new JLabel( getEntityName( entity ), ENTITY_ICON, JLabel.TRAILING );
				}
			}
			
			// is it a string?
			else if( userObject instanceof String ) {
				// is it the "ROOT" object
				if( "ROOT".equals( userObject ) ) {
					return new JLabel( "Root", ROOT_ICON, JLabel.TRAILING );
				}
			}
			
			return null;
		}
		
	}
	
	/**
	 * Recording File Viewer Menu Bar
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyMenuBar extends JMenuBar {
		
		/**
		 * Default Constructor
		 */
		public MyMenuBar() {
			super.add( new FileMenu() );
		}
	}
	
	/**
	 * File Menu
	 * @author lawrence.daniels@gmail.com
	 */
	private class FileMenu extends JMenu {
		
		/**
		 * Default Constructor
		 */
		public FileMenu() {
			super( "File" );
			super.add( new CxMenuItem( "Open", cxm.getIcon( "images/extensions/demopro/load.png" ), getKeyStroke( VK_O, META_KEY ), new FileOpenAction() ) );
		}	
	}
	
	/**
	 * File::Open Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class FileOpenAction implements ActionListener {

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// create a file chooser instance
			final JFileChooser chooser = createOpenFileChooser( getRecordingsDirectory() );
			
			// open the file dialog
			final int returnVal = chooser.showOpenDialog( (Component)event.getSource() );
		    if( returnVal == JFileChooser.APPROVE_OPTION ) {
		    	final File chosenFile = chooser.getSelectedFile();
		    	
		    	// load the file
		    	loadRecording( chosenFile );
		    }
		}
		
		/**
		 * Returns the "recordings" directory path
		 * @return the "recordings" directory path
		 */
		private File getRecordingsDirectory() {
			// determine the path
			final String path = format( "%s/Constellation/recordings", System.getProperty( "user.home", "." ) );
			
			// return the file reference
			return new File( path );
		}
		
		/**
		 * Creates an "Open" file chooser instance
		 * @param parentDirectory the given {@link File parent directory}
		 * @return the {@link CxFileChooser file chooser}
		 */
		private JFileChooser createOpenFileChooser( final File parentDirectory ) {
			// create a file chooser instance
			final JFileChooser chooser = new CxFileChooser( parentDirectory );
			chooser.addChoosableFileFilter( new FileFilter() {

				/** 
				 * {@inheritDoc}
				 */
				@Override
				public boolean accept( final File file ) {
					return file.isDirectory() || file.getName().toLowerCase().endsWith( ".recording" );
				}

				/** 
				 * {@inheritDoc}
				 */
				@Override
				public String getDescription() {
					return "Constellation Recording";
				} 
				
			} );
			
			return chooser;
		}
		
	}

}
