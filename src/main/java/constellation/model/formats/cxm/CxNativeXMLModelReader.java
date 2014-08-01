package constellation.model.formats.cxm;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.GRAY;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.lang.String.format;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import constellation.drawing.EntityRepresentation;
import constellation.drawing.LinePatterns;
import constellation.drawing.VertexContainer;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CommentXY;
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.EllipticArcXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PictureXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.PolyLineXY;
import constellation.drawing.entities.SpiralXY;
import constellation.drawing.entities.SplineXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.drawing.entities.UserImage;
import constellation.model.DefaultGeometricModel;
import constellation.model.DraftingStandards;
import constellation.model.Filter;
import constellation.model.GeometricModel;
import constellation.model.Unit;
import constellation.model.Units;

/**
 * Constellation Native Model File Reader
 * @author lawrence.daniels@gmail.com
 */
class CxNativeXMLModelReader extends DefaultHandler {
	// IGES Color to Java Color mapping
	private static Color[] IGES_COLORS = {
			null, BLACK, RED, GREEN, BLUE, YELLOW, 
			MAGENTA, CYAN, WHITE, GRAY, ORANGE
	};
	
	// define the tag enumerations
	private static enum TAG_ENUMS { 
		AUTHOR_NAME, CIRCLE, CIRCULAR_ARC, COMMENT, COMPOSITION,
		ELLIPSE, ELLIPTIC_ARC, FILTER, INCLUDE_LAYER, LINE, MODEL, 
		MODEL_COLORS, MODEL_DESC, MODEL_ELEM, ORGANIZATION_NAME, 
		PHANTOM_ELEMS, PHYSICAL_ELEMS, PICTURE, POINT, POLYLINE, SPIRAL, 
		SPLINE, TEXT_NOTE, USER_IMAGE, USER_IMAGE_LIST, VERTEX
	};
	
	// define the tag name to tag enumeration mapping
	private static final Map<String,TAG_ENUMS> TAGS;
	static {
		// create a mapping of the tag names to enumeration
		TAGS = new HashMap<String, TAG_ENUMS>();
		
		// model information
		TAGS.put( "Model", 				TAG_ENUMS.MODEL );
		TAGS.put( "ModelColors",		TAG_ENUMS.MODEL_COLORS );
		TAGS.put( "ModelDescription", 	TAG_ENUMS.MODEL_DESC );
		
		// author information
		TAGS.put( "AuthorName", 		TAG_ENUMS.AUTHOR_NAME );
		TAGS.put( "OrganizationName", 	TAG_ENUMS.ORGANIZATION_NAME );
		
		// model elements
		TAGS.put( "Element",			TAG_ENUMS.MODEL_ELEM );
		
		// drawing elements
		TAGS.put( "Circle",				TAG_ENUMS.CIRCLE );
		TAGS.put( "CircularArc",		TAG_ENUMS.CIRCULAR_ARC );
		TAGS.put( "Composition",		TAG_ENUMS.COMPOSITION );
		TAGS.put( "Comment",			TAG_ENUMS.COMMENT );
		TAGS.put( "Ellipse", 			TAG_ENUMS.ELLIPSE );
		TAGS.put( "EllipticArc", 		TAG_ENUMS.ELLIPTIC_ARC );
		TAGS.put( "Filter", 			TAG_ENUMS.FILTER );
		TAGS.put( "IncludeLayer", 		TAG_ENUMS.INCLUDE_LAYER );
		TAGS.put( "Line", 				TAG_ENUMS.LINE );
		TAGS.put( "PhantomElements", 	TAG_ENUMS.PHANTOM_ELEMS );
		TAGS.put( "PhysicalElements", 	TAG_ENUMS.PHYSICAL_ELEMS );
		TAGS.put( "Picture", 			TAG_ENUMS.PICTURE );
		TAGS.put( "Point", 				TAG_ENUMS.POINT );
		TAGS.put( "PolyLine", 			TAG_ENUMS.POLYLINE );
		TAGS.put( "Spiral", 			TAG_ENUMS.SPIRAL );
		TAGS.put( "Spline", 			TAG_ENUMS.SPLINE );
		TAGS.put( "TextNote",			TAG_ENUMS.TEXT_NOTE ); 
		TAGS.put( "UserImage", 			TAG_ENUMS.USER_IMAGE );
		TAGS.put( "UserImages", 		TAG_ENUMS.USER_IMAGE_LIST );
		TAGS.put( "Vertex",		 		TAG_ENUMS.VERTEX );
	}
	
