package org.jumbune.debugger.instrumentation.instrumenter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.text.MessageFormat;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.adapter.BlockLogAdapter;
import org.jumbune.debugger.instrumentation.adapter.CaseAdapter;
import org.jumbune.debugger.instrumentation.adapter.ChainedTaskClassAdapter;
import org.jumbune.debugger.instrumentation.adapter.ConfigureMapReduceAdapter;
import org.jumbune.debugger.instrumentation.adapter.ContextWriteLogAdapter;
import org.jumbune.debugger.instrumentation.adapter.ContextWriteValidationAdapter;
import org.jumbune.debugger.instrumentation.adapter.DoNotDisturbAdapter;
import org.jumbune.debugger.instrumentation.adapter.InstrumentFinalizer;
import org.jumbune.debugger.instrumentation.adapter.InstrumentValidator;
import org.jumbune.debugger.instrumentation.adapter.JobAdapter;
import org.jumbune.debugger.instrumentation.adapter.MREntryExitAdapter;
import org.jumbune.debugger.instrumentation.adapter.MainAdapter;
import org.jumbune.debugger.instrumentation.adapter.MethodEntryExitAdapter;
import org.jumbune.debugger.instrumentation.adapter.PartitionerAdapter;
import org.jumbune.debugger.instrumentation.adapter.SubmitCaseAdapter;
import org.jumbune.debugger.instrumentation.adapter.UDVPatternValidator;
import org.jumbune.debugger.instrumentation.utils.ContextWriteParams;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.FileUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentConfig;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.jumbune.utils.ClassLoaderUtil;
import org.jumbune.utils.Instrumented;
import org.objectweb.asm.ClassWriter;



/**
 * This class is used to instrument a jar file. The output is generated in a new
 * jar file.
 * <p>
 * It instruments only class files in the jar. All other files are written back
 * as it is.
 * </p>
 * 

 */
public class JarInstrumenter extends Instrumenter {
	private static final String LOG4J_CONF_FILE = "log4j2.xml";
	private static final String LOG4J_FILE_PATH = "/resources/"
			+ LOG4J_CONF_FILE;
	private static Environment env;
	private static final Logger LOGGER = LogManager.getLogger(Instrumenter.class);

	public List<String> userDefValidationsClasses = new ArrayList<String>();
	public List<String> regexValidationsClasses = new ArrayList<String>();
	
	/**
	 * <p>
	 * Create a new instance of JarInstrumenter.
	 * </p>
	 */
	public JarInstrumenter(Loader loader) {
		super(loader);
		env = new Environment();
	}

	/**
	 * This method instruments a jar file. The source and destination files are
	 * picked from yaml configuration.
	 * 
	 * @throws IOException
	 * @see org.jumbune.debugger.instrumentation.instrumenter.Instrumenter#instrumentJar()
	 */
	public void instrumentJar() throws IOException {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		File fin = new File(yamlLoader.getInputFile());
		File fout = new File(yamlLoader.getInstrumentOutputFile());
		instrumentJar(fin, fout);
		createSymbolTableFile(yamlLoader,env);
	}

	private void createSymbolTableFile(Loader loader , Environment env) throws IOException {
		FileWriter fw = null;
		try {
			YamlLoader yamlLoader = (YamlLoader)loader;
			String absFilePath = yamlLoader.getJumbuneJobLoc()+"/logs/symbolTable.log";
			fw = new FileWriter(absFilePath);
			Map<String,String> map = env.getSymbolTable();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append(entry.getValue()).append("             ").append(entry.getKey()).append("\n");
				fw.write(sb.toString());
			}
			
			Map<String,String> map2 = env.getClassMethodSymbols();
			for (Map.Entry<String, String> entry : map2.entrySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append(entry.getKey()).append("             ").append(entry.getValue()).append("\n");
				fw.write(sb.toString());
			}
			
		} finally{
				if(fw!=null){
					fw.close();
				}
		}
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
		byte [] instrumentBytes=bytes;
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		boolean instrumentIfBlock = yamlLoader.isInstrumentEnabled("ifblock");
		boolean instrumentSwitchCase = yamlLoader
				.isInstrumentEnabled("switchcase");
		boolean instrumentMapreduceRegex = yamlLoader
				.isInstrumentEnabled("instrumentRegex");
		boolean instrumentMapreduceUserDefinedValidation = yamlLoader
				.isInstrumentEnabled("instrumentUserDefValidate");
		boolean instrumentJobPartitioner = yamlLoader
				.isInstrumentEnabled("partitioner");

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		// Step 1: check if already instrumented or is an interface
		InstrumentValidator ic = new InstrumentValidator(getLoader(), cw);
		instrumentBytes = InstrumentUtil.instrumentBytes(bytes, ic, cw);

