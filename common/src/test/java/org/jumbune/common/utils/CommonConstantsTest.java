package org.jumbune.common.utils;

public interface CommonConstantsTest {
	String lineSeparator = System.getProperty("line.separator");

	int MESSAGELOADER_KEY_INT = 123;

	String MESSAGELOADER_KEYS_TRING = "name";

	String CONTACTYAMLFILE = new StringBuilder().append("name: Nathan Sweet").append(lineSeparator).append("age: 28").append(lineSeparator)
			.append("address: pak").append(lineSeparator).append("123: 3322").append(lineSeparator).append("1019: 3322").append(lineSeparator)
			.append("1004: 332").append(lineSeparator).append("1016: 332").append(lineSeparator).append("1017: 332").append(lineSeparator)
			.append("1018: 332").append(lineSeparator).append("1020: 332").append(lineSeparator).append("1005: 332").append(lineSeparator)
			.append("1009: 332").toString();
}
