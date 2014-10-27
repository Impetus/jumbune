package org.jumbune.common.yaml.config;

import java.io.InputStream;

import org.jumbune.common.yaml.config.Loader;


public class YamlLoaderProviderTest {

	public static Loader testYamlLoader = null;

	static {
		YamlLoader.setjHome("/home/impadmin/Desktop/Jumbune_Home");
		InputStream is = YamlLoaderProviderTest.class.getResourceAsStream("/TestJumbune-UserProperties");
		testYamlLoader = new YamlLoader(is);
	}

	public static Loader getYamlLoader() {
		return testYamlLoader;
	}
}
