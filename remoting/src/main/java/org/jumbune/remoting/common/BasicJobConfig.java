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
	private String[] workers;

	/** Specifies the Jumbune tmp directory **/
	private String tmpDir;
	
	/** The host IP **/
	private String host;
	
	/** The port **/
	private String port;
	
	/** The ssh Auth Keys file. */
	private String sshAuthKeysFile;
	
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
	public String[] getWorkers() {
		return workers;
	}

	/**
	 * sets the slaves
	 * @param slaves
	 */
	public void setWorkers(String[] workers) {
		this.workers = workers;
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
	 * @return the sshAuthKeysFile
	 */
	public String getSshAuthKeysFile() {
		return sshAuthKeysFile;
	}

	/**
	 * @param sshAuthKeysFile the sshAuthKeysFile to set
	 */
	public void setSshAuthKeysFile(String sshAuthKeysFile) {
		this.sshAuthKeysFile = sshAuthKeysFile;
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
		", workers="+workers+ ", tmpDir="+tmpDir+", host="+host+", port="+port+", sshAuthKeysFile="+sshAuthKeysFile+
		", user="+user+" ]";
	}

}
