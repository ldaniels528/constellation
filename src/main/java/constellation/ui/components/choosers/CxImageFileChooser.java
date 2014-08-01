package constellation.ui.components.choosers;

import static java.lang.String.format;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import constellation.CxConfigurationUtil;

/**
 * Constellation Image File Chooser
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxImageFileChooser extends JFileChooser {
	private static final CxImageFileChooser instance = new CxImageFileChooser();
	
	/**
	 * Private constructor
	 */
	private CxImageFileChooser() {
		super( CxConfigurationUtil.getSnapshotsDirectory() );
		
		// attach the file filter
		final ImageFileFilter[] fileFilters = createFileFilters();
		for( ImageFileFilter filter : fileFilters ) {
			super.addChoosableFileFilter( filter );
		}
	}
	
	/** 
	 * Returns the singleton instance of the class
	 * @return the singleton instance of the class
	 */
	public static CxImageFileChooser getInstance() {
		return instance;
	}
	
	/** 
	 * Creates file filters for all available image formats
	 * @return an array of {@link ImageFileFilter file filters}
	 */
	private static ImageFileFilter[] createFileFilters() {
		final Set<String> formatNames = getImageFormats();
		ImageFileFilter[] fileFilters = new ImageFileFilter[ formatNames.size() ];
		int n = 0;
		for( final String formatName : formatNames ) {
			fileFilters[n++] = new ImageFileFilter( formatName );
		}
		return fileFilters;
	}
	
	/**
	 * Returns all available image file formats
	 * @return the set of image file formats
	 */
	private static Set<String> getImageFormats() {
		final Set<String> formats = new HashSet<String>();
		final String[] formatNames = ImageIO.getWriterFormatNames();
		for( final String formatName : formatNames ) {
			formats.add( formatName.toLowerCase() );
		}
		return formats;
	}
	
	/**
	 * Represents an image file filter
	 * @author lawrence.daniels@gmail.com
	 */
	public static class ImageFileFilter extends FileFilter {
		private final String formatName;
		
		/**
		 * Creates a new image file filter
		 * @param formatName the given image format name (e.g. 'jpeg')
		 */
		public ImageFileFilter( final String formatName ) {
			this.formatName = formatName;
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept( final File file ) {
			return file.getName().endsWith( format( ".%s", formatName ) );
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			return String.format( "%s Image", formatName.toUpperCase() );
		}
		
		/**
		 * Reads the image from disk based on the given image file reference
		 * @param imageFile the given image {@link File file reference}
		 * @return the {@link BufferedImage image}
		 * @throws IOException
		 */
		public BufferedImage readImage( final File imageFile ) 
		throws IOException {
			// get the image reader
			final Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName( formatName );
			
			// get the next reader in the iteration
			final ImageReader reader = readers.next();
			
			// read the image from disk
			ImageInputStream iis = ImageIO.createImageInputStream( new FileInputStream( imageFile ) );
			reader.setInput( iis );
			return reader.read( 0 );
		}
		
		/**
		 * Writes the given image to the given file system
		 * @param image the given {@link Image image}
		 * @param file the given {@link File file}
		 * @throws IOException 
		 */
		public void write( final BufferedImage image, final File file ) 
		throws IOException {
			ImageOutputStream out = null;
			try {
				final File imageFile = file.getName().contains( "." )
							? file 
							: new File( String.format( "%s.%s", file.getAbsolutePath(), formatName ) );
				
				// get the image writers
				final Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName( formatName );

				// must be at least one
				if( !imageWriters.hasNext() ) {
					throw new IOException( "No suitable image writer found" );
				}
				
				// get the image writer instance
				final ImageWriter imageWriter = imageWriters.next();
				
				// open the file for writing
				out = ImageIO.createImageOutputStream( new FileOutputStream( imageFile ) );	
				
				// set the output
				imageWriter.setOutput( out );
				
				// write the image to disk
				imageWriter.write( image );
				
				// flush the buffer
				out.flush();
			}
			finally {
				if( out != null ) {
					try { out.close(); } catch( Exception e ) { }
				}
			}
		}
		
	}
	
}
