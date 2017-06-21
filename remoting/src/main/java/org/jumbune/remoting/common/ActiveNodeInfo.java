package org.jumbune.remoting.common;

import com.google.protobuf.ByteString;

/**
 * The Class ActiveNodeInfo is responsible for populating the active namenode host.
 */
public class ActiveNodeInfo extends com.google.protobuf.GeneratedMessage implements ActiveNodeInfoOrBuilder{
	
	
	   // Use ActiveNodeInfo.newBuilder() to construct.
    public ActiveNodeInfo(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    public ActiveNodeInfo(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static ActiveNodeInfo defaultInstance;
    
    public static ActiveNodeInfo getDefaultInstance() {
      return defaultInstance;
    }

    public ActiveNodeInfo getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    public ActiveNodeInfo(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              nameserviceId_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              namenodeId_ = input.readBytes();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              hostname_ = input.readBytes();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              port_ = input.readInt32();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              zkfcPort_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }	
    
    
    private void initFields() {
    	      nameserviceId_ = "";
    	       namenodeId_ = "";
    	       hostname_ = "";
    	      port_ = 0;
    	       zkfcPort_ = 0;
    	     }
    
    private int bitField0_;
    private java.lang.Object nameserviceId_;
    private java.lang.Object namenodeId_;
    private java.lang.Object hostname_;
    private int zkfcPort_;
    private int port_;
    
    protected void makeExtensionsImmutable() {
    	   }
	@Override
	public boolean hasNameserviceId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNameserviceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteString getNameserviceIdBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNamenodeId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNamenodeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteString getNamenodeIdBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasHostname() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHostname() {
		java.lang.Object ref = hostname_;
	      if (ref instanceof java.lang.String) {
	       return (java.lang.String) ref;
	      } else {
	    com.google.protobuf.ByteString bs = 
	            (com.google.protobuf.ByteString) ref;
	        java.lang.String s = bs.toStringUtf8();
	       if (bs.isValidUtf8()) {
	      hostname_ = s;
	         }
	     return s;
		       }
		    }
		    
	

	@Override
	public ByteString getHostnameBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasZkfcPort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getZkfcPort() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public com.google.protobuf.Message.Builder newBuilderForType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public com.google.protobuf.Message.Builder toBuilder() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected FieldAccessorTable internalGetFieldAccessorTable() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected com.google.protobuf.Message.Builder newBuilderForType(BuilderParent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