	// logger instance
	private static final Logger logger = Logger.getLogger( CxNativeXMLModelReader.class );
	
	// internal fields
	private final LinkedList<EntityRepresentation> stackElems;
	private final LinkedList<Properties> stackAttribs;
	private final LinkedList<TAG_ENUMS> stackTags;
	private final GeometricModel model;
	private final StringBuilder characters;
	
	// state fields
	private CompositionXY currentComposition;
	private VertexContainer currentVertexElem;
	private Filter currentFilter;
	private TAG_ENUMS currentTag;
	
	/**
	 * Default constructor
	 */
	private CxNativeXMLModelReader( final File modelFile ) {
		this.characters		= new StringBuilder( 1024 );
		this.stackElems		= new LinkedList<EntityRepresentation>();
		this.stackAttribs	= new LinkedList<Properties>();
		this.stackTags		= new LinkedList<TAG_ENUMS>();
		this.model			= DefaultGeometricModel.newModel( modelFile );
	}
	
	/**
	 * Loads the model file from disk
	 * @param modelFile the given model {@link File file}
	 * @return the {@link GeometricModel model}
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static GeometricModel readFile( final File modelFile ) 
	throws ParserConfigurationException, SAXException, IOException {
		// create a handler instance
		final CxNativeXMLModelReader handler = new CxNativeXMLModelReader( modelFile );
		
		// get a SAX Parser Factory instance
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		
		// get a SAX Parser instance
		final SAXParser parser = parserFactory.newSAXParser();
		
		// parse the file
		parser.parse( modelFile, handler );
		
		// return the model
		return handler.model;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters( final char[] chars, final int start, final int length )
	throws SAXException {
		characters.append( String.copyValueOf( chars, start, length ) );
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement( final String uri, 
						  	final String localName, 
						  	final String name )
	throws SAXException {
		// get the text string
		final String text = characters.toString();
		
		// pop the current tag 
		stackTags.removeLast();
		
		// get the parent tag
		final TAG_ENUMS parentTag = !stackTags.isEmpty() ? stackTags.getLast() : null;
		
		// get the current depth of the stack
		final Properties attribs = popAttributes();
		
		// determine which tag handler to invoke
		final TAG_ENUMS tag = TAGS.get( name );
		if( tag != null ) {
			switch( tag ) {
				case AUTHOR_NAME:		authorNameEnd( text ); break;
				case COMMENT:			commentEnd( parentTag, attribs, text ); break;
				case COMPOSITION:		compositionEnd( parentTag, text ); break;
				case MODEL_DESC: 		modelDescriptionEnd( text ); break;
				case MODEL_ELEM:		modelElementEnd( parentTag, attribs ); break;
				case TEXT_NOTE:			textNoteEnd( parentTag, attribs, text ); break;
				case ORGANIZATION_NAME:	organizationNameEnd( text ); break;
				case POLYLINE:			polyLineEnd( parentTag ); break;
				case SPLINE:			splineEnd( parentTag, attribs ); break;
				case USER_IMAGE:		userImageEnd( attribs, text ); break;
			}
		}
		else {
			logger.warn( format( "Element '%s' not recognized", name ) );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement( final String uri, 
							  final String localName, 
							  final String name,
							  final Attributes attributes ) 
	throws SAXException {
		// clear the old text
		characters.delete( 0, characters.length() );
		
		// get the map of attributes
		final Properties attribs = toProperties( attributes );
		
		// get the parent tag
		final TAG_ENUMS parentTag = !stackTags.isEmpty() ? stackTags.getLast() : null;
		
		// capture the current tag
		currentTag = TAGS.get( name );
		stackTags.add( currentTag );
		
		// push the attributes onto the stack
		pushAttributes( attribs );
		
		// identify the tag
		if( currentTag != null ) {
			switch( currentTag ) {
				case AUTHOR_NAME:		break;
				case CIRCLE:			circleStart( parentTag, attribs ); break;
				case CIRCULAR_ARC:		circularArcStart( parentTag, attribs ); break;
				case COMMENT:			break;
				case COMPOSITION:		compositionStart( parentTag, attribs ); break;
				case ELLIPSE:			ellipseStart( parentTag, attribs ); break;
				case ELLIPTIC_ARC:		ellipticArcStart( parentTag, attribs ); break;
				case FILTER:			filterStart( parentTag, attribs ); break;
				case INCLUDE_LAYER:		includeLayerStart( parentTag, attribs ); break;
				case LINE:				lineStart( attribs, parentTag ); break;
				case MODEL:				modelStart( attribs ); break;
				case MODEL_DESC:		break;
				case MODEL_ELEM:		break;
				case ORGANIZATION_NAME:	break;
				case PICTURE:			pictureStart( parentTag, attribs ); break;
				case POINT:				pointStart( parentTag, attribs ); break;
				case POLYLINE:			polyLineStart( parentTag, attribs ); break;
				case SPIRAL:			spiralStart( parentTag, attribs ); break;
				case SPLINE:			splineStart( parentTag, attribs ); break;
				case TEXT_NOTE:			break;
				case VERTEX:			vertexStart( parentTag, attribs ); break;
			}
		}
		else {
			logger.warn( format( "Element '%s' not recognized", name ) );
		}
	}
	
	/** 
	 * Handles the 'AuthorName' tag
	 * @param authorName the given author name
	 */
	private void authorNameEnd( final String authorName ) {
		model.setAuthorName( authorName );
	}

