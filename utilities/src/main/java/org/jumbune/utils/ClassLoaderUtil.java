package org.jumbune.utils;

import static org.jumbune.utils.UtilitiesConstants.SEPARATOR_COMMA;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides util methods to deal with class loading
 * 
 */
public final class ClassLoaderUtil {
	/** The LOGGER. */
	private static final Logger LOG = LogManager
			.getLogger(ClassLoaderUtil.class);

	/**
	 * private constructor for ClassLoaderUtil
	 */
	private ClassLoaderUtil(){
		
	}

	/**
	 * This method call when injected into a class will load jars and resources
	 * at runtime.
	 * 
	 * @param fileList
	 *            comma separated files jars to be added to the classpath
	 * @throws NoSuchMethodException
	 *             If the mentioned method is not found in the class
	 * @throws MalformedURLException
	 *             If the jars/resources path cannot be converted to URL
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void loadClasses(String fileList)
			throws NoSuchMethodException, MalformedURLException,
			InvocationTargetException, IllegalAccessException {
		final String loadMethodName = "addURL";

		if (fileList != null && fileList.length() > 0) {
			URLClassLoader urlClassLoader = (URLClassLoader) Thread
					.currentThread().getContextClassLoader().getParent();
			Method method = URLClassLoader.class.getDeclaredMethod(
					loadMethodName, URL.class);
			method.setAccessible(true);

			String[] filesArray = fileList.split(SEPARATOR_COMMA);
			// Convert the string to URLs and populate URL[]
			for (String res : filesArray) {
				LOG.debug("Adding to the classpath: " + res);
				method.invoke(urlClassLoader, new Object[] { new File(res)
						.toURI().toURL() });
			}
			method.setAccessible(false);
		}
	}
}