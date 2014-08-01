package constellation.thirdparty.formats.iges.entities;

import constellation.drawing.elements.ModelElement;

/**
 * IGES Faux Entity
 * @author lawrence.daniels@gmail.com
 */
public class IGESFauxEntity extends IGESAbstractEntity 
implements IGESEntity {
	
	/**
	 * Default constructor
	 */
	public IGESFauxEntity() {
		super();
	}

	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		return null;
	}

}