	/** 
	 * Handles the 'Circle' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void circleStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the parameters
		final double x		= getParamDouble( attribs, "x" );
		final double y 		= getParamDouble( attribs, "y" );
		final double radius = getParamDouble( attribs, "radius" );
		
		// add the circle to the stack
		attach( new CircleXY( x, y, radius ), parentTag );
	}
	
	/** 
	 * Handles the 'CircularArc' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void circularArcStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the parameters
		final double x		= getParamDouble( attribs, "x" );
		final double y 		= getParamDouble( attribs, "y" );
		final double radius = getParamDouble( attribs, "radius" );
		final Double start 	= getParamDouble( attribs, "startAngle" );
		final Double end 	= getParamDouble( attribs, "endAngle" );
		
		// add the circular arc to the stack
		attach( new ArcXY( x, y, radius, start, end ), parentTag );
	}
	
	/** 
	 * Handles the 'Comment' end tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @param commentText the given comment text
	 * @throws SAXException 
	 */
	private void commentEnd( final TAG_ENUMS parentTag, Properties attribs, final String commentText ) 
	throws SAXException {
		// get the parameters
		final double x 	= getParamDouble( attribs, "x" );
		final double y 	= getParamDouble( attribs, "y" );
		
		// add the comment to the stack
		attach( new CommentXY( new PointXY( x, y ), commentText ), parentTag );
	}
	
	/** 
	 * Handles the 'Composition' start tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void compositionStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// initialize the composition
		final CompositionXY composition = new CompositionXY();
		
		// add the composition to the stack
		stackElems.add( composition );
		
		// capture the composition
		currentComposition = composition;
	}
	
	/** 
	 * Handles the 'Composition' end tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param commentText the given comment text
	 * @throws SAXException 
	 */
	private void compositionEnd( final TAG_ENUMS parentTag, final String commentText ) 
	throws SAXException {
		currentComposition = null;
	}

	/** 
	 * Handles the 'Ellipse' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void ellipseStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the attributes
		final double x 		= getParamDouble( attribs, "x" );
		final double y 		= getParamDouble( attribs, "y" );
		final double width 	= getParamDouble( attribs, "width" );
		final double height	= getParamDouble( attribs, "height" );
		
		// create the curve
		attach( new EllipseXY( x, y, width, height ), parentTag );
	}
	
	/** 
	 * Handles the 'EllipticArc' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void ellipticArcStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the attributes
		final double x 				= getParamDouble( attribs, "x" );
		final double y 				= getParamDouble( attribs, "y" );
		final double width 			= getParamDouble( attribs, "width" );
		final double height 		= getParamDouble( attribs, "height" );
		final Double start 			= getParamDouble( attribs, "startAngle" );
		final Double end 			= getParamDouble( attribs, "endAngle" );
		
		// add the elliptic arc to the model
		attach( new EllipticArcXY( x, y, width, height, start, end ), parentTag );
	}

	/** 
	 * Handles the 'Filter' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void filterStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the attributes
		final String label = getParamString( attribs, "label", true );
		
		// create the filter
		currentFilter = new Filter( label );
		model.addFilter( currentFilter );
	}

	/** 
	 * Handles the 'IncludeLayer' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void includeLayerStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the 'ref' (reference layer) parameter
		final int layer = getParamInt( attribs, "ref", true );
		
		// turn on the selected layer
		currentFilter.setLayerState( layer, true );
	}
	
	/** 
	 * Handles the 'Line' tag
	 * @param attribs the given {@link Properties attributes}
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @throws SAXException 
	 */
	private void lineStart( final Properties attribs, final TAG_ENUMS parentTag ) 
	throws SAXException {
		// get the attributes
		final double x1 = getParamDouble( attribs, "x1" );
		final double y1 = getParamDouble( attribs, "y1" );
		final double x2 = getParamDouble( attribs, "x2" );
		final double y2 = getParamDouble( attribs, "y2" );
		
		// add the line to the model
		attach( new LineXY( x1, y1, x2, y2 ), parentTag );
	}

