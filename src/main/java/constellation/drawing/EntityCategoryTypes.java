package constellation.drawing;

/**
 * Represents the enumeration of Constellation Entity Category Types
 * @author lawrence.daniels@gmail.com
 */
public enum EntityCategoryTypes {
	
	/**
	 * Represents a  vertex type (e.g. point)
	 */
	VERTEX,
	
	/**
	 * Represents a linear type (e.g. line)
	 */
	LINEAR,
	
	/**
	 * Representation of all curves
	 */
	CURVE,
	
	/**
	 * Represents a special non-geometric type
	 */
	IMAGE,
	
	/**
	 * Represents compound, composite, or complex geometry (e.g. digitized line)
	 */
	COMPOUND,
	
	/**
	 * Represents a textual type
	 */
	TEXT,
	
	/**
	 * Represents a dimension type
	 */
	DIMENSION

}
