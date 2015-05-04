package org.jumbune.debugger.instrumentation.instrumenter;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.debugger.instrumentation.adapter.InstrumentValidator;
import org.jumbune.debugger.instrumentation.adapter.ProfileAdapter;
import org.jumbune.debugger.instrumentation.adapter.SubmitCaseAdapter;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.objectweb.asm.ClassWriter;



/**
 * This class is used to instrument a jar file for hadoop profiling. The output
 * is generated in a new jar file.
 * <p>
 * It instruments only class files in the jar. All other files are written back
 * as it is.
 * </p>

 */
public class PureJarInstrumenter extends Instrumenter {
	private static final Logger LOGGER = LogManager
			.getLogger(PureJarInstrumenter.class);

	/**
	 * <p>
	 * Create a new instance of ProfilingInstrumenter.
	 * </p>
	 */
	public PureJarInstrumenter(Config config) {
		super(config);
	}

	/**
	 * This method instruments a jar file. The source and destination files are
	 * picked from yaml configuration.
	 * 
	 * @see org.jumbune.debugger.instrumentation.instrumenter.Instrumenter#instrumentJar()
	 */
	public void instrumentJar() throws IOException {
		JobConfig jobConfig = (JobConfig)getConfig();
		File fin = new File(jobConfig.getInputFile());
		File fout = new File(jobConfig.getProfiledOutputFile());
		instrumentJar(fin, fout);
	}

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
	public byte[] instrumentEntry(final byte[] bytes) throws IOException {
		byte[] instrumentedBytes=bytes;
		try {
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

			// Step 1: check if already instrumented or is an interface
			InstrumentValidator instrumentValidator = new InstrumentValidator(getConfig(), classWriter);
			instrumentedBytes = InstrumentUtil.instrumentBytes(instrumentedBytes, instrumentValidator, classWriter);

			// if the class has not been already instrumented
			if (!instrumentValidator.getAlreadyInstrumented() && !instrumentValidator.getInterface()) {
				JobConfig jobConfig = (JobConfig)getConfig();
				if (jobConfig.isHadoopJobProfileEnabled()) {
					classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
					instrumentedBytes = InstrumentUtil.instrumentBytes(instrumentedBytes,
							new ProfileAdapter(getConfig(), classWriter), classWriter);
				}
				classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				instrumentedBytes = InstrumentUtil.instrumentBytes(instrumentedBytes,
						new SubmitCaseAdapter(getConfig(), classWriter), classWriter);
			}

			return instrumentedBytes;
		} catch (IOException ioEx) {
			LOGGER.error("Could not instrument Pure Jar  ", ioEx);
			throw ioEx;
		}
	}

	/**
	 * <p>
	 * Adds other files to the instrumented files. No file in case of profiling.
	 * </p>
	 * 
	 * @param zos
	 *            Stream for output file
	
	 *             If any error occurred
	 */
	@Override
	public void addAdditionalFiles(ZipOutputStream zos) throws IOException {
		// Do Nothing
	}

	
}