		// Check if user defined commanded not to instrument this class
			if (ic.isDoNotInstrumentSuggested()) {
				return instrumentBytes;
			}
		

		// Whether the user defined validation class has implemented the
		// PatternValidator Interface

		// Step 2: check if the classes mentioned for userDefined validations
		// actually implement Jumbune PatternValidator
		UDVPatternValidator patternValidator = new UDVPatternValidator(getLoader(),
				cw);

		// Whether the user defined validation class has implemented the
		// PatternValidator Interface
		checkPatternValidator(patternValidator);


		// if the class has not been already instrumented
		if (!ic.getAlreadyInstrumented() && !ic.getInterface()) {

			instrumentBytes = processNonInstrumentatedClass(instrumentBytes,
					instrumentIfBlock, instrumentSwitchCase,
					instrumentMapreduceRegex,
					instrumentMapreduceUserDefinedValidation,
					instrumentJobPartitioner);

		}

		return instrumentBytes;
	}

	private byte[] processNonInstrumentatedClass(byte[] instrumentBytes,
			boolean instrumentIfBlock, boolean instrumentSwitchCase,
			boolean instrumentMapreduceRegex,
			boolean instrumentMapreduceUserDefinedValidation,
			boolean instrumentJobPartitioner) throws IOException {
		
		YamlLoader loader = (YamlLoader) getLoader();

		// getting all regex validations
		List<Validation> validations = loader.getRegex();

		for (Validation validation : validations) {
			regexValidationsClasses.add(validation.getClassname());
		}

		// getting all user validations
		validations = loader.getUserValidations();

		for (Validation validation : validations) {
			userDefValidationsClasses.add(validation.getClassname());
		}

		boolean isRegexValidationClass = regexValidationsClasses
				.contains(currentlyInstrumentingClass);
		boolean isUserDefValidationClass = userDefValidationsClasses
				.contains(currentlyInstrumentingClass);

		ClassWriter cw;
		byte[] instrumentBytesTmp = instrumentBytes;
		

		//add logging only if validations are enabled on a particular class. 
		if(isRegexValidationClass||isUserDefValidationClass)
	{	
		
		// Step 2: Handling for regex and user validations
		if (instrumentMapreduceRegex
				|| instrumentMapreduceUserDefinedValidation) {
			cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
					new ContextWriteValidationAdapter(getLoader(), cw), cw);
		}

		// Step 3: Add logging for map/reduce entry/exit
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
				new MREntryExitAdapter(getLoader(), cw,env), cw);

		// Step 4: Add logging for map/reduce entry/exit
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
				new MethodEntryExitAdapter(getLoader(), cw,env), cw);

		// Step 5: Add logging for Switch cases execution counters
		if (instrumentSwitchCase) {
			cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp, new CaseAdapter(
					getLoader(), cw,env), cw);
		}

		// Step 6: Add logging for if blocks, loop execution counters
		if (instrumentIfBlock) {
			cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
					new BlockLogAdapter(getLoader(), cw,env), cw);
		}
	}
		
		// Step 7: Partitioner handler
		if (instrumentJobPartitioner) {
			cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
					new PartitionerAdapter(getLoader(), cw), cw);
		}

		// Step 8: Add logging for map/reduce execution counters
		if (InstrumentConfig.INSTRUMENT_MAPREDUCE_EXECUTION) {
			cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
					new ConfigureMapReduceAdapter(getLoader(), cw,env), cw);
		}

		// add logging only if validations are enabled on a particular class.
