package constellation.app.functions.tools;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.lang.String.format;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttributeSet;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.drawing.Camera;
import constellation.model.GeometricModel;

/** 
 * Constellation Printing Utility
 * @author lawrence.daniels@gmail.com
 */
public class CxPrintingUtil {
	// define the print orientations
	private static enum PageOrientations { LANDSCAPE, PORTRAIT, REVERSE_LANDSCAPE };
	
	// singleton instance
	private static final CxPrintingUtil instance = new CxPrintingUtil();
	
	// logger instance
	private final Logger logger = Logger.getLogger( getClass() );
	
	/**
	 * Default Constructor
	 */
	private CxPrintingUtil() {
		super();
	}
	
	/** 
	 * Returns the singleton instance
	 * @return the singleton instance
	 */
	public static CxPrintingUtil getInstance() {
		return instance;
	}
	
	/**
	 * Prints the model document
	 * @param controller the given {@link ApplicationController controller}
	 */
	public void print( final ApplicationController controller ) {
		// create a new printer job
		final PrinterJob job = PrinterJob.getPrinterJob();	    
		job.setPrintable( new PrintingAgent( controller ) );
		logger.info( format( "Created print job %s@%s...", job.getUserName(), job.getJobName() ) );
		
		// get the print service information
		final PrintService printService = job.getPrintService();
		final PrintServiceAttributeSet attributeSet = printService.getAttributes();
		final Attribute[] attributes = attributeSet.toArray();
		for( final Attribute attribute : attributes ) {
			logger.info( format( "Attribute: %s", attribute.getName() ) );
		}
		
		// print the document
		if( job.printDialog() ) {
			try {
		        job.print();
			} 
			catch( final PrinterException cause ) {
				controller.showErrorDialog("Error printing document", "Print Error", cause );
			}
		}
	}
	
	/** 
	 * Draws the image for the current page
	 * @param g2d the given {@link Graphics2D graphics context}
	 * @param image the given {@link BufferedImage image}
	 * @param pageX the given page cell X-axis
	 * @param pageY the given page cell Y-axis
	 * @param pageWidth the given page width
	 * @param pageHeight the given page height
	 * @param comp the image observer {@link Component component}
	 */
	private void drawPageImage( final Graphics2D g2d, 
								final BufferedImage image,
								final int pageX, 
								final int pageY, 
								final double pageWidth,
								final double pageHeight, 
								final Component comp ) {
		// setup the rendering hints
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		// compute the source area of extract
		final int sx1 = (int)( pageX * pageWidth );
		final int sy1 = (int)( pageY * pageHeight );
		final int sx2 = sx1 + (int)pageWidth;
		final int sy2 = sy1 + (int)pageHeight; 
		
		// compute the destination area for the image
		final int dx1 = 0;
		final int dy1 = 0;
		final int dx2 = dx1 + (int)pageWidth;
		final int dy2 = dy1 + (int)pageHeight; 
	
		// draw the print image
		g2d.drawImage( image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, comp );
	}

	/** 
	 * Returns the requested page orientation
	 * @param pageFormat the given {@link PageFormat page format}
	 * @return the {@link PageOrientations page orientation}
	 */
	private PageOrientations getPageOrientation( final PageFormat pageFormat ) {
		switch( pageFormat.getOrientation() ) {
			case PageFormat.LANDSCAPE: 
				return PageOrientations.LANDSCAPE; 
			case PageFormat.PORTRAIT: 
				return PageOrientations.PORTRAIT; 
			case PageFormat.REVERSE_LANDSCAPE: 
				return PageOrientations.REVERSE_LANDSCAPE; 
			default:
				return PageOrientations.LANDSCAPE;
		}
	}

