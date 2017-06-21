package org.jumbune.remoting.common;

/**
 * The Interface ActiveNodeInfoOrBuilder.
 */
public interface ActiveNodeInfoOrBuilder
extends com.google.protobuf.MessageOrBuilder {

// required string nameserviceId = 1;
/**
* <code>required string nameserviceId = 1;</code>
*/
boolean hasNameserviceId();
/**
* <code>required string nameserviceId = 1;</code>
*/
java.lang.String getNameserviceId();
/**
* <code>required string nameserviceId = 1;</code>
*/
com.google.protobuf.ByteString
  getNameserviceIdBytes();

// required string namenodeId = 2;
/**
* <code>required string namenodeId = 2;</code>
*/
boolean hasNamenodeId();
/**
* <code>required string namenodeId = 2;</code>
*/
java.lang.String getNamenodeId();
/**
* <code>required string namenodeId = 2;</code>
*/
com.google.protobuf.ByteString
  getNamenodeIdBytes();

// required string hostname = 3;
/**
* <code>required string hostname = 3;</code>
*/
boolean hasHostname();
/**
* <code>required string hostname = 3;</code>
*/
java.lang.String getHostname();
/**
* <code>required string hostname = 3;</code>
*/
com.google.protobuf.ByteString
  getHostnameBytes();

// required int32 port = 4;
/**
* <code>required int32 port = 4;</code>
*/
boolean hasPort();
/**
* <code>required int32 port = 4;</code>
*/
int getPort();

// required int32 zkfcPort = 5;
/**
* <code>required int32 zkfcPort = 5;</code>
*/
boolean hasZkfcPort();
/**
* <code>required int32 zkfcPort = 5;</code>
*/
int getZkfcPort();
}