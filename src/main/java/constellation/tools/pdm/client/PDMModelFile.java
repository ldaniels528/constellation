package constellation.tools.pdm.client;

import java.util.Date;

/**
 * Represents a PDM Model File
 * @author lawrence.daniels@gmail.com
 */
public class PDMModelFile {
	private int pdmFileId;
	private String name;
	private PDMModelFileStatus status;
	private String lastModifiedBy;
	private Date lastModifiedTime;
	private String createdBy;
	private Date createdTime;
	
	/**
	 * Default constructor
	 */
	public PDMModelFile() {
		super();
	}

	/**
	 * @return the pdmFileId
	 */
	public int getPdmFileId() {
		return pdmFileId;
	}

	/**
	 * @param pdmFileId the pdmFileId to set
	 */
	public void setPdmFileId(int pdmFileId) {
		this.pdmFileId = pdmFileId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName( final String name) {
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public PDMModelFileStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus( final PDMModelFileStatus status ) {
		this.status = status;
	}

	/**
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
