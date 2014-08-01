package constellation.commands;

import static java.lang.String.format;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import constellation.commands.builtin.AddElementCommand;
import constellation.commands.builtin.AddElementCommandDecoder;
import constellation.commands.builtin.AddUserImageCommandDecoder;
import constellation.commands.builtin.ClearPickedElementCommandDecoder;
import constellation.commands.builtin.ClearSelectedElementsCommandDecoder;
import constellation.commands.builtin.ClearTempElementCommandDecoder;
import constellation.commands.builtin.DeleteElementCommand;
import constellation.commands.builtin.DeleteElementCommandDecoder;
import constellation.commands.builtin.SelectAllCommandDecoder;
import constellation.commands.builtin.SelectElementCommandDecoder;
import constellation.commands.builtin.SelectElementsCommandDecoder;
import constellation.commands.builtin.SelectEntityCommandDecoder;
import constellation.commands.builtin.SetPickedCommandDecoder;
import constellation.commands.builtin.SetTempElementCommandDecoder;
import constellation.commands.builtin.SetTempElementHUDCommandDecoder;
import constellation.commands.builtin.WaitCommandDecoder;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PictureXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.SpiralXY;
import constellation.drawing.entities.SplineXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.drawing.entities.UserImage;
import constellation.drawing.entities.VerticesXY;

/**
 * Constellation Command Manager
 * @author lawrence.daniels@gmail.com
 */
public class CxCommandManager {
	// built-in operation code constants
	public static final Integer ADD_ELEM		= 0x0000;
	public static final Integer ADD_IMG			= 0x0001;
	public static final Integer DEL_ELEM		= 0x0004;
	public static final Integer CLR_PICK		= 0x0008;
	public static final Integer CLR_SEL			= 0x0009;
	public static final Integer CLR_TEMP		= 0x000A;
	public static final Integer SEL_ALL			= 0x000C;
	public static final Integer SEL_ENTITY		= 0x000D;
	public static final Integer SEL_ELEM		= 0x000E;
	public static final Integer SEL_ELEMS		= 0x000F;
	public static final Integer SET_PICK		= 0x0010;
	public static final Integer SET_TEMP		= 0x0011;
	public static final Integer SET_TEMP_HUD	= 0x0012;
	public static final Integer WAIT 			= 0x0014;
	
	// singleton instance
	private static final CxCommandManager instance = new CxCommandManager();
	
	// internal fields
	private final Map<Integer, CxCommandDecoder> decoders;
	
	/**
	 * Default Constructor
	 */
	private CxCommandManager() {
		decoders 				  = new HashMap<Integer, CxCommandDecoder>();
		decoders.put( ADD_ELEM, 	new AddElementCommandDecoder() );
		decoders.put( ADD_IMG, 		new AddUserImageCommandDecoder() );
		decoders.put( DEL_ELEM,		new DeleteElementCommandDecoder() );	
		decoders.put( CLR_PICK, 	new ClearPickedElementCommandDecoder() );
		decoders.put( CLR_SEL, 		new ClearSelectedElementsCommandDecoder() );
		decoders.put( CLR_TEMP, 	new ClearTempElementCommandDecoder() );
		decoders.put( SEL_ALL,		new SelectAllCommandDecoder() );
		decoders.put( SEL_ENTITY,	new SelectEntityCommandDecoder() );
		decoders.put( SEL_ELEM,		new SelectElementCommandDecoder() );
		decoders.put( SEL_ELEMS,	new SelectElementsCommandDecoder() );
		decoders.put( SET_PICK,		new SetPickedCommandDecoder() );
		decoders.put( SET_TEMP,		new SetTempElementCommandDecoder() );
		decoders.put( SET_TEMP_HUD,	new SetTempElementHUDCommandDecoder() );
		decoders.put( WAIT, 		new WaitCommandDecoder() );
	}
	
	/** 
	 * Returns the singleton instance
	 * @return the singleton instance
	 */
	public static CxCommandManager getInstance() {
		return instance;
	}

