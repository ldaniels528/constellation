package constellation.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import constellation.drawing.EntityTypes;
import constellation.drawing.elements.ModelElement;

/**
 * Represents a drawing element collection
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class DrawingElementCollection implements Collection<ModelElement> {
	private final ModelElementCollection[] layers;
	private ModelElementCollection allLayers;
	private ModelElementCollection layer;
	
	/**
	 * Creates a new instance of the element collection
	 */
	public DrawingElementCollection() {
		this.allLayers	= new ModelElementCollection();
		this.layers 	= new ModelElementCollection[ 256 ];
		
		// set the active layer
		setCurrentLayer( 0 );
	}
	
	/**
	 * Sets the "active" layer
	 * @param layerIndex the given layer index
	 */
	public void setCurrentLayer( final int layerIndex ) {
		// initial the layer, if not already initialized
		if( layers[layerIndex] == null ) {
			layers[layerIndex] = new ModelElementCollection();
		}
		
		// set the "active" layer
		layer = layers[layerIndex];
	}
	
	/**
	 * Copies the visible geometry based on the given filter into the given container.
	 * @param filter the given {@link Filter filter}
	 * @param container the container for the returned collection of {@link ModelElement elements}
	 */
	public void filter( final Filter filter, final Collection<ModelElement> container ) {
		// clear the container (if not empty)
		if( !container.isEmpty() ) {
			container.clear();
		}
		
		// if no filter is applied, return everything
		if( filter == null ) {
			container.addAll( Collections.synchronizedCollection( allLayers ) );
		}

		// filter all elements
		else {
			for( int n = 0; n < layers.length; n++ ) {
				if( ( layers[n] != null ) && filter.containsLayer( n ) ) {
					container.addAll( layers[n] );
				}
			}
		}	
	}
	
	/**
	 * Copies the visible geometry based on the given filter into the given container.
	 * @param filter the given {@link Filter filter}
	 * @param container the container for the returned collection of {@link ModelElement elements}
	 * @param typeSet the given set of {@link EntityTypes types}
	 */
	public void filter( final Filter filter, 
						final Collection<ModelElement> container,
						final Set<EntityTypes> typeSet ) {
		// clear the container (if not empty)
		if( !container.isEmpty() ) {
			container.clear();
		}
		
		// if no filter is applied, use everything
		if( filter == null ) {
			for( final ModelElement element : allLayers ) {
				if( typeSet.contains( element.getType() ) ) {
					container.add( element );
				}
			}
		}
		
		// filter all elements
		else {
			for( int n = 0; n < layers.length; n++ ) {
				if( filter.containsLayer( n ) ) {
					for( final ModelElement element : layers[n] ) {
						if( typeSet.contains( element.getType() ) ) {
							container.add( element );
						}
					}
				}
			}	
		}
	}
	
	/**
	 * Returns the entire collection of elements
	 * @return the entire collection of {@link ModelElement elements}
	 */
	public Collection<ModelElement> getAllLayers() {
		return Collections.synchronizedCollection( allLayers );
	}

	/**
	 * Attempts to retrieve an element by label
	 * @param label the given label to search for
	 * @return the {@link ModelElement element} or <tt>null</tt>, if not found.
	 */
	public ModelElement lookupElementByLabel( final String label ) {
		// iterate the two-dimensional collection
		for( final ModelElement element : this ) {
			if( element.getLabel().equals( label ) ) {
				return element;
			}
		}

		// not found
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add( final ModelElement element ) {
		// get the layer index for the element
		final int layerIndex = element.getLayer();
		
		// make sure the layer has been initialized
		final ModelElementCollection layer = allocateLayer( layerIndex );
		
		// add it to the combined list
		synchronized( allLayers ) {
			allLayers.add( element );
		}
		
		// add the element to the later	
		return layer.add( element );
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll( final Collection<? extends ModelElement> elements ) {
		// add them to the combined list
		synchronized( allLayers ) {
			allLayers.addAll( elements );
		}
		
		// add the elements to each layer
		synchronized( layer ) {
			for( ModelElement element : elements ) {
				layer.add( element );
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		synchronized( layer ) {
			layer.clear();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains( final Object object ) {
		synchronized( layer ) {
			return layer.contains( object );
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll( final Collection<?> objects ) {
		synchronized( layer ) {
			return layer.containsAll( objects );
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		synchronized( layer ) {
			return layer.isEmpty();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<ModelElement> iterator() {
		return layer.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove( final Object object ) {
		// is the object a model element?
		if( object instanceof ModelElement ) {
			// cast the element
			final ModelElement element = (ModelElement)object;
			
			// remove the object from the complete list
			synchronized( allLayers ) {
				allLayers.remove( element );
			}
			
			// remove the object from the current layer
			final ModelElementCollection layer = layers[element.getLayer()];
			synchronized( layer ) {
				return layer.remove( element );
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll( final Collection<?> objects ) {
		synchronized( layer ) {
			return layer.removeAll( objects );
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll( final Collection<?> objects ) {
		synchronized( layer ) {
			return layer.retainAll( objects );
		}
	}

	/** 
	 * Returns the size of the current layer
	 * @return the size of the current layer
	 */
	public int size() {
		synchronized( layer ) {
			return layer.size();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		synchronized( layer ) {
			return layer.toArray();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray( final T[] array ) {
		synchronized( layer ) {
			return layer.toArray( array );
		}
	}

	/**
	 * Allocates the layer by layer index
	 * @param layerIndex the given layer index
	 */
	private ModelElementCollection allocateLayer( final int layerIndex ) {
		synchronized( layers ) {
			// initial the layer, if not already initialized
			if( layers[layerIndex] == null ) {
				layers[layerIndex] = new ModelElementCollection();
			}
			
			// set the "active" layer
			return layers[layerIndex];
		}
	}
	
	/** 
	 * Combines all the layers
	 * @return all the layers
	 */
	@SuppressWarnings("unused")
	private LinkedList<ModelElement> combineAll() {
		synchronized( layers ) {
			final LinkedList<ModelElement> list = new LinkedList<ModelElement>();
			for( int n = 0; n < layers.length; n++ ) {
				if( layers[n] != null ) {
					list.addAll( layers[n] );
				}
			}
			return list;
		}
	}
	
	/** 
	 * Represents a collection of Model Elements
	 * @author lawrence.daniels@gmail.com
	 */
	private class ModelElementCollection extends HashSet<ModelElement> {
		
		/**
		 * Default Constructor
		 */
		public ModelElementCollection() {
			super();
		}
		
	}
	
}
