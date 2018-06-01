package org.jumbune.monitoring.hprof;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jumbune.monitoring.utils.ProfilerConstants;


/**
 * Hprof binary format related constants shared between the BinaryHprofReader and BinaryHprofWriter.
 */
public final class BinaryHprof {
	/**
	 * Currently code only supports 4 byte id size.
	 */
	public static final int ID_SIZE = 4;

	/**
	 * Prefix of valid magic values from the start of a binary hprof file.
	 */
	private static final String MAGIC = "JAVA PROFILE ";

	/***
	 * private constructor for BinaryHprof
	 */
	private BinaryHprof(){
		
	}
	
	/**
	 * Returns the file's magic value as a String if found, otherwise null.
	 */
	public static String readMagic(DataInputStream in) {
		try {
			byte[] bytes = new byte[ProfilerConstants.FIVE_ONE_TWO];
			for (int i = 0; i < bytes.length; i++) {
				byte b = in.readByte();
				char c = (char) b;
				if (b == '\0' || c == ',') {
					String string = new String(bytes, 0, i, "UTF-8");
					if (string.startsWith(MAGIC)) {
						return string;
					}
					return null;
				}
				bytes[i] = b;
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * The Enum Tag.
	 */
	public static enum Tag {

		STRING_IN_UTF8(0x01, -ID_SIZE), LOAD_CLASS(0x02, ProfilerConstants.FOUR + ID_SIZE + ProfilerConstants.FOUR + ID_SIZE), UNLOAD_CLASS(0x03, ProfilerConstants.FOUR), STACK_FRAME(0x04, ID_SIZE + ID_SIZE
				+ ID_SIZE + ID_SIZE + ProfilerConstants.FOUR + ProfilerConstants.FOUR), STACK_TRACE(0x05, -(ProfilerConstants.FOUR + ProfilerConstants.FOUR + ProfilerConstants.FOUR)), ALLOC_SITES(0x06, -(2 + ProfilerConstants.FOUR + ProfilerConstants.FOUR + ProfilerConstants.FOUR + ProfilerConstants.EIGHT + ProfilerConstants.EIGHT  + ProfilerConstants.FOUR)), HEAP_SUMMARY(0x07,
						ProfilerConstants.FOUR + ProfilerConstants.FOUR + ProfilerConstants.EIGHT  + ProfilerConstants.EIGHT ), START_THREAD(0x0a, ProfilerConstants.FOUR + ID_SIZE + ProfilerConstants.FOUR + ID_SIZE + ID_SIZE + ID_SIZE), END_THREAD(0x0b, ProfilerConstants.FOUR), HEAP_DUMP(0x0c, -0), HEAP_DUMP_SEGMENT(
				0x1c, -0), HEAP_DUMP_END(0x2c, 0), CPU_SAMPLES(0x0d, -(ProfilerConstants.FOUR + ProfilerConstants.FOUR)), CONTROL_SETTINGS(0x0e, ProfilerConstants.FOUR + 2);

		private final byte tag;

		/**
		 * Gets the tag.
		 *
		 * @return the tag
		 */
		public byte getTag() {
			return tag;
		}

		/**
		 * Minimum size in bytes.
		 */
		private final int minimumSize;

		/**
		 * Maximum size in bytes. 0 mean no specific limit.
		 */
		private final int maximumSize;

		/**
		 * Instantiates a new tag.
		 *
		 * @param tag the tag
		 * @param size the size
		 */
		private Tag(int tag, int size) {
			this.tag = (byte) tag;
			if (size > 0) {
				// fixed size, max and min the same
				this.minimumSize = size;
				this.maximumSize = size;
			} else {
				// only minimum bound
				this.minimumSize = -size;
				this.maximumSize = 0;
			}
		}

		private static final Map<Byte, Tag> BYTE_TO_TAG = new HashMap<Byte, Tag>();

		static {
			for (Tag v : Tag.values()) {
				BYTE_TO_TAG.put(v.tag, v);
			}
		}

		/**
		 * Gets the tag.
		 *
		 * @param tag the tag
		 * @return the tag
		 */
		public static Tag get(byte tag) {
			return BYTE_TO_TAG.get(tag);
		}

		/**
		 * Returns null if the actual size meets expectations, or a String error message if not.
		 */
		public String checkSize(int actual) {
			if (actual < minimumSize) {
				return "expected a minimial record size of " + minimumSize + " for " + this + " but received " + actual;
			}
			if (maximumSize == 0) {
				return null;
			}
			if (actual > maximumSize) {
				return "expected a maximum record size of " + maximumSize + " for " + this + " but received " + actual;
			}
			return null;
		}
	}

	/**
	 * The Enum ControlSettings.
	 */
	public static enum ControlSettings {
		ALLOC_TRACES(0x01), CPU_SAMPLING(0x02);

		private final int bitmask;

		private ControlSettings(int bitmask) {
			this.bitmask = bitmask;
		}

		public int getBitmask() {
			return bitmask;
		}
	}

}
