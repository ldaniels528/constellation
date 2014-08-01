package constellation.app.functions.layout;

import static constellation.util.StringUtil.isBlank;
import static java.lang.String.format;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import constellation.ApplicationController;
import constellation.CxConfigurationUtil;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PictureXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.UserImage;
import constellation.model.GeometricModel;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;
import constellation.ui.components.fields.CxDecimalField;
import constellation.ui.components.fields.CxIDField;
import constellation.ui.components.fields.CxLabel;
import constellation.ui.components.fields.CxStringField;

/** 
 * Picture Management Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PictureManagementDialog extends CxDialog {
	private PictureImportDialog importDialog;
	private PicturePlacementDialog placementDialog;
	
	/**
	 * Default constructor
	 */
	public PictureManagementDialog( final ApplicationController controller ) {
		super( controller, "Picture Management" );			
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}

	/** 
	 * Sets the coordinates of the given image within the dialog
	 * @param vertex the given {@link PointXY vertex}
	 */
	public void setCoordinates( final PointXY vertex ) {
		placementDialog.setCoordinates( vertex );
	}
	
	/** 
	 * Imports the settings of the given picture
	 * @param picture the given {@link PictureXY picture}
	 */
	public void importSettings( final ModelElement picture ) {
		placementDialog.importSettings( picture );
	}
	
	/** 
	 * Resets the input values
	 */
	public void reset() {
		importDialog.reset();
		placementDialog.reset();
	}
	
	/**
	 * Creates the main content pane
	 * @return the main content pane
	 */
	private JComponent createContentPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab( "Import", importDialog = new PictureImportDialog() );
		tabbedPane.addTab( "Placement", placementDialog = new PicturePlacementDialog() );
		return tabbedPane;
	}

	/** 
	 * Picture Import Dialog
	 * @author lawrence.daniels@gmail.com
	 */
	private class PictureImportDialog extends CxPanel
	implements ActionListener {
		public static final int MAX_NAME_LENGTH = 40;
		private PicturePanel picturePanel;
		private CxStringField fileField;
		private CxStringField nameField;
		private CxLabel hField;
		private CxLabel wField;
		
		/**
		 * Default constructor
		 */
		public PictureImportDialog() {		
			createContentPane( this );
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the dialog input values
			final String imageName	= nameField.getText();
			final File imageFile	= new File( fileField.getText() );
			
			// the image name must be present
			if( isBlank( imageName ) ) {
				controller.setStatusMessage( "The label field must not be blank" );
				return;
			}
			
			// create the image
			final UserImage userImage;
			try {
				userImage = UserImage.createUserImage( imageName, imageFile);
			}
			catch( final IOException cause ) {
				controller.showErrorDialog( "Picture Import Error", cause );
				return;
			}
			
			// add the picture to the model
			controller.getModel().addUserImage( userImage );
			
			// set the message
			controller.setStatusMessage( format( "Imported picture as '%s'", imageName ) );
			
			// reset the dialog
			PictureManagementDialog.this.reset();
		}
		
		/** 
		 * Resets the input values
		 */
		public void reset() {
			nameField.reset();
			wField.reset();
			hField.reset();
			fileField.reset();
			picturePanel.reset();
		}

		/**
		 * Creates the content pane
		 * @return the content {@link JComponent pane}
		 */
		private JComponent createContentPane( final CxPanel cp ) {
			cp.gbc.insets = new Insets( 5, 5, 5, 5 );
			cp.gbc.weightx = 0;
			cp.gbc.weighty = 0;
			int row = -1;
			
			// setup the image field
			picturePanel = new PicturePanel();
			picturePanel.setPreferredSize( new Dimension( 400, 400 ) );
			
			// create the scroll pane
			final JScrollPane scrollPane = new JScrollPane( picturePanel );
			scrollPane.setPreferredSize( new Dimension( 400, 400 ) );
			
			// row #1
			cp.gbc.fill = GridBagConstraints.HORIZONTAL;
			cp.attach( 0, ++row, new JLabel( "Label:")  );
			cp.gbc.gridwidth = 2;
			cp.attach( 1,   row, nameField = new CxStringField( MAX_NAME_LENGTH ) );
			cp.gbc.gridwidth = 1;
			
			// row #2, column #1-2
			cp.attach( 0, ++row, new JLabel( "Width:" ) );
			cp.attach( 1,   row, wField = new CxLabel() );
			
			// row #2, column #3: image area
			cp.gbc.gridheight = 2;
			cp.gbc.fill = GridBagConstraints.BOTH;
			cp.gbc.weightx = 2;
			cp.attach( 2,  row, scrollPane );
			cp.gbc.weightx = 0;
			cp.gbc.gridheight = 1;
			
			// row #4
			cp.gbc.fill = GridBagConstraints.HORIZONTAL;
			cp.attach( 0, ++row, new JLabel( "Height:" ) );
			cp.attach( 1,   row, hField = new CxLabel() );
			
			// row #5
			final CxPanel filePanel = new CxPanel();
			filePanel.attach( 0, 0, new JLabel( "Picture File:")  );
			filePanel.attach( 1, 0, fileField = new CxStringField( 40 ) );
			filePanel.attach( 2, 0, new CxButton( "Browse", new PictureImportAction() ) );
			
			cp.gbc.gridwidth = 3;
			cp.attach( 0, ++row, filePanel  );
			cp.gbc.gridwidth = 1;
			
			// row #6
			cp.attach( 1, ++row, new CxButton( "Accept", this ) );
			return cp;
		}
		
		/**
		 * Picture Import Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class PictureImportAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// create a file chooser instance
				final JFileChooser chooser = new JFileChooser( CxConfigurationUtil.getUserHomeDirectory() );
				
				// open the file dialog
				final int returnVal = chooser.showOpenDialog( (Component)event.getSource() );
			    if( returnVal == JFileChooser.APPROVE_OPTION ) {
			    	// load the image
			    	final File chosenFile = chooser.getSelectedFile();
			    	final ImageIcon icon = new ImageIcon( chosenFile.getAbsolutePath() );
			    	
			    	// get the image attributes
			    	picturePanel.setImage( icon.getImage() );
			    	wField.setText( icon.getIconWidth() );
			    	hField.setText( icon.getIconHeight() );
			    	fileField.setText( chosenFile.getAbsolutePath() );
			    	
			    	// if the name field is blank, populate it
			    	if( nameField.isBlank() ) {
			    		final String fileName = chosenFile.getName();
			    		final String defaultName = fileName.length() < MAX_NAME_LENGTH ? fileName : fileName.substring( 0, MAX_NAME_LENGTH );
			    		nameField.setText( defaultName ); 
			    	}
			    	
			    	// update the picture panel
			    	picturePanel.repaint();
			    }	
			}	
		}
		
	}
	
	/** 
	 * Picture Placement Dialog
	 * @author lawrence.daniels@gmail.com
	 */
	private class PicturePlacementDialog extends CxPanel 
	implements ActionListener {
		private ModelElement selectedPicture;
		private CxDecimalField xField;
		private CxDecimalField yField;
		private CxLabel hField;
		private CxLabel wField;
		private UserImageSelectionBox pictureBox;
		private PicturePanel picturePanel;
		private CxIDField idField;
		
		/**
		 * Default constructor
		 */
		public PicturePlacementDialog() {
			createContentPane( this );
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the point attributes
			final double x = xField.getDecimal();
			final double y = yField.getDecimal();
			final UserImage image = pictureBox.getSelectedUserImage();
			
			// create the picture
			createPicture( x, y, image );
		}
		
		/** 
		 * Resets the X- and Y- values
		 */
		public void reset() {
			idField.reset();
			xField.reset();
			yField.reset();
			wField.reset();
			hField.reset();
			pictureBox.reset();
			picturePanel.reset();
			
			// preset the ID field with the next unique name
			idField.setText( controller.getModel().getNamingService().getEntityName( EntityCategoryTypes.IMAGE ) );
		}
		
		/** 
		 * Sets the coordinates of the given image within the dialog
		 * @param vertex the given {@link PointXY vertex}
		 */
		public void setCoordinates( final PointXY vertex ) {
			xField.setDecimal( vertex.getX() );
			yField.setDecimal( vertex.getY() );
		}

		/** 
		 * Imports the settings of the given picture
		 * @param picture the given {@link PictureXY picture}
		 */
		public void importSettings( final ModelElement element ) {
			// remember this model element
			selectedPicture = element;
			
			// get the picture drawing element
			final PictureXY picture = (PictureXY)element.getRepresentation();
			
			// get the picture's position in space
			final PointXY location = picture.getLocation();
			
			// set the attributes
			idField.setText( element.getLabel() );
			xField.setDecimal( location.getX() );
			yField.setDecimal( location.getY() );
			wField.setText( picture.getWidth() );
			hField.setText( picture.getHeight() );
			pictureBox.setSelectedItem( picture.getUserImage() );
		}
		
		/**
		 * Create the picture at the given (x,y) coordinate
		 * @param x the given X-axis of the coordinate point
		 * @param y the given Y-axis of the coordinate point
		 * @param image the given {@link UserImage image}
		 */
		private void createPicture( final double x, final double y, final UserImage image ) {
			// create the image
			final PictureXY picture = new PictureXY( new PointXY( x, y ), image );
			
			// create the picture element
			final ModelElement pictElem = new CxModelElement( picture );
			
			// if the label isn't blank, set it
			final String label = idField.getText();
			if( !isBlank( label ) ) {
				pictElem.setLabel( label );
			}
			
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// remove the selected picture
			if( selectedPicture != null ) {
				model.erase( selectedPicture );
			}
			
			// add the picture to the model
			model.addPhysicalElement( pictElem );
			
			// set the message
			controller.setStatusMessage( format( "Image '%s' %s", pictElem.getLabel(), 
									( selectedPicture != null ) ? "updated" : "created" ) );
			
			// reset the dialog
			PictureManagementDialog.this.reset();
		}

		/**
		 * Creates the content pane
		 * @return the content {@link JComponent pane}
		 */
		private JComponent createContentPane( final CxPanel cp ) {
			cp.gbc.insets = new Insets( 5, 5, 5, 5 );
			cp.gbc.weightx = 0;
			cp.gbc.weighty = 0;
			int row = -1;
			
			// setup the image field
			picturePanel = new PicturePanel();
			picturePanel.setPreferredSize( new Dimension( 400, 400 ) );
			
			// create the scroll pane
			final JScrollPane scrollPane = new JScrollPane( picturePanel );
			scrollPane.setPreferredSize( new Dimension( 400, 400 ) );
			
			// row #1
			cp.gbc.fill = GridBagConstraints.NONE;
			cp.attach( 0, ++row, new JLabel( "ID:")  );
			cp.attach( 1,   row, idField = new CxIDField() );
			
			// row #1, col #2: image area
			cp.gbc.gridheight = 5;
			cp.gbc.fill = GridBagConstraints.BOTH;
			cp.gbc.weightx = 2;
			cp.gbc.fill = GridBagConstraints.HORIZONTAL;
			cp.attach( 2,   row, scrollPane );
			cp.gbc.weightx = 0;
			cp.gbc.fill = GridBagConstraints.NONE;
			cp.gbc.gridheight = 1;
			
			// row #2
			cp.attach( 0, ++row, new JLabel( "X-Axis:" ) );
			cp.attach( 1,   row, xField = new CxDecimalField() );
			
			// row #3
			cp.attach( 0, ++row, new JLabel( "Y-Axis:" ) );
			cp.attach( 1,   row, yField = new CxDecimalField() );
			
			// row #2
			cp.attach( 0, ++row, new JLabel( "Width:" ) );
			cp.attach( 1,   row, wField = new CxLabel() );
			
			// row #3
			cp.attach( 0, ++row, new JLabel( "Height:" ) );
			cp.attach( 1,   row, hField = new CxLabel() );
			
			// row #4
			cp.attach( 0, ++row, new JLabel( "Picture:" ) );
			cp.gbc.gridwidth = 2;
			cp.attach( 1,   row, pictureBox = new UserImageSelectionBox( controller, new PictureSelectionAction() ) );
			cp.gbc.gridwidth = 1;
			
			// row #5
			cp.attach( 1, ++row, new CxButton( "Accept", this ) );
			return cp;
		}
		
		/**
		 * Picture Selection Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class PictureSelectionAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
			    final UserImageSelectionBox box = (UserImageSelectionBox)event.getSource();
			    final UserImage userImage = (UserImage)box.getSelectedItem();
			    if( userImage != null ) {
			    	// get the image attributes
			    	picturePanel.setImage( userImage.getImage() );
			    	wField.setText( userImage.getWidth() );
			    	hField.setText( userImage.getHeight() );
			    	picturePanel.repaint();	
			    }
			}	
		}
		
	}
	
}