	/** 
	 * Returns the appropriate decoder for the opCode referenced
	 * by the given buffer.
	 * @param buffer the given {@link ByteBuffer buffer}.
	 * @return the {@link CxCommandDecoder decoder}
	 * @throws IllegalArgumentException if no registered decoder could be retrieved
	 */
	public CxCommandDecoder getDecoder( final ByteBuffer buffer ) {
		// get the opCode
		final Integer opCode = buffer.getInt();
		
		// return the decoder
		final CxCommandDecoder decoder = decoders.get( opCode );
		if( decoder == null ) {
			throw new IllegalArgumentException( format( "OpCode %04Xh is not registered to any known decoder", opCode ) );
		}
		return decoder;
	}
	
	/**
	 * Indicates whether the given opCode is registered to a decoder
	 * @param opCode the given opCode
	 * @return true, if the opCode is registered
	 */
	public boolean isDecoderRegistered( final Integer opCode ) {
		return decoders.containsKey( opCode );
	}
	
	/**
	 * Indicates whether the given decoder is registered to at least one opCode
	 * @param decoder the given {@link CxCommandDecoder command decoder}
	 * @return true, if the decoder is registered
	 */
	public boolean isRegistered( final CxCommandDecoder decoder ) {
		return decoders.values().contains( decoder );
	}
	
	/**
	 * Registers the given command decoder to the given unique operational code
	 * @param opCode the given operation code (e.g. '0x00C0')
	 * @param decoder the given {@link CxCommandDecoder command decoder}
	 */
	public void register( final Integer opCode, final CxCommandDecoder decoder ) {
		synchronized( decoders ) {
			// if the opCode is already registered
			if( decoders.containsKey( opCode ) ) {
				// lookup the registered decoder
				final CxCommandDecoder registeredDecoder = decoders.get( opCode );
				
				// throw the exception
				throw new IllegalStateException( format( "OpCode %04Xh is already registered to '%s'", 
						opCode, registeredDecoder.getClass().getName() ) );
			}
			
			// register the mapping
			decoders.put( opCode, decoder );
		}
	}
	
	/** 
	 * Creates a command to replicate the given geometry on a remote host
	 * @param element the given {@link ModelElement model element}
	 * @return a {@link CxCommand replication command}
	 */
	public static CxCommand createAddCommand( final ModelElement element ) {
		return AddElementCommand.create( element );
	}
	
	/** 
	 * Creates a collection of commands to replicate the given geometry on a remote host
	 * @param elements the given array of {@link ModelElement elements}
	 * @return a {@link Collection collection} of {@link CxCommand replication commands}
	 */
	public static Collection<CxCommand> createAddCommands( final ModelElement[] elements ) {
		final Collection<CxCommand> requests = new LinkedList<CxCommand>();
		for( final ModelElement element : elements ) {
			final CxCommand request = createAddCommand( element );
			if( request != null ) {
				requests.add( request );
			}
		}
		return requests;
	}
	
	/** 
	 * Creates an array of operations capable of creating the given 
	 * geometry on a remote host
	 * @param elements the given collection of {@link ModelElement model elements}
	 * @return a {@link Collection collection} of {@link CxCommand synchronization data}
	 */
	public static Collection<CxCommand> createAddCommands( final Collection<? extends ModelElement> elements ) {
		final Collection<CxCommand> requests = new LinkedList<CxCommand>();
		for( final ModelElement element : elements ) {
			final CxCommand request = createAddCommand( element );
			if( request != null ) {
				requests.add( request );
			}
		}
		return requests;
	}
	
	/** 
	 * Creates an array of operations capable of removing the given 
	 * geometry on a remote host
	 * @param elements the given array of {@link ModelElement elements}
	 * @return a {@link Collection collection} of {@link CxCommand synchronization data}
	 */
	public static Collection<CxCommand> createDeleteCommands( final Collection<ModelElement> elements ) {
		final Collection<CxCommand> requests = new LinkedList<CxCommand>();
		for( final ModelElement element : elements ) {
			final CxCommand request = DeleteElementCommand.create( element );
			if( request != null ) {
				requests.add( request );
			}
		}
		return requests;
	}

	/**
	 * Decodes the color object from the given buffer
	 * @param buffer the given buffer
	 * @return the {@ink Color color} object
	 */
	public static Color decodeColor( final ByteBuffer buffer ) {
		// get the color in RGB
		final int rgb = buffer.getInt();
		
		// return the color instance
		return new Color( rgb );
	}

