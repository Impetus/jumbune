package org.jumbune.remoting.common;

/**
 * 
 * The class BasicJobConfig represents a light weight version of JobConfig for agent.
 *
 */
public class BasicJobConfig implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7392974131762993444L;

	/** String specifying name of jumbune Job */
	private String jumbuneJobName;
	
	/** List of slave hosts **/
	private String[] slaves;

	/** Specifies the Jumbune tmp directory **/
	private String tmpDir;
	
	/** The host IP **/
	private String host;
	
	/** The port **/
	private String port;
	
	/** The private key RSA file **/
	private String rsaFile;
	
	/** The private key DSA file **/
	private String dsaFile;
	
	/** The user **/
	private String user;
	
	/**
	 * Constructor for creating new BasicYamlConfig object
	 * @param jumbuneJobName
	 * @param host
	 * @param port
	 */
	public BasicJobConfig(String jumbuneJobName, String host, String port){
		this.jumbuneJobName = jumbuneJobName;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * gets the jumbuneJobName
	 * @return
	 */
	public String getJumbuneJobName() {
		return jumbuneJobName;
	}

	/**
	 * sets the jumbuneJobName
	 * @param jumbuneJobName
	 */
	public void setJumbuneJobName(String jumbuneJobName) {
		this.jumbuneJobName = jumbuneJobName;
	}

	/**
	 * gets the slaves
	 * @return
	 */
	public String[] getSlaves() {
		return slaves;
	}

	/**
	 * sets the slaves
	 * @param slaves
	 */
	public void setSlaves(String[] slaves) {
		String[] slavesTmp = slaves;
		this.slaves = slavesTmp;
	}

	/**
	 * gets the tmpDir
	 * @return
	 */
	public String getTmpDir() {
		return tmpDir;
	}

	/**
	 * sets the tmpDir
	 * @param tmpDir
	 */
	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}
	
	/**
	 * gets the host
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * sets the host
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * gets the port
	 * @return
	 */
	public String getPort() {
		return port;
	}

	/**
	 * sets the port
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * gets the rsaFile
	 * @return
	 */
	public String getRsaFile() {
		return rsaFile;
	}

	/**
	 * sets the rsaFile
	 * @param rsaFile
	 */
	public void setRsaFile(String rsaFile) {
		this.rsaFile = rsaFile;
	}

	/**
	 * gets the dsaFile
	 * @return
	 */
	public String getDsaFile() {
		return dsaFile;
	}

	/**
	 * sets the dsaFile
	 * @param dsaFile
	 */
	public void setDsaFile(String dsaFile) {
		this.dsaFile = dsaFile;
	}

	/**
	 * gets the user
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * sets the user
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobConfigAgent [jumbuneJobName="+jumbuneJobName+
		", slaves="+slaves+ ", tmpDir="+tmpDir+", host="+host+", port="+port+", rsaFile="+rsaFile+", dsaFile="+dsaFile+
		", user="+user+" ]";
	}

}
