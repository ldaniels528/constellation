package constellation.thirdparty.formats.iges.elements;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/**
 * IGES Directory Entry Element
 * <pre>
 * Entity Type Number       = 116 - Point
 * Parameter Data (Count)   = 1 (1)
 * Structure                = 0
 * Line Font Pattern        = 1 - Solid
 * Level                    = 0
 * View                     = 0
 * Transformation Matrix    = 0
 * Label Display            = 0
 * Blank Status             = <default> - Visible
 * Subord. Entity Switch    = <default> - Independent
 * Entity Use Flag          = <default> - GeometricElement
 * Hierarchy                = 1 - Global defer
 * Line Weight Number       = 1
 * Color Number             = 5 - Yellow
 * Form Number              = 0
 * Entity Label (Subscript) = <default> (0)
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES_D extends IGESElement {
	private int entityTypeNumber;
	private int parameterIndex;
	private int structure;
	private int lineFontPattern;
	private int level;
	private int view;
	private int transformationMatrix;
	private int labelDisplay;
	private int blankStatus;
	private int subordEntitySwitch;
	private int entityUseFlag;
	private int hierarchy;
	private int lineWeightNumber;
	private int colorNumber;
	private int formNumber;
	private int entityLabel;
	
	/**
	 * Default constructor
	 */
	public IGES_D() {
		super();
	}

	/**
	 * Creates a new IGES "D" element
	 * @param lines the given {@link LineCollection line collection}
	 * @throws ModelFormatException
	 */
	protected IGES_D( final LineCollection lines ) 
	throws ModelFormatException {
		// get the next 2 lines
		final String line1 = lines.next();
		final String line2 = lines.next();
		
		// parse attributes from line #1
		this.entityTypeNumber		= parseInt( line1, 0, 9 );
		this.parameterIndex			= parseInt( line1, 9, 17 );
		this.structure				= parseInt( line1, 17, 25 );
		this.lineFontPattern		= parseInt( line1, 25, 33 );
		this.level					= parseInt( line1, 33, 41 );
		this.view					= parseInt( line1, 41, 49 );
		this.transformationMatrix	= parseInt( line1, 49, 57 );
		this.labelDisplay			= parseInt( line1, 57, 65 );
		this.blankStatus			= parseInt( line1, 65, 72 );
		
		// parse attributes from line #2
		this.lineWeightNumber		= parseInt( line2, 9, 17 );
		this.colorNumber			= parseInt( line2, 17, 25 );
		this.formNumber				= parseInt( line1, 25, 33 );
	}

	/**
	 * @return the entityTypeNumber
	 */
	public int getEntityTypeNumber() {
		return entityTypeNumber;
	}

	/**
	 * @param entityTypeNumber the entityTypeNumber to set
	 */
	public void setEntityTypeNumber(int entityTypeNumber) {
		this.entityTypeNumber = entityTypeNumber;
	}

	/**
	 * @return the parameterDataCount
	 */
	public int getParameterIndex() {
		return parameterIndex;
	}

	/**
	 * @param parameterDataCount the parameterDataCount to set
	 */
	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	/**
	 * @return the structure
	 */
	public int getStructure() {
		return structure;
	}

	/**
	 * @param structure the structure to set
	 */
	public void setStructure(int structure) {
		this.structure = structure;
	}

	/**
	 * @return the lineFontPattern
	 */
	public int getLineFontPattern() {
		return lineFontPattern;
	}

	/**
	 * @param lineFontPattern the lineFontPattern to set
	 */
	public void setLineFontPattern(int lineFontPattern) {
		this.lineFontPattern = lineFontPattern;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the view
	 */
	public int getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(int view) {
		this.view = view;
	}

	/**
	 * @return the transformationMatrix
	 */
	public int getTransformationMatrix() {
		return transformationMatrix;
	}

	/**
	 * @param transformationMatrix the transformationMatrix to set
	 */
	public void setTransformationMatrix(int transformationMatrix) {
		this.transformationMatrix = transformationMatrix;
	}

	/**
	 * @return the labelDisplay
	 */
	public int getLabelDisplay() {
		return labelDisplay;
	}

	/**
	 * @param labelDisplay the labelDisplay to set
	 */
	public void setLabelDisplay(int labelDisplay) {
		this.labelDisplay = labelDisplay;
	}

	/**
	 * @return the blankStatus
	 */
	public int getBlankStatus() {
		return blankStatus;
	}

	/**
	 * @param blankStatus the blankStatus to set
	 */
	public void setBlankStatus(int blankStatus) {
		this.blankStatus = blankStatus;
	}

	/**
	 * @return the subordEntitySwitch
	 */
	public int getSubordEntitySwitch() {
		return subordEntitySwitch;
	}

	/**
	 * @param subordEntitySwitch the subordEntitySwitch to set
	 */
	public void setSubordEntitySwitch(int subordEntitySwitch) {
		this.subordEntitySwitch = subordEntitySwitch;
	}

	/**
	 * @return the entityUseFlag
	 */
	public int getEntityUseFlag() {
		return entityUseFlag;
	}

	/**
	 * @param entityUseFlag the entityUseFlag to set
	 */
	public void setEntityUseFlag(int entityUseFlag) {
		this.entityUseFlag = entityUseFlag;
	}

	/**
	 * @return the hierarchy
	 */
	public int getHierarchy() {
		return hierarchy;
	}

	/**
	 * @param hierarchy the hierarchy to set
	 */
	public void setHierarchy(int hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
	 * @return the lineWeightNumber
	 */
	public int getLineWeightNumber() {
		return lineWeightNumber;
	}

	/**
	 * @param lineWeightNumber the lineWeightNumber to set
	 */
	public void setLineWeightNumber(int lineWeightNumber) {
		this.lineWeightNumber = lineWeightNumber;
	}

	/**
	 * @return the colorNumber
	 */
	public int getColorNumber() {
		return colorNumber;
	}

	/**
	 * @param colorNumber the colorNumber to set
	 */
	public void setColorNumber(int colorNumber) {
		this.colorNumber = colorNumber;
	}

	/**
	 * @return the formNumber
	 */
	public int getFormNumber() {
		return formNumber;
	}

	/**
	 * @param formNumber the formNumber to set
	 */
	public void setFormNumber(int formNumber) {
		this.formNumber = formNumber;
	}

	/**
	 * @return the entityLabel
	 */
	public int getEntityLabel() {
		return entityLabel;
	}

	/**
	 * @param entityLabel the entityLabel to set
	 */
	public void setEntityLabel(int entityLabel) {
		this.entityLabel = entityLabel;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getType() {
		return TYPE_D;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format( "entityTypeNumber '%d', paramCount '%d' structure '%d' pattern '%d' " +
								"level '%d' transformationMatrix '%d' labelDisplay '%d' blankStatus '%d' " +
								"subordEntitySwitch '%d' %s", 
								entityTypeNumber, parameterIndex, structure, lineFontPattern,
								level, transformationMatrix, labelDisplay, blankStatus, 
								subordEntitySwitch, super.toString() );
	}

}