	/**
	 * Encodes the given color to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param color the given {@link Color color}
	 * @throws IOException 
	 */
	public static void encodeColor( final DataOutputStream stream, final Color color ) 
	throws IOException {
		// if the string is null, write zero
		if( color == null ) {
			stream.writeInt( 0 );
		}
		else {
			// write the RGB value
			stream.writeInt( color.getRGB() );
		}
	}

	/**
	 * Decodes the binary content from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the binary content
	 */
	public static byte[] decodeContent( final ByteBuffer buffer ) {
		// get the length of the content
		final int length = buffer.getInt();
		
		// create a buffer for the content
		final byte[] bytes = new byte[ length ];
		
		// read the bytes into the buffer
		buffer.get( bytes );
		
		// return the string
		return bytes;
	}
	
	/**
	 * Encodes the given binary content to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param bytes the given binary content
	 * @throws IOException 
	 */
	public static void encodeContent( final DataOutputStream stream, final byte[] bytes ) 
	throws IOException {
		// append the length of the string
		stream.writeInt( bytes.length );
		
		// append the bytes
		stream.write( bytes );
	}
	
	/**
	 * Decodes the date from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link Date date}
	 */
	public static Date decodeDate( final ByteBuffer buffer ) {
		// get the time in milliseconds 
		final long timeInMillis = buffer.getLong();
		
		// return the date
		return new Date( timeInMillis );
	}
	
	/**
	 * Encodes the given date to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param date the given {@ink Date date}
	 * @throws IOException 
	 */
	public static void encodeDate( final DataOutputStream stream, final Date date ) 
	throws IOException {
		stream.writeLong( date.getTime() );
	}
	
	/**
	 * Decodes the HUD from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link HUDXY HUD}
	 */
	public static HUDXY decodeHUD( final ByteBuffer buffer ) 
	throws IOException {
		// create the HUD
		final HUDXY hud = new HUDXY();
		
		// retrieve the text lines
		final short lineCount = buffer.getShort();
		for( int n = 0; n < lineCount; n++ ) {
			// retrieve & append the text line
			hud.append( decodeString( buffer ) );
		}
		
		// retrieve the elements
		final short elementCount = buffer.getShort();
		for( int n = 0; n < elementCount; n++ ) {
			// retrieve & append the element
			hud.add( decodeRepresentation( buffer ) );
		}
		
		// return the HUD
		return hud;
	}
	
	/**
	 * Encodes the given HUD to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param hud the given {@ink HUDXY HUD}
	 * @throws IOException 
	 */
	public static void encodeHUD( final DataOutputStream stream, final HUDXY hud ) 
	throws IOException {
		// get the collection of lines
		final Collection<String> lines = hud.getLines();
		stream.writeShort( lines.size() );
		
		// write the line data
		for( final String line : lines ) {
			// write the line data (length & content)
			stream.writeShort( line.length() );
			stream.writeChars( line );
		}
		
		// get the collection of elements
		final Collection<RenderableElement> elements = hud.getElements();
		stream.writeShort( elements.size() );
		
		// write the elements
		for( final RenderableElement element : elements ) {
			// is the element an entity representation?
			if( element instanceof EntityRepresentation ) {
				final EntityRepresentation entity = (EntityRepresentation)element;
				encodeRepresentation( stream, entity );
			}
			
			// unrecognized type
			else {
				throw new IOException( format( "Unhandled element type '%s'", element.getClass().getSimpleName() ) );
			}
		}
	}
	
	/**
	 * Decodes the points from the given buffer
	 * @param buffer the given buffer
	 * @return the array of {@link PointXY points}
	 */
	public static PointXY[] decodePoints( final ByteBuffer buffer ) {
		// get the number of points
		final int length = buffer.getShort();
		
		// retrieve the points
		final PointXY[] points = new PointXY[ length ];
		for( int n = 0; n < length; n++ ) {
			final double x = buffer.getDouble();
			final double y = buffer.getDouble();
			points[n] = new PointXY( x, y );
		}
		
		// return the point array
		return points;
	}
	
	/**
	 * Encodes the given array of points to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param points the given {@link PointXY array of points}
	 * @throws IOException 
	 */
	public static void encodePoints( final DataOutputStream stream, final PointXY[] points ) 
	throws IOException {
		// append the number of points
		stream.writeShort( points.length );
		
		// append the points
		for( final PointXY point : points ) {
			stream.writeDouble( point.getX() );
			stream.writeDouble( point.getY() );
		}
	}
	
