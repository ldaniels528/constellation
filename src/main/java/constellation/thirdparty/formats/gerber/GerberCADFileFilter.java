package constellation.thirdparty.formats.gerber;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * GERBER CAD File Filter
 * @author lawrence.daniels@gmail.com
 */
public class GerberCADFileFilter extends FileFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept( final File file ) {
		return file.isDirectory() || 
				file.getName().toLowerCase().endsWith( ".ger" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "Gerber CAD Files";
	}
}