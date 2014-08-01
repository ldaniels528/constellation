package constellation.thirdparty.formats.vsd;

import static java.lang.String.format;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import constellation.model.DefaultGeometricModel;
import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;

/**
 * Represents a Microsoft VISIO/VSD Model
 * @author lawrence.daniels@gmail.com
 */
public class VSDModel {
	private final Logger logger = Logger.getLogger( getClass() );
	private final List<VSDWorkSheet> workSheets;
	private final List<VSDElementType> types;
	private String companyName;
	private String authorName;
	private String vstPath;
	
	/**
	 * Default constructor
	 */
	public VSDModel() {
		this.workSheets = new LinkedList<VSDWorkSheet>();
		this.types		= new LinkedList<VSDElementType>();
	}
	
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName( final String companyName ) {
		this.companyName = companyName;
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
	public void setAuthorName( final String creatorName ) {
		this.authorName = creatorName;
	}

	/**
	 * @return the vstPath
	 */
	public String getVstPath() {
		return vstPath;
	}

	/**
	 * @param vstPath the .VST file path to set
	 */
	public void setVstPath( final String vstPath ) {
		this.vstPath = vstPath;
	}

	/**
	 * Adds the given VSD Work Sheet to the model
	 * @param workSheet the given {@link VSDWorkSheet VSD Work Sheet}
	 */
	public void add( final VSDWorkSheet workSheet ) {
		workSheets.add( workSheet );
	}
	
	/** 
	 * Returns the collection of work sheets
	 * @return the collection of {@link VSDWorkSheet work sheets}
	 */
	public Collection<VSDWorkSheet> getWorkSheets() {
		return workSheets;
	}
	
	/**
	 * Adds the given VSD Element Type to the model
	 * @param type the given {@link VSDElementType VSD Element Type}
	 */
	public void add( final VSDElementType type ) {
		types.add( type );
	}
	
	/** 
	 * Returns the collection of work sheets
	 * @return the collection of {@link VSDWorkSheet work sheets}
	 */
	public Collection<VSDElementType> getTypes() {
		return types;
	}

	/**
	 * Returns the Constellation Model
	 * @return the {@link GeometricModel Constellation Model}
	 * @throws ModelFormatException 
	 */
	public GeometricModel toModel() {
		// create the Constellation model
		final GeometricModel model = DefaultGeometricModel.newModel();
		model.setAuthorName( authorName );
		model.setAuthorOrganization( companyName );
		model.setDescription( vstPath );
		
		// add work sheets
		for( final VSDWorkSheet workSheet : workSheets ) {
			logger.info( format( "Worksheet: %s", workSheet.getLabel() ) );
			// TODO add conversion logic here			
		}
		
		return model;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return format( "%s - %s", workSheets, types );
	}
	
}
