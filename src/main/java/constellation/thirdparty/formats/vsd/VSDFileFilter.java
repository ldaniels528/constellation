package constellation.thirdparty.formats.vsd;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Microsoft VISIO/VSD File Filter
 * @author lawrence.daniels@gmail.com
 */
class VSDFileFilter extends FileFilter {

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept( final File file ) {
		return file.isDirectory() || 
				file.getName().toLowerCase().endsWith( ".vsd" );
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Visio Files";
	}
}