	/** 
	 * Handles the 'Model' tag
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void modelStart( final Properties attribs ) 
	throws SAXException {
		model.setScale( getParamDouble( attribs, "scale" ) );
		model.setUnit( getParamUnit( attribs, "unit", true ) );
		model.setDraftingStandard( getParamDraftingStandard( attribs, "draftingStandard", true ) );
		model.setDefaultPattern( getParamLinePattern( attribs, "defaultPattern", true ) );
		model.setDefaultColor( getParamColor( attribs, "defaultColor", true ) );
	}
	
	/** 
	 * Handles the 'Element' end tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void modelElementEnd( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// initialize the composition
		final EntityRepresentation entity = stackElems.removeLast();
		//logger.info( format( "Popped element '%s'", entity.getType() ) );
		
		// create the model element
		final ModelElement element = new CxModelElement( entity );
		element.setLabel( getParamString( attribs, "label", true ) );
		element.setColor( getParamColor( attribs, "color", false ) );
		element.setPattern( getParamLinePattern( attribs, "pattern", false ) );
		element.setLayer( getParamInt( attribs, "layer", false ) );
		element.setThickness( getParamInt( attribs, "thickness", false ) );
		
		// add the element to the model
		if( parentTag == TAG_ENUMS.PHYSICAL_ELEMS ) {
			model.addPhysicalElement( element );
		}
		else {
			model.addPhantoms( element );
		}	
	}
	
	/** 
	 * Handles the 'ModelDescription' tag
	 * @param description the given description
	 */
	private void modelDescriptionEnd( final String description ) {
		model.setDescription( description );
	}
	
	/** 
	 * Handles the 'OrganizationName' tag
	 * @param organizationName the given organization name
	 */
	private void organizationNameEnd( final String organizationName ) {
		model.setAuthorOrganization( organizationName );
	}
	
	/** 
	 * Handles the 'Picture' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void pictureStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the parameters
		final String imageName 	= getParamString( attribs, "imageName", true );
		final double x 			= getParamDouble( attribs, "x" );
		final double y 			= getParamDouble( attribs, "y" );
		
		// lookup the user image
		final UserImage image = model.lookupUserImage( imageName );
		if( image == null ) {
			throw new SAXException( format( "Image '%s' not found", image ) );
		}
		
		// attach the picture
		attach( new PictureXY( new PointXY( x, y ), image ), parentTag );
	}
	
	/** 
	 * Handles the 'Point' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void pointStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the parameters
		final double x 	= getParamDouble( attribs, "x" );
		final double y 	= getParamDouble( attribs, "y" );
		
		// add the point to the model
		attach( new PointXY( x, y ), parentTag );
	}
	
	/** 
	 * Handles the 'PolyLine' tag
	 * @param parentTag the given parent {@link TAG_ENUMS tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void polyLineStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {		
		// create the spline
		currentVertexElem = new PolyLineXY();
		
		// attach the spline
		attach( currentVertexElem, parentTag );
	}

	/** 
	 * Handles the 'PolyLine' tag
	 * @param parentTag the given parent {@link TAG_ENUMS tag}
	 * @throws SAXException 
	 */
	private void polyLineEnd( final TAG_ENUMS parentTag ) 
	throws SAXException {
		// clear the spline
		currentVertexElem = null;
	}

