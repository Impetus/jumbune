package org.jumbune.execution;

import java.io.InputStream;

import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import java.io.File;


public class YamlLoaderProviderTest {

	public static Loader testYamlLoader = null;

	static {
		YamlLoader.setjHome("/home/impadmin/Desktop/Jumbune_Home");
		String path = System.getProperty("user.dir")+"/JUMBUNE_HOME";
		File f = new File(path);
		f.mkdir();
		YamlLoader.setjHome(f.getAbsolutePath());
		InputStream is = YamlLoaderProviderTest.class.getResourceAsStream("/TestJumbune-UserProperties");
		testYamlLoader = new YamlLoader(is);
	}

	public static Loader getYamlLoader() {
		return testYamlLoader;
	}
}
