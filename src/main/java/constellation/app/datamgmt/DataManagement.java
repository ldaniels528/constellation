package constellation.app.datamgmt;

import static java.awt.Color.*;
import static java.lang.String.format;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import constellation.app.CxVersion;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxLabel;
import constellation.ui.components.fields.CxTextArea;

/**
 * Constellation Data Management Module 
 * @author lawrence.daniels@gmail.com
 */
@CxVersion("0.01")
@SuppressWarnings("serial")
public class DataManagement extends JFrame {
	private static final Logger logger = Logger.getLogger( DataManagement.class );
	private static final String VERSION = DataManagement.class.getAnnotation(CxVersion.class).value();
	
	/**
	 * Default Constructor
	 */
	public DataManagement() {
		super( format( "Constellation Data Management v%s", VERSION ) );
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setContentPane( createContentPane() );
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
		final DataManagement app = new DataManagement();
		app.init();
	}
	
	/**
	 * Initializes the application
	 */
	public void init() {
		
	}
	
	public void execute() {
		
	}
	
	/**
	 * Creates the content pane
	 * @return the {@link JPanel content pane}
	 */
	private JComponent createContentPane() {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 4, 4, 4, 4 );
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.fill	  = GridBagConstraints.BOTH;
		cp.setBorder( BorderFactory.createTitledBorder( "Specifications" ) );
		cp.gbc.weightx = 2;
		cp.gbc.weighty = 2;
		
		// row #1
		cp.attach( 0, 0, createContentPane_SewingInstructions() );
		cp.attach( 1, 0, createContentPane_OrderInfo() );
		cp.attach( 2, 0, createContentPane_Material() );
		
		// row #2
		cp.gbc.gridwidth = 3;
		cp.attach( 0, 1, createContentPane_PointOfMeasurement() );
		cp.gbc.gridwidth = 1;
		return cp;
	}
	
	/**
	 * Creates the 'Sewing Instructions' pane
	 * @return the {@link JPanel content pane}
	 */
	private JComponent createContentPane_SewingInstructions() {
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.fill	  = GridBagConstraints.BOTH;
		cp.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( 
						BorderFactory.createLineBorder( BLACK ),
						"Sewing Instructions" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 )
		) );
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		int row = 0;
		
		// row #1
		//cp.gbc.gridwidth = 2;
		//cp.attach( 0, row++, new CxLabel( "Sewing Instructions", true ) );
		//cp.gbc.gridwidth = 1;
		
		// row #2
		final CxTextArea textArea = new CxTextArea();
		cp.attach( 0, row++, new JScrollPane( textArea ), GridBagConstraints.NORTHWEST  );
		
		// set the preliminary text
		textArea.setText( format( "Thread: Tex 105 BLACK\nContrast: Black Leather\n" ) );
		return cp;
	}
	
	/**
	 * Creates the 'Order Information' pane
	 * @return the {@link JPanel content pane}
	 */
	private JComponent createContentPane_OrderInfo() {
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.fill	  = GridBagConstraints.BOTH;
		cp.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( 
						BorderFactory.createLineBorder( BLACK ),
						"Order Information" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 )
		) );
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		int row = 0;
		
		// row #1
		cp.attach( 0, row, 	 new CxLabel( "Customer Name", true ) );
		cp.attach( 1, row++, new CxLabel( "N.B." ) );
		
		// row #2
		cp.attach( 0, row, 	 new CxLabel( "Order #", true ) );
		cp.attach( 1, row++, new CxLabel( "2002" ) );
		
		// row #3
		cp.attach( 0, row, 	 new CxLabel( "Size", true ) );
		cp.attach( 1, row++, new CxLabel( "Custom" ) );
		
		// row #4
		cp.attach( 0, row, 	 new CxLabel( "Date", true ) );
		cp.attach( 1, row++, new CxLabel( "11/04/09" ), GridBagConstraints.NORTHWEST );
		return cp;
	}
	
	/**
	 * Creates the 'Materials' pane
	 * @return the {@link JPanel content pane}
	 */
	private JComponent createContentPane_Material() {
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.fill	  = GridBagConstraints.VERTICAL;
		cp.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( 
						BorderFactory.createLineBorder( BLACK ),
						"Material Information" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 )
		) );
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		int row = 0;
		
		// row #1
		cp.attach( 0, row, 	 new CxLabel( "Fabric", true ) );
		cp.attach( 1, row++, new CxLabel( "EFYI - 2A 3.5yd" ) );
		
		// row #2
		cp.attach( 0, row, 	 new CxLabel( "Wash", true ) );
		cp.attach( 1, row++, new JComboBox( new String[] { "Rinse Wash" } ) );
		
		// row #3
		cp.attach( 0, row, 	 new CxLabel( "Length Shrink", true ) );
		cp.attach( 1, row++, new CxLabel( "0.5" ) );
		
		// row #4
		cp.attach( 0, row, 	 new CxLabel( "Width Shrink", true ) );
		cp.attach( 1, row++, new CxLabel( "0.5" ) );
		
		// row #4
		cp.attach( 0, row, 	 new CxLabel( "Back Pocket style", true ) );
		cp.attach( 1, row++, new JComboBox( new String[] { "1F" } ), GridBagConstraints.NORTHWEST );
		return cp;
	}
	
	/**
	 * Creates the 'Point Of Measurement' pane
	 * @return the {@link JPanel content pane}
	 */
	private JComponent createContentPane_PointOfMeasurement() {
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.fill	  = GridBagConstraints.BOTH;
		cp.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( 
						BorderFactory.createLineBorder( BLACK ),
						"Measurements" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 )
		) );
		cp.gbc.insets = new Insets( 2, 2, 2, 6 );
		
		// define the column headers
		final String[] headers = {
				 "Point of measurements", 
				 "Target Meas: bfr wsh", 
				 "Target Meas: aft wsh", 
				 "Shipped/Sample: bfr wsh", 
				 "Shipped/Sample: aft wsh", 
				 "Tolerence"
		};
		
		// create the data array
		final String[][] data = {
				{ "Waist @ top of waistband edge", "34 1/2", "34 1/2", "34 1/2", "34 1/2", "0" },
				{ "Seat (measured 3-point technique) @ 7 1/4\" fr top", "34 1/2", "34 1/2", "34 1/2", "34 1/2", "0" },
				{ "Thigh @ 1\" from crotch", "34 1/2", "34 1/2", "34 1/2", "34 1/2", "0" },
				{ "Knee  @ 19\" From Hem", "34 1/2", "34 1/2", "34 1/2", "34 1/2", "0" },
				{ "Outseam to top of waistband", "34 1/2", "34 1/2", "34 1/2", "34 1/2", "0" }
		};
		
		final JTable table = new JTable( data, headers );
		table.setPreferredScrollableViewportSize( new Dimension( 400, 200 ) );
		final JScrollPane jsp = new JScrollPane( table );
		
		cp.attach( 0, 0, jsp, GridBagConstraints.NORTHWEST );
		return cp;
	}
	
}
