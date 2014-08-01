package constellation.app.functions.edit;

import static constellation.math.CxMathUtil.*;
import static constellation.drawing.EntityRepresentationUtil.*;
import static constellation.drawing.EntityTypes.*;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.lang.String.format;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.InputDialog;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityNamingService;
import constellation.drawing.EntityTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CommentXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.EllipticArcXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.SpiralXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.Function;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.AcceptButton;
import constellation.ui.components.buttons.CancelButton;
import constellation.ui.components.comboboxes.ColorSelectionBox;
import constellation.ui.components.comboboxes.LayerComboBox;
import constellation.ui.components.comboboxes.LinePatternBox;
import constellation.ui.components.fields.CxDecimalField;
import constellation.ui.components.fields.CxIDField;
import constellation.ui.components.fields.CxIntegerField;

/** 
 * Constellation Entity Editor Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class EntityEditorDialog extends InputDialog {
	// singleton instance
	private static EntityEditorDialog instance = null;
	
	// internal fields
	private final Map<EntityTypes, Icon> entityImages;
	private final EntityTypeBox typeBox;
	private final CxIDField idF;
	private final CxDecimalField xF;
	private final CxDecimalField yF;
	private final CxDecimalField x2F;
	private final CxDecimalField y2F;
	private final CxDecimalField angleAF;
	private final CxDecimalField angleBF;
	private final CxDecimalField radiusF;
	private final CxDecimalField widthF;
	private final CxDecimalField heightF;
	private final CxDecimalField incrementF;
	private final LayerComboBox layerBox;
	private final LinePatternBox patternBox;
	private final ColorSelectionBox colorBox;
	private final CxIntegerField revolutionsF;
	private final JTextComponent textF;
	private final JCheckBox infiniteBox;
	private final JList fxF;
	
	// mutable fields
	private Function callingFunction;
	private ModelElement selectedElement;
	
	/**
	 * Default constructor
	 */
	private EntityEditorDialog( final ApplicationController controller ) {
		super( controller, "Editor" );	
		
		// get the curve images
		this.entityImages = createEntityTypeImageMapping();
		
		// initialize the components
		typeBox			= new EntityTypeBox();
		idF				= new CxIDField();
		xF 				= new CxDecimalField( 0d );
		yF 				= new CxDecimalField( 0d );
		x2F 			= new CxDecimalField( 0d );
		y2F 			= new CxDecimalField( 0d );
		infiniteBox		= new JCheckBox( "Yes" );
		fxF				= new JList();
		angleAF			= new CxDecimalField( 0d );
		angleBF			= new CxDecimalField( 0d );
		radiusF			= new CxDecimalField( 10d );
		widthF			= new CxDecimalField( 10d );
		heightF			= new CxDecimalField( 10d );
		incrementF		= new CxDecimalField( 0.1d );
		revolutionsF	= new CxIntegerField( 1 );
		textF			= new JTextPane();
		layerBox		= new LayerComboBox();
		patternBox		= new LinePatternBox();
		colorBox 		= new ColorSelectionBox();
		
		// set the preferred size of specific field
		fxF.setPreferredSize( new Dimension( 50, 100 ) );
		textF.setPreferredSize( new Dimension( 50, 100 ) );
		
		// setup the content pane
		updateDialog( EntityTypes.values()[0] );
		super.setLocation( controller.getLowerRightAnchorPoint( this ) );
	}
	
	/**
	 * Returns the singleton instance of the class
	 * @return the singleton instance of the class
	 */
	public static EntityEditorDialog getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new EntityEditorDialog( controller );
		}
		return instance;
	}
	
	/**
	 * Returns the selected geometric element
	 * @return the selected {@link ModelElement geometric element}
	 */
	public ModelElement getSelectedElement() {
		return selectedElement;
	}
	
	/** 
	 * Imports the settings of the given model element
	 * @param element the given {@link ModelElement model element}
	 */
	public void importSettings( final ModelElement element ) {
		// capture the selected element
		this.selectedElement = element;
		
		// output a message to the user
		controller.setStatusMessage( format( "Selected %s '%s'", getTypeName( element.getType() ), element ) );
		
		// determine the element type
		final EntityTypes type = element.getType();
		
		// setup the content pane
		updateDialog( type );
		
		// set the curve type
		typeBox.setSelectedItem( type );
		
		// lock down the type box
		typeBox.setEnabled( false );
		
		// infinite line indicator should be off by default
		infiniteBox.setSelected( false );
		
		// retrieve the curve's basic attributes
		idF.setText( element.getLabel() );
		
		// import the pattern and color attributes
		patternBox.setSelectedPattern( element.getPattern() );
		layerBox.setSelectedIndex( element.getLayer() );
		colorBox.setSelectedColor( element.getColor() );
		
		// retrieve the curve type specific attributes
		switch( element.getType() ) {
			case LINE:
				final LineXY line = EntityRepresentationUtil.getLine( element );
				xF.setDecimal( line.getX1() );
				yF.setDecimal( line.getY1() );
				x2F.setDecimal( line.getX2() );
				y2F.setDecimal( line.getY2() );
				infiniteBox.setSelected( line.isInfinite() );
				break;

			case POINT:
				final PointXY point = EntityRepresentationUtil.getPoint( element );
				xF.setDecimal( point.getX() );
				yF.setDecimal( point.getY() );
				break;
						
			case CIRCLE:
				final CircleXY circle = EntityRepresentationUtil.getCircle( element );
				xF.setDecimal( circle.getCenterX() );
				yF.setDecimal( circle.getCenterY() );
				radiusF.setDecimal( circle.getRadius() );
				break;
				
			case ARC:
				final ArcXY arc = EntityRepresentationUtil.getCircularArc( element );
				xF.setDecimal( arc.getCenterX() );
				yF.setDecimal( arc.getCenterY() );
				radiusF.setDecimal( arc.getRadius() );
				angleAF.setDecimal( convertRadiansToDegrees( arc.getAngleStart() ) );
				angleBF.setDecimal( convertRadiansToDegrees( arc.getAngleEnd() ) );
				break;
				
			case ELLIPSE:
				final EllipseXY ellipse = EntityRepresentationUtil.getEllipse( element );
				xF.setDecimal( ellipse.getCenterX() );
				yF.setDecimal( ellipse.getCenterY() );
				widthF.setDecimal( ellipse.getWidth() );
				heightF.setDecimal( ellipse.getHeight() );
				break;
				
			case ELLIPTIC_ARC:
				final EllipticArcXY ellipticArc = EntityRepresentationUtil.getEllipticArc( element );
				xF.setDecimal( ellipticArc.getCenterX() );
				yF.setDecimal( ellipticArc.getCenterY() );
				widthF.setDecimal( ellipticArc.getWidth() );
				heightF.setDecimal( ellipticArc.getHeight() );
				angleAF.setDecimal( convertRadiansToDegrees( ellipticArc.getAngleStart() ) );
				angleBF.setDecimal( convertRadiansToDegrees( ellipticArc.getAngleEnd() ) );
				break;
				
			case SPLINE:
				//final SplineXY spline = EntityRepresentationUtil.getSpline( element );
				// we don't need to do anything here
				break;
				
			case SPIRAL:
				final SpiralXY spiral = EntityRepresentationUtil.getSpiral( element );
				xF.setDecimal( spiral.getCenterX() );
				yF.setDecimal( spiral.getCenterY() );
				radiusF.setDecimal( spiral.getRadius() );
				incrementF.setDecimal( spiral.getIncrement() );
				revolutionsF.setInteger( spiral.getRevolutions() );
				break;
				
			case USER_DEFINED:
				throw new IllegalArgumentException( "User defined curves are not implemented yet" );
						
			default:
				throw new IllegalArgumentException( format( "Unhandled geometry type %s", element.getType() ) );
		}
		
		// set the curve as temporary geometry
		final GeometricModel model = controller.getModel();
		model.setTemporaryElement( element );
	}
	
	/**
	 * Sets the current active function
	 * @param callingFunction the given calling function
	 * @param type the given {@link EntityTypes entity type}
	 */
	public void setCallingFunction( final Function callingFunction, 
									final EntityTypes type ) {
		this.callingFunction = callingFunction;
		
		// setup the content pane
		//updateDialog( type );
		typeBox.setSelectedItem( type );
		
		// preset the element's ID
		final GeometricModel model = controller.getModel();
		final EntityNamingService namingService = model.getNamingService();
		final EntityCategoryTypes categoryType = EntityRepresentationUtil.getCategoryType( type );
		idF.setText( namingService.getEntityName( categoryType ) );
	}
	
	/**
	 * Returns arc angle "A"
	 * @return the given angle in radians
	 */
	public Double getAngleA() {
		return angleAF.getDecimal();
	}
	
	/**
	 * Sets arc angle "A"
	 * @param angle the given angle in radians
	 */
	public void setAngleA( final double angle ) {
		angleAF.setDecimal( angle );
	}
	
	/**
	 * Returns arc angle "B"
	 * @return the given angle in radians
	 */
	public Double getAngleB() {
		return angleBF.getDecimal();
	}
	
	/**
	 * Sets arc angle "B"
	 * @param angle the given angle in radians
	 */
	public void setAngleB( final double angle ) {
		angleBF.setDecimal( angle );
	}
	
	/** 
	 * Sets the coordinates of the given point within the dialog
	 * @param vertex the given {@link PointXY vertex}
	 */
	public void setCoordinates( final PointXY vertex ) {
		// set the coordinates
		xF.setDecimal( vertex.getX() );
		yF.setDecimal( vertex.getY() );
	}
	
	/** 
	 * Sets the coordinates of the given point within the dialog
	 * @param vertex the given {@link PointXY vertex}
	 */
	public void setCoordinatesB( final PointXY vertex ) {
		x2F.setDecimal( vertex.getX() );
		y2F.setDecimal( vertex.getY() );
	}
	
	/**
	 * Indicates whether the dialog is currently in editor mode
	 * @return true, if dialog is in editor mode
	 */
	public boolean isEditorMode() {
		return ( callingFunction instanceof EntityEditorFunction );
	}
	
	/**
	 * Returns the spiral increment value
	 * @return the spiral increment value
	 */
	public Double getIncrement() {
		return incrementF.getDecimal();
	}
	
	/**
	 * Sets the spiral increment value
	 * @param increment the spiral increment value
	 */
	public void setIncrement( final double increment ) {
		incrementF.setDecimal( increment );
	}
	
	/**
	 * Indicates whether the line to be created is an infinite line
	 * @return true, if the line to be created is an infinite line 
	 */
	public boolean isInfinite() {
		return infiniteBox.isSelected();
	}
	
	/**
	 * Sets the infinite line state flag
	 * @param infinite the infinite line state flag
	 */
	public void setInfinite( final boolean infinite ) {
		infiniteBox.setSelected( infinite );
	}
	
	/**
	 * Returns the circle/arc radius
	 * @return the circle/arc radius
	 */
	public Double getRadius() {
		return radiusF.getDecimal();
	}
	
	/** 
	 * Sets the circle/arc radius
	 * @param radius the circle/arc radius
	 */
	public void setRadius( final double radius ) {
		radiusF.setDecimal( radius );
	}
	
	/**
	 * Returns the number of spiral revolutions
	 * @return the number of spiral revolutions
	 */
	public Integer getRevolutions() {
		return revolutionsF.getInteger();
	}
	
	/** 
	 * Sets the number of spiral revolutions
	 * @param revolutions the number of spiral revolutions
	 */
	public void setRevolutions( final int revolutions ) {
		revolutionsF.setInteger( revolutions );
	}
	
	/**
	 * Returns the entity width
	 * @return the entity width
	 */
	public Double getEntityWidth() {
		return widthF.getDecimal();
	}
	
	/** 
	 * Sets the entity width
	 * @param width entity width
	 */
	public void setEntityWidth( final double width ) {
		widthF.setDecimal( width );
	}

	/** 
	 * Resets the X- and Y- values
	 */
	public void reset() {
		// clear the selection
		selectedElement = null;
				
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// clear the temporary geometry
		model.clearTemporaryElement();
		
		// reset the input fields
		idF.reset();
		xF.reset();
		yF.reset();
		x2F.reset();
		y2F.reset();
		radiusF.setDecimal( 10d );
		widthF.setDecimal( 10d );
		heightF.setDecimal( 10d );
		angleAF.reset();
		angleBF.reset();
		
		// reset the comboBoxes
		colorBox.reset();
		layerBox.reset();
		patternBox.reset();
		
		// if we're in editor mode
		if( isEditorMode() ) {
			// reset the type box
			typeBox.reset();
			
			// unlock the type box
			typeBox.setEnabled( isEditorMode() );
		}
		
		// preset the element's ID
		final EntityCategoryTypes categoryType = EntityRepresentationUtil.getCategoryType( typeBox.getSelectedType() );
		idF.setText( model.getNamingService().getEntityName( categoryType ) );
		
		// request a redraw
		controller.requestRedraw();
	}

	/**
	 * Creates the content pane
	 * @return the {@link JComponent pane}
	 */
	private JComponent createContentPane( final EntityTypes type ) {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		cp.gbc.fill = GridBagConstraints.BOTH;
		
		// put it all together
		int row = -1;
		
		// attach the "Identification" section
		cp.attach( 0, ++row, createContentPane_IdentificationSection( type ) );
		
		// attach the "Parameter" section
		final JComponent paramPane = createContentPane_ParameterSection( type );
		if( paramPane != null ) {
			cp.attach( 0, ++row, paramPane );
		}
		
		// attach the "Appearance" section
		cp.attach( 0, ++row, createContentPane_AppearanceSection() );
		
		// attach the "Accept" button
		cp.gbc.fill = GridBagConstraints.NONE;
		cp.gbc.anchor = GridBagConstraints.WEST;
		cp.attach( 0, ++row, new CancelButton( new CancelAction() ) );
		cp.attach( 0,   row, new AcceptButton( new AcceptAction() ), GridBagConstraints.EAST );
		return cp;
	}
	
	/**
	 * Creates the "Identification" section of the content pane
	 * @return the {@link JComponent pane}
	 */
	private JComponent createContentPane_IdentificationSection( final EntityTypes type ) {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		cp.gbc.anchor = GridBagConstraints.WEST;
		cp.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( "Identification" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) 
		) );
		
		// row #1
		int row = -1;
		cp.attach( 0, ++row, new JLabel( "Label:" )  );
		cp.attach( 1,   row, idF );
	
		// row #2 (for Element Editor function only) 
		if( isEditorMode() ) {
			cp.attach( 0, ++row, new JLabel( "Type:" )  );
			cp.attach( 1,   row, typeBox );
		}
		
		// row #3 (element image)
		final Icon icon = entityImages.get( type );
		if( icon != null ) {
			cp.gbc.gridwidth = 2;
			cp.gbc.fill = GridBagConstraints.BOTH;
			cp.attach( 0, ++row, createIconPanel( icon ) );
			cp.gbc.gridwidth = 1;
		}
		return cp;
	}
	
	/**
	 * Creates the "Parameter" section of the content pane
	 * @return the {@link JComponent pane}
	 */
	private JComponent createContentPane_ParameterSection( final EntityTypes type ) {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		cp.gbc.anchor = GridBagConstraints.WEST;
		cp.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( "Parameters" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) 
		) );
		
		// add the type specific elements
		int row = -1;
		switch( type ) {			
			case ARC:			row = attachArcDetails( cp, row ); break;	
			case CIRCLE:		row = attachCircleDetails( cp, row ); break;
			case COMMENT:		row = attachNoteDetails( cp, row ); break;
			case ELLIPSE:		row = attachEllipseDetails( cp, row ); break;
			case ELLIPTIC_ARC:	row = attachEllipticArcDetails( cp, row ); break;
			case LINE:			row = attachLineDetails( cp, row ); break;
			case POINT:			row = attachPointDetails( cp, row ); break;
			case SPIRAL:		row = attachSpiralDetails( cp, row ); break;
			case SPLINE:		row = attachSplineDetails( cp, row ); break;
			case TEXTNOTE:		row = attachNoteDetails( cp, row ); break;
		}
		
		return ( row > - 1 ) ? cp : null;
	}
	
	/**
	 * Creates the "Appearance" section of the content pane
	 * @return the {@link JComponent pane}
	 */
	private JComponent createContentPane_AppearanceSection() {
		final CxPanel cpB = new CxPanel();
		cpB.gbc.insets = new Insets( 2, 2, 2, 2 );
		cpB.gbc.anchor = GridBagConstraints.WEST;
		cpB.setBorder(  BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( "Appearance" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) 
		) );
		cpB.attach( 0, 0, new JLabel( "Layer:")  );
		cpB.attach( 1, 0, layerBox );
		cpB.attach( 0, 1, new JLabel( "Pattern:")  );
		cpB.attach( 1, 1, patternBox );
		cpB.attach( 0, 2, new JLabel( "Color:")  );
		cpB.attach( 1, 2, colorBox );
		return cpB;
	}
	
	/** 
	 * Attaches the "circle" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachCircleDetails( final CxPanel cp, int row ) {
		// row #4
		cp.attach( 0, ++row, new JLabel( "X-Axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Y-Axis:")  );
		cp.attach( 1,   row, yF );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Radius:")  );
		cp.attach( 1,   row, radiusF );
		return row;
	}
	
	/** 
	 * Attaches the "circular arc" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachArcDetails( final CxPanel cp, int row ) {
		// row #4
		cp.attach( 0, ++row, new JLabel( "X-axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Y-axis:")  );
		cp.attach( 1,   row, yF );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Radius:")  );
		cp.attach( 1,   row, radiusF );
		
		// row #7
		cp.attach( 0, ++row, new JLabel( "Start angle:")  );
		cp.attach( 1,   row, angleAF );
		
		// row #8
		cp.attach( 0, ++row, new JLabel( "End angle:")  );
		cp.attach( 1,   row, angleBF  );
		return row;
	}
	
	/** 
	 * Attaches the "ellipse" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachEllipseDetails( final CxPanel cp, int row ) {
		// row #4
		cp.attach( 0, ++row, new JLabel( "X-axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Y-axis:")  );
		cp.attach( 1,   row, yF );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Width:")  );
		cp.attach( 1,   row, widthF );
		
		// row #7
		cp.attach( 0, ++row, new JLabel( "Height:")  );
		cp.attach( 1,   row, heightF );
		return row;
	}
	
	/** 
	 * Attaches the "elliptical arc" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachEllipticArcDetails( final CxPanel cp, int row ) {
		// row #4
		cp.attach( 0, ++row, new JLabel( "X-axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Y-axis:")  );
		cp.attach( 1,   row, yF );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Width:")  );
		cp.attach( 1,   row, widthF );
		
		// row #7
		cp.attach( 0, ++row, new JLabel( "Height:")  );
		cp.attach( 1,   row, heightF );
		
		// row #8
		cp.attach( 0, ++row, new JLabel( "Start angle:")  );
		cp.attach( 1,   row, angleAF );
		
		// row #9
		cp.attach( 0, ++row, new JLabel( "End angle:")  );
		cp.attach( 1,   row, angleBF  );
		return row;
	}
	
	/** 
	 * Attaches the "line" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachLineDetails( final CxPanel cp, int row ) {
		// row #3
		cp.attach( 0, ++row, new JLabel( "X-axis A:")  );
		cp.attach( 1,   row, xF );
		
		// row #4
		cp.attach( 0, ++row, new JLabel( "Y-axis A:")  );
		cp.attach( 1,   row, yF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "X-axis B:")  );
		cp.attach( 1,   row, x2F );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Y-axis B:")  );
		cp.attach( 1,   row, y2F );
		
		// row #7
		cp.attach( 0, ++row, new JLabel( "Infinite Line" ) );
		cp.attach( 1,   row, infiniteBox );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Angle:")  );
		cp.attach( 1,   row, angleAF, GridBagConstraints.WEST );
		return row;
	}

	/** 
	 * Attaches the "note" (e.g. comment or text note) details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachNoteDetails( final CxPanel cp, int row ) {
		// row #4
		cp.attach( 0, ++row, new JLabel( "X-axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Y-axis:")  );
		cp.attach( 1,   row, yF );
		
		// row #6
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, new JLabel( "Note Text")  );
		
		// row #7
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.attach( 0, ++row, new JScrollPane( textF ), GridBagConstraints.WEST );
		return row;
	}

	/** 
	 * Attaches the "spiral" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachSpiralDetails( final CxPanel cp, int row ) {
		// row #4
		cp.attach( 0, ++row, new JLabel( "X-axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Y-axis:")  );
		cp.attach( 1,   row, yF );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Radius:")  );
		cp.attach( 1,   row, radiusF );
		
		// row #7
		cp.attach( 0, ++row, new JLabel( "Increment:")  );
		cp.attach( 1,   row, incrementF );
		
		// row #7
		cp.attach( 0, ++row, new JLabel( "Revolutions:")  );
		cp.attach( 1,   row, revolutionsF );
		return row;
	}
	
	/** 
	 * Attaches the "point" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachPointDetails( final CxPanel cp, int row ) {
		// row #3
		cp.attach( 0, ++row, new JLabel( "X-axis:")  );
		cp.attach( 1,   row, xF );
		
		// row #4
		cp.attach( 0, ++row, new JLabel( "Y-axis:")  );
		cp.attach( 1,   row, yF );
		return row;
	}
	
	/** 
	 * Attaches the "spline" details to the given panel
	 * @param cp the given {@link CxPanel panel}
	 * @param row the current row within the grid
	 * @return the {@link CxPanel panel}
	 */
	private int attachSplineDetails( final CxPanel cp, int row ) {
		// row #5
		//cp.attach( 0, ++row, new JLabel( "Points")  );
		//cp.attach( 1,   row, new JScrollPane( fxF ) );
		return row;
	}
	
	/**
	 * Creates a panel with an icon pictured on it
	 * @param icon the given {@link Icon icon}
	 * @return the {@link CxPanel panel}
	 */
	private CxPanel createIconPanel( final Icon icon ) {
		final CxPanel cp = new CxPanel();
		cp.setPreferredSize( new Dimension( 130, 130 ) );
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.setBorder( BorderFactory.createLineBorder( BLACK ) );
		cp.setBackground( WHITE );
		cp.attach( 0, 0, new JLabel( icon ) );
		return cp;
	}
	
	/** 
	 * Creates a curve based on the given entity type information
	 * @param type the given {@link EntityTypes entity type}
	 * @return the appropriate {@link EntityRepresentation geometric representation} instance
	 */
	private EntityRepresentation createInternalRepresentation( final EntityTypes type ) {
		// get the geometry's attributes
		final double x = xF.getDecimal();
		final double y = yF.getDecimal();
	
		// create the appropriate instance
		switch( type ) {
			case POINT:			
				return new PointXY( x, y );
				
			case LINE:			
				final LineXY line = new LineXY( 
						x, y, 
						x2F.getDecimal(), 
						y2F.getDecimal() 
					);
				return infiniteBox.isSelected() 
							? LineXY.createInfiniteLine( line ) 
							: line;
				
			case CIRCLE:		
				return new CircleXY( 
						x, y, 
						radiusF.getDecimal() 
					);
				
			case ARC:	
				return new ArcXY( 
						x, y, 
						radiusF.getDecimal(), 
						convertDegreesToRadians( angleAF.getDecimal() ), 
						convertDegreesToRadians( angleBF.getDecimal() ) 
					);		
			
			case ELLIPSE: 		
				return new EllipseXY( 
						x, y, 
						widthF.getDecimal(), 
						heightF.getDecimal() 
					);
				
			case ELLIPTIC_ARC:	
				return new EllipticArcXY( 
						x, y, 
						widthF.getDecimal(), 
						heightF.getDecimal(), 
						convertDegreesToRadians( angleAF.getDecimal() ), 
						convertDegreesToRadians( angleBF.getDecimal() ) 
					); 	
			
			case SPIRAL: 		
				return new SpiralXY( 
						x, y, 
						radiusF.getDecimal(), 
						incrementF.getDecimal(), 
						revolutionsF.getInteger() 
					);
				
			case SPLINE:		
				return null;
				
			case COMMENT:
				return new CommentXY( 
						new PointXY( x, y ), 
						textF.getText() 
					);
				
			case TEXTNOTE:
				return new TextNoteXY( 
						new PointXY( x, y ), 
						textF.getText() 
					);
				
			default:
				throw new IllegalArgumentException( format( "Element type '%s' could not be created", type ) );
		}
	}
	
	/**
	 * Updates the dialog's content based on the given type
	 * @param type the given {@link EntityTypes entity type}
	 */
	private void updateDialog( final EntityTypes type ) {
		// refresh the content pane
		super.setContentPane( createContentPane( type ) );
		super.pack();
	}
	
	/** 
	 * Creates a new geometric element based on the given entity type and geometric representation.
	 * @param type the given {@link EntityTypes entity types}
	 * @param representation the given {@link EntityRepresentation geometric representation}.
	 * @return the {@link ModelElement model element}
	 */
	private ModelElement createGeometricElement( final EntityTypes type, final EntityRepresentation representation ) {
		return toDrawingElement( representation );
	}
	
	/** 
	 * Returns the type to image mapping for each entity type
	 * @return the {@link EntityTypes type} to {@link Icon image} mapping 
	 */
	private static Map<EntityTypes, Icon> createEntityTypeImageMapping() {
		final Map<EntityTypes, Icon> map = new HashMap<EntityTypes, Icon>();
		final CxContentManager contentManager = CxContentManager.getInstance();
		map.put( LINE, 			contentManager.getIcon( "images/dialog/editor/line.gif" ) );
		map.put( POINT, 		contentManager.getIcon( "images/dialog/editor/point.gif" ) );
		map.put( CIRCLE, 		contentManager.getIcon( "images/dialog/editor/circle.png" ) );
		map.put( ARC,			contentManager.getIcon( "images/dialog/editor/arc.jpg" ) );
		map.put( ELLIPSE, 		contentManager.getIcon( "images/dialog/editor/ellipse.jpg" ) );
		map.put( ELLIPTIC_ARC,	contentManager.getIcon( "images/dialog/editor/ellipse.jpg" ) );
		map.put( PICTURE, 		contentManager.getIcon( "images/dialog/editor/picture.jpg" ) );
		map.put( POLYLINE, 		contentManager.getIcon( "images/dialog/editor/polyLine.gif" ) );
		map.put( SPIRAL, 		contentManager.getIcon( "images/dialog/editor/spiral.png" ) );
		map.put( SPLINE, 		contentManager.getIcon( "images/dialog/editor/spline.png" ) );
		map.put( USER_DEFINED,	contentManager.getIcon( "images/dialog/editor/user_defined.jpg" ) );
		return map;
	}
	
	/**
	 * Entity Type ComboBox
	 * @author lawrence.daniels@gmail.com
	 */
	private class EntityTypeBox extends JComboBox {
		
		/**
		 * Default constructor
		 */
		public EntityTypeBox() {
			super( EntityTypes.values() );
			super.addActionListener( new DialogRefreshAction() );
		}
		
		/**
		 * Resets the component to it's initial state
		 */
		public void reset() {
			super.setSelectedIndex( 0 );
		}

		/**
		 * Returns the selected curve type
		 * @return the selected {@link EntityTypes curve type}
		 */
		public EntityTypes getSelectedType() {
			return (EntityTypes)getSelectedItem();
		}
	}
	
	/**
	 * Accept Settings Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class AcceptAction implements ActionListener {
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			boolean created = ( selectedElement == null );
			
			// get the model instance
			final GeometricModel model = controller.getModel();
				
			// determine the entity type information
			final EntityTypes type = typeBox.getSelectedType();
			
			// create the new internal representation
			final EntityRepresentation representation = createInternalRepresentation( type );
			if( representation != null ) {
				// are we modifying an existing element?
				if( selectedElement != null ) {
					// set the entity's internal representation
					selectedElement.setRepresentation( representation );
				}
				
				// must be creating a new element
				else {
					// create the new element
					selectedElement = createGeometricElement( type, representation ); 
				}
			}
			
			// update the line pattern & color
			selectedElement.setColor( colorBox.getSelectedColor() );
			selectedElement.setPattern( patternBox.getSelectedPattern() );
			selectedElement.setLayer( layerBox.getSelectedIndex() );
			
			// update the given selected geometry
			model.addPhysicalElement( selectedElement );
			
			// alert the operator
			controller.setStatusMessage( format( created ? "Created new %s '%s'" : "Updated %s '%s'", 
					getTypeName( type ), selectedElement.getLabel() ) );
	
			// restart the calling function
			callingFunction.onStart( controller );
	
			// reset the dialog
			reset();
		}
	}
	
	/**
	 * Cancel Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class CancelAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			reset();
		}
	}
	
	/**
	 * Dialog Refresh Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class DialogRefreshAction implements ActionListener {
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the entity type box
			final EntityTypeBox box = (EntityTypeBox)event.getSource();
			
			// get the selected type
			final EntityTypes type = box.getSelectedType();
			
			// set the title bar
			EntityEditorDialog.this.setTitle( getTypeHeading( type ) );
			
			// update the dialog based on the selected type
			updateDialog( type );
			
			// set the next ID for the type
			final String idText = idF.getText();
			if( idText.trim().equals( "" ) || idText.startsWith( "*" ) ) {
				final GeometricModel model = controller.getModel();
				final EntityNamingService namingService = model.getNamingService();
				final EntityCategoryTypes categoryType = EntityRepresentationUtil.getCategoryType( type );
				idF.setText( namingService.getEntityName( categoryType ) );
			}
		}
	}
	
}