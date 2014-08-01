package constellation.thirdparty.formats.iges.elements;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/**
 * IGES Global Element
 * @author lawrence.daniels@gmail.com
 */
public class IGES_G extends IGESElement {
	private static final Logger logger = Logger.getLogger( IGES_G.class );
	private String parameterDelimiter;
	private String recordDelimiter;
	private String fileName;
	private String systemID;
	private String senderProductID;
	private String receiverProductID;
	private String preProcessorVersion;
	private String units;
	private double modelSpaceScale;
	private int numOfBitsForInteger;
	private int singlePrecisionMagnitude;
	private int singlePrecisionSignificance;
	private int soublePrecisionMagnitude;
	private int soublePrecisionSignificance;
	private int unitFlag;
	private int maxNumOfLineWeights;
	private double sizeOfMaxLineWidth;
	private double dateTimeStamp;
	private double minUserIntendedResolution;
	private double approxMaxCoordinate;
	private String authorName;
	private String authorOrganization;
	private String igesVersion;
	private int draftingStandardCode;
	
	/**
	 * Default constructor
	 */
	public IGES_G() {
		super();
	}
	
	/**
	 * Creates a new IGES "G" element
	 * @param params the given segment parameters
	 * @throws ModelFormatException
	 */
	private IGES_G( final String[] params ) 
	throws ModelFormatException {
		for( int n = 0; n < params.length; n++ ) {
			logger.debug( String.format( "param[%d] '%s'", n+1, params[n] ) );
		}
		
		int index					= 0;
		parameterDelimiter			= params[index++];
		recordDelimiter				= params[index++];
		senderProductID				= params[index++];
		fileName					= params[index++];
		systemID					= params[index++];
		preProcessorVersion			= params[index++];
		numOfBitsForInteger			= parseInt( params[index++] );
		singlePrecisionMagnitude	= parseInt( params[index++] );
		singlePrecisionSignificance	= parseInt( params[index++] );
		soublePrecisionMagnitude	= parseInt( params[index++] );
		soublePrecisionSignificance	= parseInt( params[index++] );
		receiverProductID			= params[index++];
		modelSpaceScale				= parseDouble( params[index++] );
		unitFlag					= parseInt( params[index++] );
		units						= params[index++];
		maxNumOfLineWeights			= parseInt( params[index++] );
		sizeOfMaxLineWidth			= parseDouble( params[index++] );
		dateTimeStamp				= parseDouble( params[index++] );
		minUserIntendedResolution	= parseDouble( params[index++] );
		approxMaxCoordinate			= parseDouble( params[index++] );
		authorName					= params[index++];
		authorOrganization			= params[index++];
		igesVersion					= params[index++];
		draftingStandardCode		= parseInt( params[index++] );
	}
	
	/** 
	 * Parses the given line collection into an IGES element
	 * @param lines the given {@link LineCollection line collection}
	 * @return an IGS "G" element
	 * @throws ModelFormatException
	 */
	public static IGES_G parse( final LineCollection lines )
	throws ModelFormatException {
		// convert the buffer to a string
		final String data = extractDataString( lines );

		// parse the parameters
		final LinkedList<String> tokens = parseTokens( data );
		
		// add default delimiters if not specified
		switch( tokens.size() ) {
			case 22:
				tokens.addFirst( ";" );
			case 23:
				tokens.addFirst( "," );
		}
		
		// parse the parameters
		final String[] params = tokens.toArray( new String[ tokens.size() ] );
		
		// return the IGES instance
		return new IGES_G( params );
	}
	
