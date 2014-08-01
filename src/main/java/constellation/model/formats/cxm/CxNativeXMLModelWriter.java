package constellation.model.formats.cxm;

import static constellation.drawing.EntityTypes.ARC;
import static constellation.drawing.EntityTypes.CIRCLE;
import static constellation.drawing.EntityTypes.COMMENT;
import static constellation.drawing.EntityTypes.COMPOSITION;
import static constellation.drawing.EntityTypes.ELLIPSE;
import static constellation.drawing.EntityTypes.ELLIPTIC_ARC;
import static constellation.drawing.EntityTypes.LINE;
import static constellation.drawing.EntityTypes.PICTURE;
import static constellation.drawing.EntityTypes.POINT;
import static constellation.drawing.EntityTypes.POLYLINE;
import static constellation.drawing.EntityTypes.SPIRAL;
import static constellation.drawing.EntityTypes.SPLINE;
import static constellation.drawing.EntityTypes.TEXTNOTE;
import static constellation.util.StringUtil.notNull;
import static java.lang.String.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
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
import constellation.model.Filter;
import constellation.model.GeometricModel;

/**
 * Constellation Native Model File Writer
 * @author lawrence.daniels@gmail.com
 */
class CxNativeXMLModelWriter {
	// singleton instance
	private static final CxNativeXMLModelWriter instance = new CxNativeXMLModelWriter();
	
	// internal fields
	private final Logger logger = Logger.getLogger( getClass() );
	private final Map<EntityTypes, DrawingElementWriter> defaultWriters;
	
	/**
	 * Private constructor
	 */
	private CxNativeXMLModelWriter() {
		this.defaultWriters	 = createDefaultWriterMapping();
	}

	/**
	 * Saves the given project to disk
	 * @param model the given {@link GeometricModel model}
	 * @throws IOException
	 */
	public static void writeFile( final GeometricModel model ) 
	throws IOException {
		PrintWriter out = null;
		try {
			// open the file for writing
			out = new PrintWriter( model.getModelFile() );
			
			// write the model object
			out.println( "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" );
			instance.write( out, model );
			
			// flush the buffer
			out.flush();
		}
		finally {
			if( out != null ) {
				try { out.close(); } catch( Exception e ) { } 
			}
		}
	}
	
	/**
	 * Saves the given project to disk
	 * @param model the given {@link GeometricModel model}
	 */
	public void write( final PrintWriter out, final GeometricModel model ) {
		// append the 'Model' start tag
		out.printf( "<Model scale='%.4f' unit='%d' draftingStandard='%d' defaultPattern='%d' defaultColor='%06X'>\n",
				model.getScale(), 
				model.getUnit().ordinal(),
				model.getDraftingStandard().ordinal(), 
				model.getDefaultPattern().ordinal(), 
				model.getDefaultColor().getRGB() );
		
		// append the description
		out.printf( "<ModelDescription>%s</ModelDescription>\n", notNull( model.getDescription() ) );
		out.printf( "<AuthorName>%s</AuthorName>\n", notNull( model.getAuthorName() ) );
		out.printf( "<OrganizationName>%s</OrganizationName>\n", notNull( model.getAuthorOrganization() ) );
		
		// append the filters
		writeFilters( out, model.getFilters() );
		
		// append user image definitions
		writeUserImages( out, model.getUserImages() );
		
		// create a container for the physical and phantom elements
		final LinkedList<ModelElement> container = new LinkedList<ModelElement>();
		
		// append the physical elements
		writePhysicalElements( out, model.getPhysicalElements() );
		
		// append the phantom elements
		model.getPhantomElements( container );
		writePhantomElements( out, container );
			
		// append the 'Model' end tag
		out.println( "</Model>" );
	}
	
	/** 
	 * Writes the model element to the given output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param element the given {@link ModelElement model element}
	 */
	public void writeElement( final PrintWriter out, final ModelElement element ) {
		out.printf( "<Element label='%s' layer='%d' color='%06X' pattern='%d'>\n", 
				element.getLabel(), 
				element.getLayer(), 
				element.getColor().getRGB(), 
				element.getPattern().ordinal() );
		
		// write the drawing element
		writeDrawingElement( out, element.getRepresentation() );
		
		// close the element
		out.println( "</Element>" );
	}
	
	public void writeDrawingElement( final PrintWriter out, final EntityRepresentation element ) {
		// lookup the appropriate writer for the element
		final DrawingElementWriter writer = lookupDrawingElementWriter( element );
		
		// write the element
		if( writer != null ) {
			writer.write( out, element );
		}
		
		// otherwise, log the error
		else {
			logger.error( format( "No writer found for element type '%s'", element.getClass().getName() ) );
		}
	}
	