	/** 
	 * Generates the printable image
	 * @param controller the given {@link ApplicationController controller}
	 * @return the {@link BufferedImage image}
	 */
	private BufferedImage getFullPrintImage( final ApplicationController controller ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the drawing dimensions
		final Dimension dimension = controller.getDrawingDimensions();
		
		// get the camera instance
		final Camera camera = controller.getCamera();
		
		// create a buffered image
		final BufferedImage image = 
			new BufferedImage( dimension.width, dimension.height, TYPE_3BYTE_BGR );
		
		// get the snapshot of the model
		camera.render( controller, model, image );
		
		// open the 'Save As' dialog
		return image;
	}

	/** 
	 * Sets the user defined transform
	 * @param g2d the given {@link Graphics2D graphics context}
	 * @param pageFormat the given {@link PageFormat page format}
	 */
	private void setTransform( final Graphics2D g2d, final PageFormat pageFormat ) {
		// get the matrix
		final double[] m = pageFormat.getMatrix();
		
		// cache the individual values
		final double m00 = m[0], m10 = m[1], m01 = m[2], 
					 m11 = m[3], m02 = m[4], m12 = m[5];
		
		// set the transform
		g2d.getTransform().setTransform( m00, m10, m01, m11, m02, m12 );
	}
	
	/** 
	 * Constellation Printing Agent
	 * @author lawrence.daniels@gmail.com
	 */
	private class PrintingAgent implements Printable {
		private final ApplicationController controller;
		
		/**
		 * Creates a new printing agent
		 * @param controller the given {@link ApplicationController controller}
		 */
		public PrintingAgent( final ApplicationController controller ) {
			this.controller = controller;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public int print( final Graphics g, 
						  final PageFormat format, 
						  final int page )
		throws PrinterException {
			// get the enhanced graphics context
			final Graphics2D g2d = (Graphics2D)g;
			
			// TODO figure out how to determine the scale of the print being requested
			
			logger.info( format( "matrix = %s", toMatrixString( format.getMatrix() ) ) );
			
			// get the full image
			final BufferedImage fullImage = getFullPrintImage( controller );
			
			// get the page orientation
			final PageOrientations orientation = getPageOrientation( format );
			
			// get the width and height of the page 
			final Paper paper		= format.getPaper();
			final double pageWidth	= paper.getWidth();
			final double pageHeight = paper.getHeight();
			
			// get the width and height of the image 
			final int imageWidth	= fullImage.getHeight();
			final int imageHeight	= fullImage.getHeight();
			
			// compute the number of horizontal pages
			final int pagesH 		= (int)( imageWidth / pageWidth ) + ( ( imageWidth % pageWidth ) > 0 ? 1 : 0 );
			final int pagesV 		= (int)( imageHeight / pageHeight ) + ( ( imageHeight % pageHeight ) > 0 ? 1 : 0 );
			final int pages  		= pagesH * pagesV;
			
			if( page < pages ) {
				// compute the page cell 
				final int pageX = page % pagesH;
				final int pageY = page % pagesV;
				
				logger.info( format( "pageWidth = %3.2f, pageHeight = %3.2f, imageWidth = %d, imageHeight = %d, " +
									 "page = %d of %d, pagesH = %d, pagesV = %d, pageX = %d, pageY = %d",  
									 pageWidth, pageHeight, imageWidth, imageHeight, 
									 page, pages, pagesH, pagesV, pageX, pageY ) );
				
				// set the print transform
				setTransform( g2d, format );
				
				// draw the image onto the graphics context		
				drawPageImage( g2d, fullImage, pageX, pageY, pageWidth, pageHeight, (Component)controller );
				
				// return page exists
				return PAGE_EXISTS;
			}
			else {
				return NO_SUCH_PAGE;
			}
		}
	}
	
	/**
	 * Converts the given matrix array into a string
	 * @param matrix the given matrix array
	 * @return the matrix string
	 */
	private String toMatrixString( final double[] matrix ) {
		final StringBuilder sb = new StringBuilder();
		for( final double value : matrix ) {
			sb.append( format( "%3.2f ", value ) );
		}
		return sb.toString();
	}
	
	/** 
	 * Constellation Print Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class PrintAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Print action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public PrintAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed( ActionEvent event ) {
			instance.print( controller );
		}
	}
	
}