	/**
	 * Decodes the character string from the given buffer
	 * @param buffer the given buffer
	 * @return the character string
	 */
	public static String decodeString( final ByteBuffer buffer ) {
		// get the length of the string
		final short length = buffer.getShort();
		
		// if the length is zero, return null
		if( length == 0 ) {
			return null;
		}
		else {
			// create a buffer for the string
			final byte[] charBuf = new byte[ length ];
			
			// read the bytes into the buffer
			buffer.get( charBuf, 0, length );
			
			// return the string
			return new String( charBuf );
		}
	}

	/**
	 * Encodes the given character string to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param string the given character string
	 * @throws IOException 
	 */
	public static void encodeString( final DataOutputStream stream, final String string ) 
	throws IOException {
		// if the string is null, write zero
		if( string == null ) {
			stream.writeShort( 0 );
		}
		else {
			// get the bytes that make up the string
			final byte[] charBuf = string.getBytes();
			
			// append the length of the string
			stream.writeShort( charBuf.length );
			
			// append the bytes
			stream.write( charBuf );
		}
	}
	
	/**
	 * Decodes the date from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link Date date}
	 * @throws IOException 
	 */
	public static EntityRepresentation decodeRepresentation( final ByteBuffer buffer ) 
	throws IOException {
		// get the type of representation
		final EntityTypes type = EntityTypes.values()[ buffer.get() ];
		
		// decode the representation
		switch( type ) {
			case CIRCLE:		return decodeCircle( buffer ); 
			case ARC:			return decodeCircularArc( buffer );
			case COMPOSITION:	return decodeComposition( buffer );
			case ELLIPSE:		return decodeEllipse( buffer );
			case LINE:			return decodeLine( buffer );
			case POINT:			return decodePoint( buffer );
			case PICTURE:		return decodePicture( buffer );
			case SPLINE:		return decodeSpline( buffer );
			case SPIRAL:		return decodeSpiral( buffer );
			case TEXTNOTE:		return decodeTextNote( buffer );
			default:
				throw new IllegalArgumentException( format( "Unhandled drawing element type '%s'", type ) );
		}
	}
	
	/**
	 * Encodes the given geometric representation to the given stream
	 * @param stream the given {@link DataOutputStream output stream}
	 * @param rep the given {@link EntityRepresentation geometric representation}
	 * @throws IOException 
	 */
	public static void encodeRepresentation( final DataOutputStream stream, 
											 final EntityRepresentation rep ) 
	throws IOException {
		// write the type
		stream.writeByte( rep.getType().ordinal() );
		
		// encode the object itself
		switch( rep.getType() ) {
			case CIRCLE:		encodeCircle( stream, (CircleXY)rep ); break;
			case ARC:			encodeCircularArc( stream, (ArcXY)rep ); break;
			case COMPOSITION:	encodeComposition( stream, (CompositionXY)rep ); break;
			case ELLIPSE:		encodeEllipse( stream, (EllipseXY)rep ); break;
			case LINE:			encodeLine( stream, (LineXY)rep ); break;
			case PICTURE:		encodePicture( stream, (PictureXY)rep ); break;
			case POINT:			encodePoint( stream, (PointXY)rep ); break;
			case SPLINE:		encodeSpline( stream, (SplineXY)rep ); break;
			case SPIRAL:		encodeSpiral( stream, (SpiralXY)rep ); break;
			case TEXTNOTE:		encodeTextNote( stream, (TextNoteXY)rep ); break;
			default:
				throw new IOException( format( "Unhandled geometric type '%s'", rep.getType() ) );
		}
	}

	/** 
	 * Decodes a circle from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link CircleXY circle} instance
	 */
	private static CircleXY decodeCircle( final ByteBuffer buffer ) {
		// decode the attributes
		final double cx = buffer.getDouble();
		final double cy = buffer.getDouble();
		final double r	= buffer.getDouble();
		
		// return the circle
		return new CircleXY( cx, cy, r );
	}
	
	/** 
	 * Decodes an arc from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link ArcXY arc} instance
	 */
	private static ArcXY decodeCircularArc( final ByteBuffer buffer ) {
		// decode the attributes
		final double cx = buffer.getDouble();
		final double cy = buffer.getDouble();
		final double r	= buffer.getDouble();
		final double as	= buffer.getDouble();
		final double ae	= buffer.getDouble();
		
		// return the arc
		return new ArcXY( cx, cy, r, as, ae );
	}
	
