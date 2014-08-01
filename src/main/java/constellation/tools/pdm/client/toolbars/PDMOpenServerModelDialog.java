package constellation.tools.pdm.client.toolbars;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ThreadPool;
import constellation.tools.pdm.client.PDMModelFile;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;

/**
 * Constellation Open Server Model Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PDMOpenServerModelDialog extends CxDialog {
	private static final String[] COLUMN_NAMES = {
		"Name", "Last Modified Date", "Last Modified By", "Status"
	};
	
	// the singleton instance
	private static PDMOpenServerModelDialog instance;
	
	// internal fields
	private final DateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yyyy hh:mma" );
	@SuppressWarnings("unused")private final Logger logger = Logger.getLogger( getClass() );
	@SuppressWarnings("unused")private final CxContentManager contentManager = CxContentManager.getInstance();
	@SuppressWarnings("unused")private final ThreadPool threadPool;
	private final DataTable table;
	
	/** 
	 * Creates a new open server model dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	private PDMOpenServerModelDialog( final ApplicationController controller ) {
		super( controller, "Open Server Model" );
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.threadPool	= controller.getThreadPool();
		
		// construct the data table
		table = new DataTable();
		
		// set the content pane
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Returns the singleton instance of the open server model dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static PDMOpenServerModelDialog getInstance( final ApplicationController controller ) {
		// if the dialog instance has not already been instantiated
		if( instance == null ) {
			// instantiate an instance of the dialog
			instance = new PDMOpenServerModelDialog( controller );
		}
		return instance;
	}
	
	/**
	 * Updates the table with the given model file list
	 * @param files the given {@link PDMModelFile model file list}
	 */
	public void update( final List<PDMModelFile> files ) {
		table.update( files );
		table.updateUI();
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link CxPanel pane}
	 */
	private CxPanel createContentPane() {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 6, 6, 6, 6 );
		int row = -1;
		
		// row #2
		cp.gbc.fill = GridBagConstraints.NONE;
		cp.attach( 0, ++row, new JLabel( "Model Search") );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, new JTextField( 20 ) );
		cp.gbc.gridwidth = 1;
		cp.attach( 3,   row, new JButton( "Search" ) );
	
		// row #1
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.gbc.gridwidth = 4;
		cp.attach( 0, ++row, new JScrollPane( table ) );
		cp.gbc.gridwidth = 1;
		
		// row #2
		cp.gbc.fill = GridBagConstraints.NONE;
		cp.attach( 0, ++row, new JButton( "CheckIn") );
		cp.attach( 1,   row, new JButton( "CheckOut") );
		cp.attach( 2,   row, new JButton( "Load") );
		cp.attach( 3,   row, new JButton( "Refresh") );
		return cp;
	}
	
	/**
	 * Represents the underlying table which will
	 * contain the data.
	 * @author lawrence.daniels@gmail.com
	 */
	private class DataTable extends JTable {
		
		/**
		 * Default Constructor
		 */
		public DataTable() {
			super( new Object[0][COLUMN_NAMES.length], COLUMN_NAMES );
		}
		
		/**
		 * Updates the table with the given model file list
		 * @param files the given {@link PDMModelFile model file list}
		 */
		public void update( final List<PDMModelFile> files ) {
			final Object[][] data = new Object[files.size()][COLUMN_NAMES.length];
			int n = 0;
			for( final PDMModelFile file : files ) {
				data[n][0] = file.getName(); 
				data[n][1] = DATE_FORMAT.format( file.getLastModifiedTime() ); 
				data[n][2] = file.getLastModifiedBy();  
				data[n][3] = file.getStatus();
				n++;
			}
			super.setModel( new DefaultTableModel( data, COLUMN_NAMES ) );
		}	
	}
	
}