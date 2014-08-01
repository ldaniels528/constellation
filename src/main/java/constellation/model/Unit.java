package constellation.model;

/**
 * Represents the a model unit
 * @author lawrence.daniels@gmail.com
 */
public class Unit {
	private final int unitID;
	private final String name;
	private final String shortName;
	private final double gridSpacing;
	private final double modelScale;
	
	/** 
	 * Creates a new unit
	 * @param unitID the given unit ID
	 * @param name the given unit name (e.g. 'MILLIMETERS')
	 * @param shortName the given short name (e.g. 'mm')
	 * @param modelScale the given model scale
	 * @param gridSpacing the given grid spacing
	 */
	protected Unit( final int unitID,
				  	final String name,
				  	final String shortName, 
				  	final double modelScale, 
				  	final double gridSpacing ) {
		this.unitID 		= unitID;
		this.name			= name;
		this.shortName		= shortName;
		this.gridSpacing	= gridSpacing;
		this.modelScale		= modelScale;
	}
	
	/**
	 * Returns the name of the unit
	 * @return the name of the unit
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the short name of the unit
	 * @return the short name of the unit
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return the unitID
	 */
	public int getUnitID() {
		return unitID;
	}

	/**
	 * @return the grid spacing
	 */
	public double getGridSpacing() {
		return gridSpacing;
	}

	/**
	 * @return the model scale
	 */
	public double getModelScale() {
		return modelScale;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return unitID;
	}
	
	/**
	 * Returns the ordinal value
	 * @return the ordinal value
	 */
	public int ordinal() {
		return unitID;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
	
}
