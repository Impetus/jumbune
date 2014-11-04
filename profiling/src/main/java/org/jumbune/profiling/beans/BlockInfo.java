package org.jumbune.profiling.beans;

import java.util.List;

public class BlockInfo {
  private long lengthOfBlock = 0l;
  private List <String> nodeList;
  private short replicationFactor ;
  /**
   * @return the lengthOfBlock
   */
  public long getLengthOfBlock() {
    return lengthOfBlock;
  }
  /**
   * @param lengthOfBlock the lengthOfBlock to set
   */
  public void setLengthOfBlock(long lengthOfBlock) {
    this.lengthOfBlock = lengthOfBlock;
  }
  /**
   * @return the nodeList
   */
  public List <String> getNodeList() {
    return nodeList;
  }
  /**
   * @param nodeList the nodeList to set
   */
  public void setNodeList(List <String> nodeList) {
    this.nodeList = nodeList;
  }
  /**
   * @return the replicationFactor
   */
  public short getReplicationFactor() {
    return replicationFactor;
  }
  /**
   * @param replicationFactor the replicationFactor to set
   */
  public void setReplicationFactor(short replicationFactor) {
    this.replicationFactor = replicationFactor;
  }
}
