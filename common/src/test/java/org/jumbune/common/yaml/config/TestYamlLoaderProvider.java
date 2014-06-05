package org.jumbune.common.yaml.config;

import java.io.InputStream;

import org.jumbune.common.yaml.config.YamlLoader;


public class TestYamlLoaderProvider {

	public static YamlLoader testYamlLoader = null;

	static {
		YamlLoader.setjHome("/home/impadmin/Desktop/Jumbune_Home");
		InputStream is = TestYamlLoaderProvider.class.getResourceAsStream("/TestJumbune-UserProperties");
		testYamlLoader = new YamlLoader(is);
	}

	public static YamlLoader getYamlLoader() {
		return testYamlLoader;
	}
}
