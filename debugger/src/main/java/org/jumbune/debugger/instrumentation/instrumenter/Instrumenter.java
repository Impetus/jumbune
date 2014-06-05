package org.jumbune.debugger.instrumentation.instrumenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;



/**
 * This class is used to instrument a jar file. The output is generated in a new
 * jar file.
 * <p>
 * It instruments only class files in the jar. All other files are written back
 * as it is.
 * </p>
 * 

 */
public abstract class Instrumenter {
	private static final Logger LOGGER = LogManager
			.getLogger(Instrumenter.class);
	private YamlLoader loader;

	/**
	 * <p>
	 * Create a new instance of Instrumenter.
	 * </p>
	 */
	public Instrumenter(YamlLoader loader) {
		this.loader=loader;
	}

	/**
	 * <p>
	 * Gets the yaml loader
	 * </p>
	 * 
	 * @return YamlLoader
	 */

	/**
	 * This method instruments a jar file. The source and destination files are
	 * picked from yaml configuration.
	 * 
	 * @see org.jumbune.debugger.instrumentation.instrumenter.Instrumenter#instrumentJar()
	 */
	public abstract void instrumentJar() throws IOException;

	/**
	 * <p>
	 * Adds other files to the instrumented files. Dependent jar files and
	 * resources for thick jar and couple of class files in case of thin jar.
	 * </p>
	 * 
	 * @param zos
	 *            Stream for output file
	 
	 *             If any error occurred
	 */
	public abstract void addAdditionalFiles(ZipOutputStream zos)
			throws IOException;

	/**
	 * <p>
	 * Process an entry through various adapters.
	 * </p>
	 * 
	 * @param bytes
	 *            bytes for an entry from the input file
	 * @return modified bytes for the entry
	 
	 *             If any error occurred
	 */
	public abstract byte[] instrumentEntry(byte[] bytes) throws IOException;

	/**
	 * <p>
	 * This method instruments a given file to a destination file.
	 * </p>
	 * 
	 * @param fin
	 *            File to be instrumented
	 * @param fout
	 *            Destination file
	 */
	public void instrumentJar(File fin, File fout) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.ARCHIVE_BEING_INSTRUMENTED),
					fin.getCanonicalPath()));
			fis = new FileInputStream(fin);
			fos = new FileOutputStream(fout);
			LOGGER.info("Instrumenting jar at path [" + fin
					+ "], Instrumented jar will be at path [" + fout + "]");
			long startTime = System.nanoTime();
			instrumentJar(fis, fos);
			LOGGER.debug(MessageFormat.format(
					InstrumentationMessageLoader
							.getMessage(MessageConstants.ARCHIVE_BEING_INSTRUMENTED_TO),
					fin.getCanonicalPath(), fout.getCanonicalPath()));
			LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.INSTRUMENTATION_TIME_TAKEN),
					new Double(System.nanoTime() - startTime) / InstrumentConstants.LAKH));
		} catch (IOException e) {
			LOGGER.error(InstrumentationMessageLoader
					.getMessage(MessageConstants.ARCHIVE_COULD_NOT_BE_INSTRUMENTED));
			throw e;
		} finally {
			// closing the streams
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException ioe) {
					LOGGER.error(InstrumentationMessageLoader
							.getMessage(MessageConstants.INPUT_STREM_NOT_CLOSED));
					throw ioe;
				}
			}
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException ioe) {
					LOGGER.error(InstrumentationMessageLoader
							.getMessage(MessageConstants.OUTPUT_STREAM_NOT_CLOSED));
					throw ioe;
				}
			}
		}
	}

	/**
	 * <p>
	 * This method instruments a given file and writes to a destination file
	 * </p>
	 * 
	 * @param fis
	 *            Stream for input file
	 * @param fos
	 *            Stream for output file
	 
	 *             If any erorr occurred
	 */
	public void instrumentJar(FileInputStream fis, FileOutputStream fos) throws IOException {
		ZipInputStream zis = new ZipInputStream(fis);
		ZipOutputStream zos = new ZipOutputStream(fos);
		try {
			instrumentJar(zis, zos);
		}finally {
			if (zis != null) {
				zis.close();
			}
			if (zos != null) {
				zos.close();
			}
		}
	}

	/**
	 * <p>
	 * This method instruments each class file in a given jar and write the
	 * instrumented one to the destination jar file
	 * </p>
	 * 
	 * @param zis
	 *            Stream for input jar
	 * @param zos
	 *            Stream for output jar
	 
	 *             If any error occurred
	 */
	public void instrumentJar(ZipInputStream zis, ZipOutputStream zos)
			throws IOException {
		ZipEntry entry;
		byte[] outputBytes = null;

		// execute for each entry in the jar
		while ((entry = zis.getNextEntry()) != null) {
			outputBytes = InstrumentUtil.getEntryBytesFromZip(zis);

			// instrument if and only if it is a class file
			if (entry.getName().endsWith(
					InstrumentConstants.CLASS_FILE_EXTENSION)) {
				outputBytes = instrumentEntry(outputBytes);
			}

			// create a new entry and write the bytes obtained above
			ZipEntry outputEntry = new ZipEntry(entry.getName());
			outputEntry.setComment(entry.getComment());
			outputEntry.setExtra(entry.getExtra());
			outputEntry.setTime(entry.getTime());
			zos.putNextEntry(outputEntry);
			zos.write(outputBytes);
			zos.closeEntry();
			zis.closeEntry();
		}

		// adding other files if required
		addAdditionalFiles(zos);
	}

	/**
	 * @param loader the loader to set
	 */
	public void setLoader(YamlLoader loader) {
		this.loader = loader;
	}

	/**
	 * @return the loader
	 */
	protected YamlLoader getLoader() {
		return loader;
	}
}
