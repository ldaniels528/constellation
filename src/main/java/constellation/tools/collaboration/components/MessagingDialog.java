package constellation.tools.collaboration.components;

import static java.lang.String.format;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ThreadPool;
import constellation.model.GeometricModel;
import constellation.tools.collaboration.RemoteGeometricModel;
import constellation.tools.collaboration.commands.RemoteMessagingDialog;
import constellation.tools.collaboration.commands.SendMessageCommand;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;

/**
 * Constellation Messaging Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class MessagingDialog extends CxDialog implements RemoteMessagingDialog {
	// internal fields
	private final Logger logger = Logger.getLogger( getClass() );
	private final CxContentManager contentManager = CxContentManager.getInstance();
	private final HtmlOutputPane outputPane;
	private final JTextComponent inputPane;
	private final ThreadPool threadPool;
	private final JLabel statusLabel;
	private CxButton button;
	
	/** 
	 * Creates a new message session dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	public MessagingDialog( final ApplicationController controller ) {
		super( controller, "Messaging" );
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.threadPool	= controller.getThreadPool();
		
		// create the input text field
		inputPane = new JTextField( 40 );
		
		// create the output text field
		outputPane = new HtmlOutputPane(); 
		
		// create the status label
		statusLabel = new JLabel( " " );
		
		// create the 'Send' button
		button = new CxButton( "Send", new SendMessageAction() );
		button.setDefaultCapable( true );
		
		// set the content pane
		super.setContentPane( createContentPane() );
		super.getRootPane().setDefaultButton( button );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.CxMessagingAgent#appendMessage(java.lang.String)
	 */
	public void appendMessage( final String message ) {
		logger.info( format( "Appending '%s'...", message ) );
		synchronized( outputPane ) {
			outputPane.append( format( "%s\n", message ) );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.CxMessagingAgent#makeVisible()
	 */
	public void makeVisible() {
		if( !super.isShowing() ) {
			super.setVisible( true );
		}
		super.requestFocusInWindow();
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link CxPanel pane}
	 */
	private CxPanel createContentPane() {
		final CxPanel cp = new CxPanel();
		int row = -1;
		
		// row #1
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.gbc.insets = new Insets( 6, 6, 6, 6 );
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, new JScrollPane( outputPane ) );
		cp.gbc.gridwidth = 1;
		
		// row #2
		cp.gbc.fill = GridBagConstraints.HORIZONTAL;
		cp.attach( 0, ++row, inputPane );
		cp.attach( 1,   row, button );
		
		// row #3
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, statusLabel );
		return cp;
	}
	
	/**
	 * Represents a text pane that supports embedding images
	 * @author lawrence.daniels@gmail.com
	 */
	private class HtmlOutputPane extends JEditorPane {
		private final LinkedList<String> messages;
		private final Map<String,String> specials;
		
		/**
		 * Default constructor
		 */
		public HtmlOutputPane() {
			this.messages	= new LinkedList<String>();
			this.specials	= createSpecialSequenceMapping();
			super.setPreferredSize( new Dimension( 320, 200 ) );
			super.setEditorKit( new HTMLEditorKit() );
			super.setEditable( false );
		}
		
		/**
		 * Appends the given message to the pane
		 * @param message the given display to display
		 */
		public void append( final String message ) {
			// add the text to messages list
			messages.add( toHTML( message ) );
			
			// maintain a maximum of 25 messages
			if( messages.size() > 25 ) {
				messages.removeFirst();
			}
			
			// update the component with the combined text
			super.setText( getHTMLContent() );
		}
		
		/**
		 * Returns the path to the referenced resource
		 * @param resourcePath the given resource path
		 * @return the path to the referenced resource
		 */
		private String getImageURL( final String resourcePath ) {
			// determine the image's relative path
			final String imagePath = format( "images/smilies/%s", resourcePath );
			
			// attempt to get the image's real path from the content manager
			try {
				final String realPath = contentManager.getImagePath( imagePath );
				return format( "<img src='%s'>", realPath );
			} 
			catch( final RuntimeException e ) {
				logger.error( format( "Image path '%s' not found", imagePath ), e );
				return format( "<img src='' alt='%s'>", resourcePath );
			}
		}
		
		/**
		 * Creates the mapping of special sequences
		 * @return the mapping of special sequences
		 */
		private Map<String, String> createSpecialSequenceMapping() {
			final Map<String,String> map = new LinkedHashMap<String, String>();
			map.put( "(->)", 	getImageURL( "icon_arrow.gif" ) );
			map.put( ">:-D", 	getImageURL( "icon_twisted.gif" ) );
			map.put( ":-D", 	getImageURL( "icon_biggrin.gif" ) );
			map.put( "B-)", 	getImageURL( "icon_cool.gif" ) );
			map.put( ":-/", 	getImageURL( "icon_confused.gif" ) );
			map.put( "(!)", 	getImageURL( "icon_exclaim.gif" ) );
			map.put( "(*)", 	getImageURL( "icon_idea.gif" ) );
			map.put( ":-))", 	getImageURL( "icon_lol.gif" ) );
			map.put( ":-Q", 	getImageURL( "icon_mad.gif" ) );
			map.put( "|-D", 	getImageURL( "icon_mrgreen.gif" ) );
			map.put( ":-|", 	getImageURL( "icon_neutral.gif" ) );
			map.put( "(?)", 	getImageURL( "icon_question.gif" ) );
			map.put( ":->", 	getImageURL( "icon_razz.gif" ) );
			map.put( "@-|",		getImageURL( "icon_rolleyes.gif" ) );
			map.put( ":-(", 	getImageURL( "icon_sad.gif" ) );
			map.put( ":-)", 	getImageURL( "icon_smile.gif" ) );
			map.put( ":-O", 	getImageURL( "icon_surprised.gif" ) );
			map.put( ";-)", 	getImageURL( "icon_wink.gif" ) );
			return map;
		}

		/**
		 * Reformats the given message as HTML text 
		 * @param message the given message
		 * @return the HTML text
		 */
		private String toHTML( final String message ) {
			final StringBuilder sb = new StringBuilder( 255 );
			sb.append( "<div>" ).append( message ).append( "</div>");
			
			// convert special sequences
			int index;
			for( final String key : specials.keySet() ) {
				final String value = specials.get( key );
				while( ( index = sb.indexOf( key ) ) != -1 ) {
					sb.replace( index, index + key.length(), value );
				}
			}
			return sb.toString();
		}
		
		/**
		 * Returns the text messages as HTML
		 * @return the HTML content
		 */
		private String getHTMLContent() {
			final StringBuilder sb = new StringBuilder( 8192 );
			sb.append( "<html><body>" );
			for( final String message : messages ) {
				sb.append( message );
			}
			sb.append( "</body></html>");
			return sb.toString();
		}
	}

	/** 
	 * Represents a Send Message Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class SendMessageAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the message from the input pane
			final String message = inputPane.getText();
			inputPane.setText( "" );
			
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// if the model is collaborative ...
			if( model.isVirtual() ) {
				// get the client ID
				final String clientID = ((RemoteGeometricModel)model).getClientID();
				
				// append the message to the output pane
				appendMessage( format( "[%s] %s", clientID, message ) );
				
				// transmit the message
				threadPool.queue( new SendMessageTask( message ) );
			}
		}
	}
	
	/** 
	 * Represents a Send Message Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class SendMessageTask implements Runnable {
		private final String message;
		
		/** 
		 * Creates a new Send Message Task
		 * @param message the given message
		 */
		public SendMessageTask( final String message ) {
			this.message = message;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// get the collaborative model
			if( model.isVirtual() ) {
				final RemoteGeometricModel collaborativeModel = (RemoteGeometricModel)model;
				collaborativeModel.queue( SendMessageCommand.create( message ) );
			}
		}
	}
	
}