	/** 
	 * Handles the 'Spline' tag
	 * @param parentTag the given parent {@link TAG_ENUMS tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void splineStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {		
		// create the spline
		currentVertexElem = new SplineXY();
		
		// attach the spline
		attach( currentVertexElem, parentTag );
	}
	
	/** 
	 * Handles the 'Spline' tag
	 * @param parentTag the given parent {@link TAG_ENUMS tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void splineEnd( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// clear the spline
		currentVertexElem = null;
	}
	
	/** 
	 * Handles the 'Spiral' tag
	 * @param parentTag the given parent {@link TAG_ENUMS tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void spiralStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {		
		// get the parameters
		final double x 			= getParamDouble( attribs, "x" );
		final double y 			= getParamDouble( attribs, "y" );
		final double radius		= getParamDouble( attribs, "radius" );
		final double increment	= getParamDouble( attribs, "increment" );
		final int revolutions	= getParamInt( attribs, "revolutions", true );
		
		// add the spiral to the model
		attach( new SpiralXY( x, y, radius, increment, revolutions ), parentTag );
	}

	/** 
	 * Handles the 'TextNote' end tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @param the given note text
	 * @throws SAXException 
	 */
	private void textNoteEnd( final TAG_ENUMS parentTag, 
							  final Properties attribs, 
							  final String noteText ) 
	throws SAXException {
		// get the parameters
		final double x 			= getParamDouble( attribs, "x" );
		final double y 			= getParamDouble( attribs, "y" );
		
		// attach the note
		attach( new TextNoteXY( new PointXY( x, y ), noteText ), parentTag );
	}

	/** 
	 * Handles the 'UserImage' end tag
	 * @param attribs the given {@link Properties attributes}
	 * @param the given note text
	 * @throws SAXException 
	 */
	private void userImageEnd( final Properties attribs, final String base64data ) 
	throws SAXException {
		// get the parameters
		final String label = getParamString( attribs, "label", true );
		
		try {			
			// get the user image
			final UserImage image = UserImage.createUserImage( label, base64data ) ;
			
			// add the user image to the model
			model.addUserImage( image );
		}
		catch( final IOException e ) {
			e.printStackTrace();
			throw new SAXException( format( "Error decoding image '%s'", label ), e );
		}
	}
	
	/** 
	 * Handles the 'Vertex' tag
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void vertexStart( final TAG_ENUMS parentTag, final Properties attribs ) 
	throws SAXException {
		// get the parameters
		final double x	= getParamDouble( attribs, "x" );
		final double y	= getParamDouble( attribs, "y" );
		
		// attach the vertex to the element
		if( currentVertexElem != null ) {
			currentVertexElem.append( new PointXY( x, y ) ); 
		}
		else {
			logger.error( format( "No vertex contained defined - %s", parentTag ) );
		}
	}

	/** 
	 * Attaches the given drawing element to the model
	 * @param entity the given {@link EntityRepresentation model entity}
	 * @param parentTag the given {@link TAG_ENUMS parent tag}
	 */
	protected void attach( final EntityRepresentation entity, final TAG_ENUMS parentTag ) {
		// if the parent tag is a composition,
		// attach the entity to it
		switch( parentTag ) {
			case COMPOSITION:
				currentComposition.add( entity );
				break;
		
			// otherwise, add the element to the stack
			default:
				stackElems.add( entity );
		}
	}
	
	/**
	 * Pops a set attributes from the stack
	 * @return the {@link Properties attributes}
	 */
	protected Properties popAttributes() {
		final Properties attribs = stackAttribs.removeLast();
		return attribs;
	}
	
	/** 
	 * Pushes the given attributes onto the stack
	 * @param attribs the given {@link Attributes attributes}
	 */
	protected void pushAttributes( final Properties attribs ) {
		stackAttribs.addLast( attribs );
	}

	/**
	 * Returns the contents of the attributes as a properties object
	 * @param attributes the given {@link Attributes attributes}
	 * @return a {@link Properties attributes}
	 */
	protected static Properties toProperties( final Attributes attributes ) {
		final Properties map = new Properties();
		for( int n = 0; n < attributes.getLength(); n++ ) {
			map.put( attributes.getQName( n ), attributes.getValue( n ) );
		}
		return map;
	}
	
	/** 
	 * Returns the integer value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the integer value
	 * @throws SAXException
	 */
	protected static int getParamInt( final Properties attributes, 
									  final String paramName, 
									  final boolean required ) 
	throws SAXException {
		// does the parameter exist?
		if( !attributes.containsKey( paramName ) ) {
			if( required ) {
				throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
			}
			return 0;
		}
		
		// get the parameter value
		final String value = attributes.getProperty( paramName );
		
		// convert the value to a double
		try {
			return Integer.parseInt( value ); 
		}
		catch( final NumberFormatException e ) {
			throw new SAXException( format( "Invalid value '%s' for parameter '%s'", value, paramName ), e );
		}
	}

