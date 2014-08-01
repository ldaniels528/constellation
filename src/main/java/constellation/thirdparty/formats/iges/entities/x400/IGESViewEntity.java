package constellation.thirdparty.formats.iges.entities.x400;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES View Entity
 * @author lawrence.daniels@gmail.com
 */
public class IGESViewEntity implements IGESEntity {
	
	/**
	 * Default constructor
	 */
	public IGESViewEntity() {
		super();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.thirdparty.formats.iges.entities.IGESEntity#toGeometry()
	 */
	public ModelElement[] toDrawingElements() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<No Views>";
	}

}
