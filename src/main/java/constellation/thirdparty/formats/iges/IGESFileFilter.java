package constellation.thirdparty.formats.iges;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * IGES File Filter
 * @author lawrence.daniels@gmail.com
 */
class IGESFileFilter extends FileFilter {

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept( final File file ) {
		return file.isDirectory() || 
				file.getName().toLowerCase().endsWith( ".igs" ) ||
				file.getName().toLowerCase().endsWith( ".iges" );
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "IGES Files";
	}
}