	/** 
	 * Returns the color value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the color value
	 * @throws SAXException
	 */
	protected static Color getParamColor( final Properties attributes, 
										  final String paramName, 
										  final boolean required ) 
	throws SAXException {
		// get the color string
		final String colorString = getParamString( attributes, paramName, true );
		
		// is it an IGES color index?
		// TODO remove this soon!!!
		if( colorString.length() == 1 ) {
			final int colorIndex = Integer.parseInt( colorString );
			return IGES_COLORS[ colorIndex ];
		}
		
		// get the RGB string (e.g. "E0FFAB")
		else if( colorString.length() != 6 ) {
			throw new SAXException( format( "RGB parameter '%s' is not valid", colorString ) );
		}
		
		// compute RGB integer value
		int rgb = 0;
		int multiplier = 1;
		final char[] rgbChars = colorString.toCharArray();
		for( int n = 0; n < rgbChars.length; n++ ) {
			// get the HEX value of the character
			final char ch = rgbChars[ rgbChars.length - n ];
			final int hex = ( ch >= 'A' && ch <= 'F' ) ? ch - 'A' : ch - '0';
			
			// adjust the RGB value
			rgb += hex * multiplier;
			multiplier *= 0x10;
		}
		
		// return the color value
		return new Color( rgb );
	}

	/** 
	 * Returns the drafting standard value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the color value
	 * @throws SAXException
	 */
	protected static DraftingStandards getParamDraftingStandard( final Properties attributes, 
															   	 final String paramName, 
															   	 final boolean required ) 
	throws SAXException {
		// get the index
		final int index = getParamInt( attributes, paramName, required );
		
		// return the color value
		return DraftingStandards.values()[ index ]; 
	}
	
	/** 
	 * Returns the line pattern value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the line pattern value
	 * @throws SAXException
	 */
	protected static LinePatterns getParamLinePattern( final Properties attributes, 
													 final String paramName, 
													 final boolean required ) 
	throws SAXException {
		// does the parameter exist?
		if( required && !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// get the index
		final int index = getParamInt( attributes, paramName, required );
		
		// return the line pattern value
		return LinePatterns.values()[ index ]; 
	}
	
	/** 
	 * Returns the string value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the string value
	 * @throws SAXException
	 */
	protected static String getParamString( final Properties attributes, 
											final String paramName, 
											final boolean required ) 
	throws SAXException {
		// does the parameter exist?
		if( required && !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// return the parameter value
		return attributes.getProperty( paramName );
	}

	/** 
	 * Returns the unit value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the color value
	 * @throws SAXException
	 */
	protected static Unit getParamUnit( final Properties attributes, 
									  	final String paramName, 
									  	final boolean required ) 
	throws SAXException {
		// get the index
		final int index = getParamInt( attributes, paramName, required );
		
		// return the color value
		return Units.values()[ index ]; 
	}

	/** 
	 * Returns the double value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @return the double value
	 * @throws SAXException
	 */
	protected static double getParamDouble( final Properties attributes, final String paramName ) 
	throws SAXException {
		// does the parameter exist?
		if( !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// get the parameter value
		final String value = attributes.getProperty(paramName );
		
		// convert the value to a double
		try {
			return Double.parseDouble( value ); 
		}
		catch( final NumberFormatException e ) {
			throw new SAXException( format( "Invalid value '%s' for parameter '%s'", value, paramName ), e );
		}
	}
	
	/** 
	 * Returns the double value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the attribute is required
	 * @return the double value
	 * @throws SAXException
	 */
	protected static Double getParamDouble( final Properties attributes, final String paramName, final boolean required ) 
	throws SAXException {
		// does the parameter exist?
		if( required && !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// get the parameter value
		final String value = attributes.getProperty(paramName );
		if( value == null ) {
			return null;
		}
		
		// convert the value to a double
		try {
			return Double.parseDouble( value ); 
		}
		catch( final NumberFormatException e ) {
			throw new SAXException( format( "Invalid value '%s' for parameter '%s'", value, paramName ), e );
		}
	}
	
}