/*		if (isRegexValidationClass || isUserDefValidationClass) {

			// Step 9: Add logging for map/reduce context.write method calls
			if (InstrumentConfig.INSTRUMENT_CONTEXT_WRITE) {
				cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				instrumentBytesTmp = InstrumentUtil.instrumentBytes(
						instrumentBytesTmp, new ContextWriteLogAdapter(
								getLoader(), cw), cw);
			}

		}
*/
		// Step 10: Handling Chained tasks
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
				new ChainedTaskClassAdapter(getLoader(), cw), cw);

		// Step 11: Add class loading at runtime in main methods
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp, new MainAdapter(
				getLoader(), cw), cw);

		// Step 12: for Handling Job.submit case for new API.
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
				new SubmitCaseAdapter(getLoader(), cw), cw);

		// Step 13: Modify the job output path
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp, new JobAdapter(
				getLoader(), cw), cw);

		// Step 14: Marking the class as instrumented. It won't be
		// instrumented again.
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		instrumentBytesTmp = InstrumentUtil.instrumentBytes(instrumentBytesTmp,
				new InstrumentFinalizer(getLoader(), cw,env), cw);

		resetContextWriteParams();
		return instrumentBytesTmp;
	}

	
	/***
	 * Check whether pattern validator is enabled or not 
	 * @param patternValidator
	 * @throws IOException
	 */
	private void checkPatternValidator(UDVPatternValidator patternValidator) throws IOException {
		if (!patternValidator.getValidValidation()) {
			throw new IOException(
					MessageFormat.format(
							InstrumentationMessageLoader
									.getMessage(MessageConstants.NOT_IMPLEMENTING_PATTERNVALIDATOR),
							patternValidator.getClassName()));
		}
	}

	/**
	 * Reseting ContextWriteParams is must since the next class/jar should again
	 * copy its context write parameters into new local variables
	 */
	private void resetContextWriteParams() {
		ContextWriteParams.getInstance().setTempKeyVariableIndex(0);
		ContextWriteParams.getInstance().setTempValueVariableIndex(0);
	}

	/**
	 * <p>
	 * Adds other files to the instrumented files. Dependent jar files and
	 * resources for thick jar.
	 * </p>
	 * 
	 * @param zos
	 *            Stream for output file
	 
	 *             If any error occurred
	 */
	@Override
	public void addAdditionalFiles(ZipOutputStream zos) throws IOException {
		// adding util classes to the jar file
		ZipEntry xmlFileEntry = new ZipEntry(LOG4J_CONF_FILE);
		zos.putNextEntry(xmlFileEntry);
		String filePath = YamlLoader.getjHome() + LOG4J_FILE_PATH;
		byte[] buffer = new byte[InstrumentConstants.FOUR_ZERO_NINE_SIX];
		InputStream is = new FileInputStream(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int read = 0;
		while ((read = is.read(buffer)) != -1) {
			baos.write(buffer, 0, read);
		}
		zos.write(baos.toByteArray());
		zos.closeEntry();

		addUtilClasses(getLoader(), zos);
	} 
	/**
	 * <p>
	 * Adds required util classes to the instrumented jar file. This files will
	 * be required in case of Jumbune dependencies are opted not to included in
	 * instrumented jar.
	 * </p>
	 * 
	 * @param zos
	 *            The output file
	 * @throws IOException
	 *             If any error occurred
	 */
	public static void addUtilClasses(Loader loader, ZipOutputStream zos)
			throws IOException {
		if (FileUtil.getJumbuneClassPathType(loader) == ClasspathUtil.CLASSPATH_TYPE_LIBJARS) {
			String[] classes = new String[] { ClassLoaderUtil.class.getName(),
					Instrumented.class.getName() };

			for (String clazz : classes) {
				LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
						.getMessage(MessageConstants.ADDING_CLASS_TO_ARCHIVE),
						clazz));
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				DoNotDisturbAdapter dnd = new DoNotDisturbAdapter(loader, cw);

				byte[] bytes = InstrumentUtil.instrumentBytes(clazz, dnd, cw);

				ZipEntry outputEntry = new ZipEntry(
						ConfigurationUtil.convertQualifiedClassNameToInternalName(clazz)
								+ InstrumentConstants.CLASS_FILE_EXTENSION);
				zos.putNextEntry(outputEntry);
				zos.write(bytes);
				zos.closeEntry();
			}
		}
	}

}
