package constellation.app.ui.informationbar;

import static constellation.app.math.CxZoomUtil.*;
import static constellation.ui.components.buttons.CxButton.createBorderlessButton;
import static java.lang.String.format;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import constellation.ApplicationController;
import constellation.app.functions.view.ZoomInFunction;
import constellation.app.math.CxZoomUtil;
import constellation.app.math.CxZoomUtil.AutoFitAction;
import constellation.app.math.CxZoomUtil.ZoomOutAction;
import constellation.drawing.Camera;
import constellation.functions.FunctionAction;
import constellation.ui.components.CxPanel;

/**
 * Constellation Zoom Panel
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
class ZoomPane extends CxPanel {	
	// zoom constant definitions
	private static final String LAST 		= "Last";
	private static final String PAGE 		= "Page";
	private static final String PAGE_WIDTH 	= "Width";
	
	// icon mapping definitions
	private static final Icon BLANK = new ImageIcon( new BufferedImage( 16, 16, BufferedImage.TYPE_4BYTE_ABGR ) );
	private static final Map<String, Icon> MAPPING;
	static {
		MAPPING = new HashMap<String, Icon>();
		MAPPING.put( "50%",			MINI_ZOOM_1TO2_ICON );
		MAPPING.put( "100%", 		MINI_ZOOM_1TO1_ICON );
		MAPPING.put( "200%", 		MINI_ZOOM_2TO1_ICON );
		MAPPING.put( LAST, 			MINI_ZOOM_PREV_ICON );
		MAPPING.put( PAGE, 			MINI_ZOOM_PAGE_ICON );
		MAPPING.put( PAGE_WIDTH, 	MINI_ZOOM_WIDTH_ICON );
	}
	
	// internal fields
	private final ApplicationController controller;
	private final ZoomComboBox zoomBox;
	
	/** 
	 * Creates the Camera Zoom Panel
	 * @param controller the given {@link ApplicationController controller}
	 */
	public ZoomPane( final ApplicationController controller ) {
		super.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		super.gbc.anchor = GridBagConstraints.WEST;
		super.gbc.fill = GridBagConstraints.BOTH;
		
		// capture main variables
		this.controller	= controller;
		this.zoomBox 	= new ZoomComboBox();
		
		// attach the components
		int col = -1;
		super.attach( ++col, 0, createBorderlessButton( ZOOM_WINDOW_ICON, new FunctionAction( controller, ZoomInFunction.class ), "Zoom In" ) );
		super.attach( ++col, 0, createBorderlessButton( ZOOM_OUT_ICON, new ZoomOutAction( controller ), "Zoom Out" ) );
		super.attach( ++col, 0, createBorderlessButton( ZOOM_AUTOFIT_ICON, new AutoFitAction( controller ), "Auto-fit" ) );
		super.gbc.fill = GridBagConstraints.HORIZONTAL;
		super.attach( ++col, 0, zoomBox, GridBagConstraints.WEST  );
		
		// attach the zoom action listener 
		zoomBox.addActionListener( new ZoomAdjustmentHandler() );
	}
	
	/**
	 * Sets the zoom factor
	 * @param zoomFactor the given zoom factor value
	 */
	public void setZoom( final Double zoomFactor ) {
		final String zoomValue = format( "%d%%", (int)( zoomFactor * 100d ) );
		zoomBox.setSelectedItem( zoomValue );
		zoomBox.repaint();
	}
	
	/**
	 * Constellation Zoom Adjustment Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class ZoomAdjustmentHandler implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// was it a custom zoom option?
			final Object zoomString = zoomBox.getSelectedItem();
			
			// previous zoom?
			if( LAST.equals( zoomString ) ) {
				CxZoomUtil.zoom1To1( controller );
			}
			
			// entire page zoom?
			else if( PAGE.equals( zoomString ) ) {
				CxZoomUtil.autoFit( controller );
			}
			
			// entire page width zoom?
			else if( PAGE_WIDTH.equals( zoomString ) ) {
				CxZoomUtil.autoFit( controller );
			}
			
			// determine the zoom factor
			else {
				// set the zoom
				final double zoomFactor = zoomBox.getPercent();
				final Camera camera = controller.getCamera();
				camera.setZoomFactor( zoomFactor );
			}
			
			// redraw the scene
			controller.requestRedraw();
		}
	}
	
	/** 
	 * Constellation Zoom ComboBox
	 * @author lawrence.daniels@gmail.com
	 */
	private class ZoomComboBox extends JComboBox {
		
		/**
		 * Default Constructor
		 */
		public ZoomComboBox() {
			super( new String[] {
				"400%", "200%", "150%", "100%", "75%", "50%", "25%", "10%",
				"Last", "Page", "Width"
			} );
			super.setToolTipText( "Change the zoom" );
			super.setRenderer( new ZoomCellRenderer( super.getRenderer() ) );
			super.setEditable( true );
		}
		
		/**
		 * Returns the decimal value contained within 
		 * the text field
		 * @return the decimal value
		 */
		public double getPercent() {
			// get the text string
			String text = ((String)getSelectedItem()).trim();
			
			// is the string ends in a percentage symbol, remove it.
			if( text.endsWith( "%" ) ) {
				text = text.substring( 0, text.length() - 1 ).trim();
			}
			
			// return the zoom factor
			return parseDouble( text ) / 100d;
		}
		
		/**
		 * Parsing the decimal string
		 * @param valueString the given decimal string value
		 * @return the decimal value
		 */
		private double parseDouble( final String valueString ) {
			try {
				return Double.parseDouble( valueString );
			}
			catch( final NumberFormatException e ) {
				return 100;
			}
		}
	}
	
	/** 
	 * Constellation Zoom Cell Renderer
	 * @author lawrence.daniels@gmail.com
	 */
	private class ZoomCellRenderer implements ListCellRenderer {
		private final ListCellRenderer renderer;
		
		/**
		 * Creates a new Zoom Cell Renderer
		 * @param renderer the given system {@link ListCellRenderer list cell renderer}
		 */
		public ZoomCellRenderer( final ListCellRenderer renderer ) {
			this.renderer = renderer;
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent( final JList list, 
													   final Object value,
													   final int index, 
													   final boolean isSelected,
													   final boolean cellHasFocus ) {
			// get the component as returned from the system cell renderer
			final Component comp = 
				renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

			// lookup the zoom icon
			final Icon icon = MAPPING.get( value );	
			
			// if the component is a label, attach the icon to it
			if( comp instanceof JLabel ) {
				final JLabel label = (JLabel)comp;
				label.setIcon( ( icon == null ) ? BLANK : icon );
			}

			return comp;
		}
	}
	
}
