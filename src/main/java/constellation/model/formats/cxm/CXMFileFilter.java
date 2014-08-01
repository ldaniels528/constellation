package constellation.model.formats.cxm;

import static constellation.model.formats.cxm.CXMFormatReader.EXTENSION;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Constellation Model File Filter
 * @author lawrence.daniels@gmail.com
 */
public class CXMFileFilter extends FileFilter {

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept( final File file ) {
		return file.isDirectory() || 
				file.getName().toLowerCase().endsWith( String.format( ".%s", EXTENSION ) );
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Constellation Model Files";
	}
}
