package constellation.thirdparty.formats.iges.entities.x200;

import static java.awt.Color.BLUE;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.LineXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES Leader Arrow
 * @author lawrence.daniels@gmail.com
 */
public class IGESLeaderArrow implements IGESEntity {
	private final Collection<TailSection> tails;
	private final double width;
	private final double height;
	private final double xh;
	private final double yh;
	private final double zt;

	public IGESLeaderArrow( final double width,
							final double height,
							final double xh, 
							final double yh, 
							final double zt ) {
		this.tails	= new LinkedList<TailSection>();
		this.width	= width;
		this.height	= height;
		this.xh 	= xh;
		this.yh 	= yh;
		this.zt		= zt;
	}
	
	/** 
	 * Adds a tail section to the leader arrow
	 * @param xt the x-coordinate of the tail
	 * @param yt the y-coordinate of the tail
	 */
	public void addTail( final double xt, final double yt ) {
		tails.add( new TailSection( xt, yt ) );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.formats.iges.entities.IGESEntity#toGeometry()
	 */
	public ModelElement[] toDrawingElements() {
		final ModelElement[] geometry = new ModelElement[ tails.size() ];
		int n = 0;
		for( TailSection tail : tails ) {
			geometry[n++] = tail.toGeometry();
		}
		return geometry;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return tails.toString();
	}

	/**
	 * IGES Leader Arrow Tail Section
	 * @author lawrence.daniels@gmail.com
	 */
	private class TailSection {
		private final double xt; 
		private final double yt;

		/** 
		 * Creates a new tail section
		 * @param xt the x-coordinate of the tail
		 * @param yt the y-coordinate of the tail
		 */
		public TailSection( final double xt, final double yt ) {
			this.xt = xt;
			this.yt = yt;
		}

		/**
		 * Returns the geometry represented by the given entity
		 * @return the {@link ModelElement geometry}
		 */
		public ModelElement toGeometry() {
			// create the leader arrow
			final ModelElement line = new CxModelElement( new LineXY( xh, yh, xt, yt ) );
			line.setColor( BLUE );
			return line;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return String.format( "(%.2f,%.2f)-(%.2f,%.2f), zt=%.2f, w=%.2f, h=%.2f", xh, yh, xt, yt, zt, width, height );
		}
		
	}
	
}
