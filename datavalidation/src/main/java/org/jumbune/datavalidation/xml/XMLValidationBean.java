/**
 * 
 */
package org.jumbune.datavalidation.xml;

/**
 * @author vivek.shivhare
 *
 */

/**
 * This class contains all validation checks to be applied on different fields by 
 * the user.
 * 
 * 
 */
public class XMLValidationBean {
	


	/**
	 * lineNumber - the line number in the xml.
	 */
	private long lineNumber;
	/**
	 * fileName - name of the file .
	 */
	private String fileName;
	/**
	 * errorType - type of the error in xml .
	 */
	private String errorType;
	/**
	 * errorDetail - Deatiled validation error.
	 */
	private String errorDetail;
	
	/**
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return lineNumber;
	}
	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
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
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}
	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	/**
	 * @return the errorDetail
	 */
	public String getErrorDetail() {
		return errorDetail;
	}
	/**
	 * @param errorDetail the errorDetail to set
	 */
	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "XMLValidationBean [lineNumber=" + lineNumber + ", fileName="
				+ fileName + ", errorType=" + errorType + ", errorDetail="
				+ errorDetail + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = (int) (prime * result + lineNumber);
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof XMLValidationBean))
			return false;
		XMLValidationBean other = (XMLValidationBean) obj;
		
		if(this.hashCode() == other.hashCode()){
			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;
			if (lineNumber != other.lineNumber)
				return false;
			return true;
		}else{
			return false;
		}
	}
	

	


}
