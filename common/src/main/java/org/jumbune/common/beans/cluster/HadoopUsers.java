package org.jumbune.common.beans.cluster;

public class HadoopUsers {

	/**
	 * This user will be used for HDFS Admin operations and accessing tmp Profiling files
	 */
	private String hdfsUser = "hdfs";
	/**
	 * No use
	 */
	private String yarnUser = "yarn";
	/**
	 * No use
	 */
	private String mapredUser = "mapred";
	/**
	 * Local NameNode file system user, typically will be performing ls, rmr, mkdir, cp, mv, etc. operations
	 * Also, this user has password less ssh authorized keys from master to worker nodes (for worker node user)
	 */
	private String fsUser;
	
	/**
	 * Having this true means Hdfs, Yarn, Mapred and FS at least are the same
	 */
	private boolean hasSingleUser;
	
	private String fsUserPassword;
	
	private String fsPrivateKeyPath;

	public String getHdfsUser() {
		return hdfsUser;
	}

	public void setHdfsUser(String hdfsUser) {
		this.hdfsUser = hdfsUser;
	}

	public String getYarnUser() {
		return yarnUser;
	}

	public void setYarnUser(String yarnUser) {
		this.yarnUser = yarnUser;
	}

	public String getMapredUser() {
		return mapredUser;
	}

	public void setMapredUser(String mapredUser) {
		this.mapredUser = mapredUser;
	}

	public String getFsUser() {
		return fsUser;
	}

	public void setFsUser(String fsUser) {
		this.fsUser = fsUser;
	}

	public boolean isHasSingleUser() {
		return hasSingleUser;
	}

	public void setHasSingleUser(boolean hasSingleUser) {
		this.hasSingleUser = hasSingleUser;
	}

	public String getFsUserPassword() {
		return fsUserPassword;
	}

	public void setFsUserPassword(String fsUserPassword) {
		this.fsUserPassword = fsUserPassword;
	}

	public String getFsPrivateKeyPath() {
		return fsPrivateKeyPath;
	}

	public void setFsPrivateKeyPath(String fsPrivateKeyPath) {
		this.fsPrivateKeyPath = fsPrivateKeyPath;
	}

	@Override
	public String toString() {
		return "HadoopUsers [hdfsUser=" + hdfsUser + ", yarnUser=" + yarnUser
				+ ", mapredUser=" + mapredUser + ", fsUser=" + fsUser
				+ ", hasSingleUser=" + hasSingleUser + ", fsUserPassword=Redacted, fsPrivateKeyPath=" + fsPrivateKeyPath
				+ "]";
	}
	
}