	/**
	 * Persists the given line to the given output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param filter the given {@link Filter filter}
	 */
	public void writeFilter( final PrintWriter out, final Filter filter ) {
		out.printf( "<Filter label='%s'>\n", filter.getName()  );
		
		final boolean[] islayerIncluded = filter.getLayerStates();
		for( int layerId = 0; layerId < islayerIncluded.length; layerId++ ) {
			if( islayerIncluded[layerId] ) {
				out.printf( "<IncludeLayer ref='%d' />\n", layerId );
			}
		}
		out.println( "</Filter>" );
	}

	/**
	 * Persists the given line to the given output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param filters the given collection of {@link Filter filters}
	 */
	public void writeFilters( final PrintWriter out, final Collection<Filter> filters ) {
		for( final Filter filter : filters ) {
			if( !filter.isSystemFilter() ) {
				writeFilter( out, filter );
			}
		}
	}

	/**
	 * Persists the lines and curves from the given geometry set 
	 * to the given output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param elements the given array of {@link ModelElement model elements}
	 */
	public void writePhysicalElements( final PrintWriter out, final Collection<ModelElement> elements ) {
		out.println( "<PhysicalElements>" );
		for( final ModelElement element : elements ) {
			writeElement( out, element );
		}
		out.println( "</PhysicalElements>" );
	}
	
	/**
	 * Persists the lines and curves from the given geometry set 
	 * to the given output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param elements the given array of {@link ModelElement drawing elements}
	 */
	public void writePhantomElements( final PrintWriter out, final Collection<ModelElement> elements ) {
		out.println( "<PhantomElements>" );
		for( final ModelElement element : elements ) {
			writeElement( out, element );
		}
		out.println( "</PhantomElements>" );
	}

	/**
	 * Persists the user images to the output stream
	 * @param out the given {@link PrintWriter output stream}
	 * @param userImages the given {@link Map mapping} of {@link UserImage user images}
	 */
	public void writeUserImages( final PrintWriter out, final Collection<UserImage> userImages ) {
		out.println( "<UserImages>" );
		for( final UserImage image : userImages ) {
			out.printf( "<UserImage label='%s'>%s</UserImage>\n", image.getName(), image.getContentAsBase64() );
		}
		out.println( "</UserImages>" );
	}
	
	/**
	 * Creates a mapping of geometric element classes to geometry writers
	 * @return a mapping of {@link ModelElement geometric element} classes 
	 * to {@link DrawingElementWriter geometry writers}
	 */
	private Map<EntityTypes, DrawingElementWriter> createDefaultWriterMapping() {
		final Map<EntityTypes, DrawingElementWriter> map = new HashMap<EntityTypes, DrawingElementWriter>();
		map.put( CIRCLE, 		new Writer_Circle() );
		map.put( ARC,	new Writer_CircularArc() );
		map.put( COMMENT, 		new Writer_Comment() );
		map.put( COMPOSITION, 	new Writer_Composition() );
		map.put( ELLIPSE, 		new Writer_Ellipse() );
		map.put( ELLIPTIC_ARC,	new Writer_EllipticArc() );
		map.put( LINE, 			new Writer_Line() );
		map.put( PICTURE, 		new Writer_Picture() );
		map.put( POINT,			new Writer_Point() );
		map.put( POLYLINE,		new Writer_PolyLine() );
		map.put( SPIRAL, 		new Writer_Spiral() );
		map.put( SPLINE, 		new Writer_Spline() );
		map.put( TEXTNOTE, 		new Writer_TextNote() );
		return map;
	}

	/**
	 * Attempts to retrieve the appropriate writer for persisting the given element
	 * @param element the given {@link EntityRepresentation element}
	 * @return the {@link DrawingElementWriter writer} or <tt>null</tt> if not found
	 */
	private DrawingElementWriter lookupDrawingElementWriter( final EntityRepresentation element ) {
		return defaultWriters.get( element.getType() );
	}