	/** 
	 * Decodes a composition from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link CompositionXY composition} instance
	 */
	private static CompositionXY decodeComposition( final ByteBuffer buffer ) 
	throws IOException {
		// get the count of elements to retrieve
		final short count = buffer.getShort();
		
		// create a collection large enough to contain the elements
		final Collection<EntityRepresentation> elements = 
			new ArrayList<EntityRepresentation>( count );
		
		// retrieve the elements
		for( int n = 0; n < count; n++ ) {
			// read an internal representation
			final EntityRepresentation cgr = decodeRepresentation( buffer );
			
			// add it to the collection
			elements.add( cgr );
		}
		
		// return the composition
		return new CompositionXY( elements );
	}
	
	/** 
	 * Decodes an ellipse from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link EllipseXY ellipse} instance
	 */
	private static EllipseXY decodeEllipse( final ByteBuffer buffer ) {
		// decode the attributes
		final double cx = buffer.getDouble();
		final double cy = buffer.getDouble();
		final double w	= buffer.getDouble();
		final double h	= buffer.getDouble();
		
		// return the ellipse
		return new EllipseXY( cx, cy, w, h );
	}
	
	/** 
	 * Decodes a line from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link LineXY line} instance
	 */
	private static LineXY decodeLine( final ByteBuffer buffer ) {
		// decode the attributes
		final double x1 = buffer.getDouble();
		final double y1 = buffer.getDouble();
		final double x2	= buffer.getDouble();
		final double y2	= buffer.getDouble();
		
		// return the line
		return new LineXY( x1, y1, x2, y2 );
	}
	
	/** 
	 * Decodes a picture from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link PictureXY picture} instance
	 */
	private static PictureXY decodePicture( final ByteBuffer buffer ) 
	throws IOException {
		// decode the attributes
		final double x 			= buffer.getDouble();
		final double y 			= buffer.getDouble();
		final String imageName	= decodeString( buffer ); 
		final byte[] content	= decodeContent( buffer );
		final UserImage image	= UserImage.createUserImage( imageName, content );
		
		// return the picture
		return new PictureXY( x, y, image );
	}
	
	/** 
	 * Decodes a point from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link PointXY point} instance
	 */
	private static PointXY decodePoint( final ByteBuffer buffer ) {
		// decode the attributes
		final double x = buffer.getDouble();
		final double y = buffer.getDouble();
		
		// return the point
		return new PointXY( x, y );
	}
	
	/** 
	 * Decodes a spline from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link SplineXY spline} instance
	 */
	private static SplineXY decodeSpline( final ByteBuffer buffer ) {
		// decode the control points
		final PointXY[] points = decodePoints( buffer );
		
		// return the spline
		return new SplineXY( points );
	}
	
	/** 
	 * Decodes a spiral from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link SpiralXY spiral} instance
	 */
	private static SpiralXY decodeSpiral( final ByteBuffer buffer ) {
		// decode the attributes
		final double cx			= buffer.getDouble();
		final double cy 		= buffer.getDouble();
		final double radius		= buffer.getDouble();
		final double increment	= buffer.getDouble();
		final int revolutions	= buffer.getShort();
		
		// return the spiral
		return new SpiralXY( new PointXY( cx, cy ), radius, increment, revolutions );
	}
	
	/** 
	 * Decodes a text note from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link TextNoteXY text note} instance
	 */
	private static TextNoteXY decodeTextNote( final ByteBuffer buffer ) {
		// decode the attributes
		final double x		= buffer.getDouble();
		final double y		= buffer.getDouble();
		final String text	= decodeString( buffer );
		
		// return the note
		return new TextNoteXY( new PointXY( x, y ), text );
	}

	/**
	 * Encodes the given circle to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param circle the given {@link CircleXY circle}
	 * @throws IOException 
	 */
	private static void encodeCircle( final DataOutputStream stream, final CircleXY circle ) 
	throws IOException {
		stream.writeDouble( circle.getCenterX() );
		stream.writeDouble( circle.getCenterY() );
		stream.writeDouble( circle.getRadius() );
	}
	
