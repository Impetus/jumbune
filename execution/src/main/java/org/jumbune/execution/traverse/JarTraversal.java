/**
 *
 */
package org.jumbune.execution.traverse;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;



/**
 * This class is used to traverse the given jar.
 */
public class JarTraversal {
	private static final String CLASS_FILE_EXTN = ".class";

	/**
	 * this is used to obtain jar input stream to traverse the jar file
	 * 
	 * @param String
	 *            jarPath
	 * @return JarInputStream
	 
	 * @throws IOException
	 */
	private JarInputStream getJarInputStream(String jarPath) throws IOException {

		FileInputStream fis = new FileInputStream(jarPath);
		JarInputStream inputputstream = new JarInputStream(
				new BufferedInputStream(fis));
		return inputputstream;
	}

	/**
	 * This method will read each file available in jar and will then filter out
	 * any class that has main method. To filter out it assumes that any class
	 * that is creating Hadoop Job should has main method. It will add all those
	 * classes name in a list and will return the same.
	 * 
	 * @param String
	 *            jarPath
	 * @return List<String>
	 * @throws IOException
	 */
	public List<String> getAlljobs(String jarPath) throws IOException {
		ZipEntry entry;
		JarInputStream inputStream = null;
		byte[] outputBytes = null;
		try {
			inputStream = getJarInputStream(jarPath);
			if (inputStream == null) {
				return null;
			}

			ClassWriter wr = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			HTFClassVisitor cvmr = new HTFClassVisitor(Opcodes.ASM4, wr);

			while ((entry = inputStream.getNextEntry()) != null) {
				if (entry.getName().endsWith(CLASS_FILE_EXTN)) {
					// This classreader should be created for every nextEntry of
					// input stream. Whenever inputStream.nextEntry
					// is called it points to a new class/folder. So all the
					// same inputStream is used but it points to a new
					// class
					outputBytes = InstrumentUtil
							.getEntryBytesFromZip(inputStream);
					ClassReader cr = new ClassReader(outputBytes);
					cr.accept(cvmr, 0);
				}
			}
			return cvmr.getJobClassList();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}
}