	/** 
	 * Writer for Comment Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Comment implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.DrawingElementWriter#write(java.io.PrintWriter, constellation.drawing.entities.representations.GeometricRepresentation)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element) {
			final CommentXY comment = (CommentXY)element;
			final PointXY location = comment.getLocation();
			out.printf( "<Comment x='%.4f' y='%.4f'>%s</Comment>\n", 
					location.getX(), 
					location.getY(),
					comment.getTextString() );
		}	
	}
	
	/** 
	 * Writer for Composition Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Composition implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.drawing.ModelElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element) {
			// get the necessary details
			final CompositionXY composition = (CompositionXY)element;
			final List<EntityRepresentation> entities = composition.getElements();
			
			// write the XML
			out.println( "<Composition>" );
			for( final EntityRepresentation entity : entities ) {
				writeDrawingElement( out, entity );
			}
			out.println( "</Composition>" );
		}	
	}
	
	/** 
	 * Writer for Line Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Line implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) { 
			final LineXY line = (LineXY)element;;
			out.printf( "<Line x1='%.4f' y1='%.4f' x2='%.4f' y2='%.4f' />\n", 
					line.getX1(), line.getY1(), line.getX2(), line.getY2() );
		}
	}
	
	/** 
	 * Writer for Picture Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Picture implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final PictureXY picture = (PictureXY)element;
			final PointXY location = picture.getLocation();
			out.printf( "<Picture x='%.4f' y='%.4f' imageName='%s' />\n", 
					location.getX(),
					location.getY(),
					picture.getUserImage().getName() );
		}	
	}
	
	/** 
	 * Writer for Point Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Point implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final PointXY p = (PointXY)element;
			out.printf( "<Point x='%.4f' y='%.4f' />\n", p.getX(), p.getY() );
		}	
	}
	
	/** 
	 * Writer for PolyLine Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_PolyLine implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final PolyLineXY polyLine = (PolyLineXY)element;
			out.println( "<PolyLine>" );
			
			// encode the vertices
			final PointXY[] vertices = EntityRepresentationUtil.fromLimits( polyLine.getLimits() );
			for( final PointXY vertex : vertices ) {
				out.printf( "<Vertex x='%.4f' y='%.4f'/>\n", vertex.getX(), vertex.getY() );
			}
			out.println( "</PolyLine>" );
		}	
	}
	
	/** 
	 * Writer for Text Note Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_TextNote implements DrawingElementWriter {

		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final TextNoteXY note = (TextNoteXY)element;
			final PointXY p = note.getLocation();
			out.printf( "<TextNote x='%.4f' y='%.4f'>%s</TextNote>\n", p.getX(), p.getY(), note.getTextString() );
		}	
	}
	
	/** 
	 * Writer for Circle Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Circle implements DrawingElementWriter {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) { 
			final CircleXY circle = (CircleXY)element; 
			out.printf( "<Circle x='%.4f' y='%.4f' radius='%.4f' />\n", 
					circle.getCenterX(),
					circle.getCenterY(),
					circle.getRadius() );
		}
	}
	
	/** 
	 * Writer for Circular Arc Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_CircularArc implements DrawingElementWriter {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final ArcXY arc = (ArcXY)element; 
			out.printf( "<CircularArc x='%.4f' y='%.4f' radius='%.4f' startAngle='%.4f' endAngle='%.4f' />\n",  
					arc.getCenterX(),
					arc.getCenterY(),
					arc.getRadius(),
					arc.getAngleStart(),
					arc.getAngleEnd() );
		}
	}
	
	/** 
	 * Writer for Elliptic Arc Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_EllipticArc implements DrawingElementWriter {
	
		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final EllipticArcXY arc = (EllipticArcXY)element; 
			out.printf( "<EllipticArc x='%.4f' y='%.4f' width='%.4f' height='%.4f' startAngle='%.4f' endAngle='%.4f' />\n", 
					arc.getCenterX(),
					arc.getCenterY(),
					arc.getWidth(),
					arc.getHeight(),
					arc.getAngleStart(),
					arc.getAngleEnd() );
		}
	}
	
	/** 
	 * Writer for Ellipse Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Ellipse implements DrawingElementWriter {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final EllipseXY ellipse = (EllipseXY)element;
			out.printf( "<Ellipse x='%.4f' y='%.4f' width='%.4f' height='%.4f' />\n",
					ellipse.getCenterX(),
					ellipse.getCenterY(),
					ellipse.getWidth(),
					ellipse.getHeight() );
		}
	}
	
	/** 
	 * Writer for Spline Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Spline implements DrawingElementWriter {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final SplineXY spline = (SplineXY)element; 
			out.println( "<Spline>" );
			
			// encode the vertices
			final PointXY[] vertices = EntityRepresentationUtil.fromLimits( spline.getLimits() );
			for( final PointXY vertex : vertices ) {
				out.printf( "<Vertex x='%.4f' y='%.4f'/>\n", vertex.getX(), vertex.getY() );
			}
			out.println( "</Spline>" );
		}
	}
	
	/** 
	 * Writer for Spiral Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class Writer_Spiral implements DrawingElementWriter {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.model.formats.cxm.CxNativeXMLModelWriter.DrawingElementWriter#write(java.io.PrintWriter, constellation.math.geometric.GeometricElement)
		 */
		public void write( final PrintWriter out, final EntityRepresentation element ) {
			final SpiralXY spiral = (SpiralXY)element;
			out.printf( "<Spiral x='%.4f' y='%.4f' radius='%.4f' increment='%.4f' revolutions='%d' />\n", 
					spiral.getCenterX(),
					spiral.getCenterY(),
					spiral.getRadius(),
					spiral.getIncrement(),
					spiral.getRevolutions() );
		}
	}
	
}