	/**
	 * Encodes the given circular arc to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param arc the given {@link ArcXY circular arc}
	 * @throws IOException 
	 */
	private static void encodeCircularArc( final DataOutputStream stream, final ArcXY arc ) 
	throws IOException {
		stream.writeDouble( arc.getCenterX() );
		stream.writeDouble( arc.getCenterY() );
		stream.writeDouble( arc.getRadius() );
		stream.writeDouble( arc.getAngleStart() );
		stream.writeDouble( arc.getAngleEnd() );
	}
	
	/**
	 * Encodes the given ellipse to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param ellipse the given {@link EllipseXY ellipse}
	 * @throws IOException 
	 */
	private static void encodeEllipse( final DataOutputStream stream, final EllipseXY ellipse ) 
	throws IOException {
		stream.writeDouble( ellipse.getCenterX() );
		stream.writeDouble( ellipse.getCenterY() );
		stream.writeDouble( ellipse.getWidth() );
		stream.writeDouble( ellipse.getHeight() );
	}
	
	/**
	 * Encodes the given composition to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param comp the given {@link CompositionXY composition}
	 * @throws IOException 
	 */
	private static void encodeComposition( final DataOutputStream stream, final CompositionXY comp ) 
	throws IOException {
		// get the collection of elements
		final List<EntityRepresentation> elements = comp.getElements();
		
		// write the number of elements
		stream.writeShort( elements.size() );
		
		// write the elements
		for( final EntityRepresentation element : elements ) {
			encodeRepresentation( stream, element );
		}
	}
	
	/**
	 * Encodes the given line to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param line the given {@link LineXY line}
	 * @throws IOException 
	 */
	private static void encodeLine( final DataOutputStream stream, final LineXY line ) 
	throws IOException {
		stream.writeDouble( line.getX1() );
		stream.writeDouble( line.getY1() );
		stream.writeDouble( line.getX2() );
		stream.writeDouble( line.getY2() );
	}
	
	/**
	 * Encodes the given picture to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param picture the given {@link PictureXY picture}
	 * @throws IOException 
	 */
	private static void encodePicture( final DataOutputStream stream, final PictureXY picture ) 
	throws IOException {
		// get the location point
		final PointXY p 	= picture.getLocation();
		
		// get the user image
		final UserImage ui	= picture.getUserImage();
		
		// write the (x,y) coordinate, name and content
		stream.writeDouble( p.getX() );
		stream.writeDouble( p.getY() );
		encodeString( stream, ui.getName() );
		encodeContent( stream, ui.getContent() );
	}

	/**
	 * Encodes the given point to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param point the given {@link PointXY point}
	 * @throws IOException 
	 */
	private static void encodePoint( final DataOutputStream stream, final PointXY point ) 
	throws IOException {
		stream.writeDouble( point.getX() );
		stream.writeDouble( point.getY() );
	}
	
	/**
	 * Encodes the given spline to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param spline the given {@link SplineXY spline}
	 * @throws IOException 
	 */
	private static void encodeSpline( final DataOutputStream stream, final SplineXY spline ) 
	throws IOException {				
		// get the vertices
		final VerticesXY vertices = spline.getLimits();
		
		// convert the vertices to control points
		final PointXY[] points = EntityRepresentationUtil.fromLimits( vertices );
		
		// encode the control points
		encodePoints( stream, points );
	}
	
	/**
	 * Encodes the given spiral to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param spiral the given {@link SpiralXY spiral}
	 * @throws IOException 
	 */
	private static void encodeSpiral( final DataOutputStream stream, final SpiralXY spiral ) 
	throws IOException {	
		// get the center point
		final PointXY p = spiral.getMidPoint();
		
		// encode the attributes
		stream.writeDouble( p.getX() );
		stream.writeDouble( p.getY() );
		stream.writeDouble( spiral.getRadius() );
		stream.writeDouble( spiral.getIncrement() );
		stream.writeShort( spiral.getRevolutions() );
	}
	
	/**
	 * Encodes the given note to the given stream
	 * @param stream the given {@link DataOutputStream stream}
	 * @param note the given {@link TextNoteXY note}
	 * @throws IOException 
	 */
	private static void encodeTextNote( final DataOutputStream stream, final TextNoteXY note )
	throws IOException {
		// get the location of the note
		final PointXY p = note.getLocation();
		
		// encode the text note
		stream.writeDouble( p.getX() );
		stream.writeDouble( p.getY() );
		encodeString( stream, note.getTextString() );
	}
	
}