	/**
	 * Extracts the data string from the line collection
	 * @param lines the given {@link LineCollection line collection}
	 * @return the data string
	 */
	private static String extractDataString( final LineCollection lines ) {
		// create a buffer for the content
		final StringBuffer sb = new StringBuffer( 320 );
		
		// extract each lines
		boolean ok = false;
		do {
			// peek at the next line
			final String line = lines.peek();
			
			// get the record type
			final String recordType = line.substring( 72, 73 );
			
			// if it's 'G' add it to the list
			ok = recordType.equals( TYPE_G );
			if( ok ){
				sb.append( parseString( line, 0, 72 ) );
				lines.next();
			}
		}
		while( ok );
		
		// return the data string
		return sb.toString();
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName( final String fileName ) {
		this.fileName = fileName;
	}

	/**
	 * @return the systemID
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * @param systemID the systemID to set
	 */
	public void setSystemID( final String systemID ) {
		this.systemID = systemID;
	}

	/**
	 * @return the senderProductID
	 */
	public String getSenderProductID() {
		return senderProductID;
	}

	/**
	 * @param senderProductID the senderProductID to set
	 */
	public void setSenderProductID( final String sendProductID ) {
		this.senderProductID = sendProductID;
	}

	/**
	 * @return the receiverProductID
	 */
	public String getReceiverProductID() {
		return receiverProductID;
	}

	/**
	 * @param receiverProductID the receiverProductID to set
	 */
	public void setReceiverProductID( final String receiverProductID ) {
		this.receiverProductID = receiverProductID;
	}

	/**
	 * @return the preProcessorVersion
	 */
	public String getPreProcessorVersion() {
		return preProcessorVersion;
	}

	/**
	 * @param preProcessorVersion the preProcessorVersion to set
	 */
	public void setPreProcessorVersion(String preProcessorVersion) {
		this.preProcessorVersion = preProcessorVersion;
	}

	/**
	 * @return the modelSpaceScale
	 */
	public double getModelSpaceScale() {
		return modelSpaceScale;
	}

	/**
	 * @param modelSpaceScale the modelSpaceScale to set
	 */
	public void setModelSpaceScale( final double modelSpaceScale ) {
		this.modelSpaceScale = modelSpaceScale;
	}

	/**
	 * @return the igesVersion
	 */
	public String getIgesVersion() {
		return igesVersion;
	}

	/**
	 * @param igesVersion the igesVersion to set
	 */
	public void setIgesVersion(String igesVersion) {
		this.igesVersion = igesVersion;
	}
	
	/**
	 * @return the parameterDelimiter
	 */
	public String getParameterDelimiter() {
		return parameterDelimiter;
	}

	/**
	 * @param parameterDelimiter the parameterDelimiter to set
	 */
	public void setParameterDelimiter(String parameterDelimiter) {
		this.parameterDelimiter = parameterDelimiter;
	}

	/**
	 * @return the recordDelimiter
	 */
	public String getRecordDelimiter() {
		return recordDelimiter;
	}

	/**
	 * @param recordDelimiter the recordDelimiter to set
	 */
	public void setRecordDelimiter(String recordDelimiter) {
		this.recordDelimiter = recordDelimiter;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the numOfBitsForInteger
	 */
	public int getNumOfBitsForInteger() {
		return numOfBitsForInteger;
	}

	/**
	 * @param numOfBitsForInteger the numOfBitsForInteger to set
	 */
	public void setNumOfBitsForInteger(int numOfBitsForInteger) {
		this.numOfBitsForInteger = numOfBitsForInteger;
	}

	/**
	 * @return the singlePrecisionMagnitude
	 */
	public int getSinglePrecisionMagnitude() {
		return singlePrecisionMagnitude;
	}

	/**
	 * @param singlePrecisionMagnitude the singlePrecisionMagnitude to set
	 */
	public void setSinglePrecisionMagnitude(int singlePrecisionMagnitude) {
		this.singlePrecisionMagnitude = singlePrecisionMagnitude;
	}

	/**
	 * @return the singlePrecisionSignificance
	 */
	public int getSinglePrecisionSignificance() {
		return singlePrecisionSignificance;
	}

	/**
	 * @param singlePrecisionSignificance the singlePrecisionSignificance to set
	 */
	public void setSinglePrecisionSignificance(int singlePrecisionSignificance) {
		this.singlePrecisionSignificance = singlePrecisionSignificance;
	}

	/**
	 * @return the soublePrecisionMagnitude
	 */
	public int getSoublePrecisionMagnitude() {
		return soublePrecisionMagnitude;
	}

	/**
	 * @param soublePrecisionMagnitude the soublePrecisionMagnitude to set
	 */
	public void setSoublePrecisionMagnitude(int soublePrecisionMagnitude) {
		this.soublePrecisionMagnitude = soublePrecisionMagnitude;
	}

	/**
	 * @return the soublePrecisionSignificance
	 */
	public int getSoublePrecisionSignificance() {
		return soublePrecisionSignificance;
	}

	/**
	 * @param soublePrecisionSignificance the soublePrecisionSignificance to set
	 */
	public void setSoublePrecisionSignificance(int soublePrecisionSignificance) {
		this.soublePrecisionSignificance = soublePrecisionSignificance;
	}

	/**
	 * @return the unitFlag
	 */
	public int getUnitFlag() {
		return unitFlag;
	}

	/**
	 * @param unitFlag the unitFlag to set
	 */
	public void setUnitFlag(int unitFlag) {
		this.unitFlag = unitFlag;
	}

	/**
	 * @return the maxNumOfLineWeights
	 */
	public int getMaxNumOfLineWeights() {
		return maxNumOfLineWeights;
	}

	/**
	 * @param maxNumOfLineWeights the maxNumOfLineWeights to set
	 */
	public void setMaxNumOfLineWeights(int maxNumOfLineWeights) {
		this.maxNumOfLineWeights = maxNumOfLineWeights;
	}

	/**
	 * @return the sizeOfMaxLineWidth
	 */
	public double getSizeOfMaxLineWidth() {
		return sizeOfMaxLineWidth;
	}

	/**
	 * @param sizeOfMaxLineWidth the sizeOfMaxLineWidth to set
	 */
	public void setSizeOfMaxLineWidth(double sizeOfMaxLineWidth) {
		this.sizeOfMaxLineWidth = sizeOfMaxLineWidth;
	}

	/**
	 * @return the dateTimeStamp
	 */
	public double getDateTimeStamp() {
		return dateTimeStamp;
	}

	/**
	 * @param dateTimeStamp the dateTimeStamp to set
	 */
	public void setDateTimeStamp(double dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	/**
	 * @return the minUserIntendedResolution
	 */
	public double getMinUserIntendedResolution() {
		return minUserIntendedResolution;
	}

	/**
	 * @param minUserIntendedResolution the minUserIntendedResolution to set
	 */
	public void setMinUserIntendedResolution(double minUserIntendedResolution) {
		this.minUserIntendedResolution = minUserIntendedResolution;
	}

	/**
	 * @return the approxMaxCoordinate
	 */
	public double getApproxMaxCoordinate() {
		return approxMaxCoordinate;
	}

	/**
	 * @param approxMaxCoordinate the approxMaxCoordinate to set
	 */
	public void setApproxMaxCoordinate(double approxMaxCoordinate) {
		this.approxMaxCoordinate = approxMaxCoordinate;
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @param authorName the authorName to set
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	/**
	 * @return the authorOrganization
	 */
	public String getAuthorOrganization() {
		return authorOrganization;
	}

	/**
	 * @param authorOrganization the authorOrganization to set
	 */
	public void setAuthorOrganization( String authorOrganization ) {
		this.authorOrganization = authorOrganization;
	}

	/**
	 * @return the draftingStandardCode
	 */
	public int getDraftingStandardCode() {
		return draftingStandardCode;
	}

	/**
	 * @param draftingStandardCode the draftingStandardCode to set
	 */
	public void setDraftingStandardCode( int draftingStandardCode ) {
		this.draftingStandardCode = draftingStandardCode;
	}

	/** 
	 * {@inheritDoc}
	 */
	public String getType() {
		return TYPE_G;
	}

}
