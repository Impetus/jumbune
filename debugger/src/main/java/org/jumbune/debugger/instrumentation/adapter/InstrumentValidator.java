package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;


/**
 * This class checks whether a class has already been instrumented or not.
 * <p>
 * </p>

 */
public class InstrumentValidator extends BaseAdapter {
	/**
	 * <p>
	 * Create a new instance of InstrumentValidator.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public InstrumentValidator(Config config, ClassVisitor cv) {
		super(config, cv);
		this.cv = cv;
	}

	private static final Logger LOGGER = LogManager
			.getLogger(InstrumentValidator.class);

	// property to identify the instrument status of the class
	private boolean isAlreadyInstrumented = false;

	/**
	 * <p>
	 * Gets the instrument status of the class
	 * </p>
	 * 
	 * @return true if instrumented
	 */
	public boolean getAlreadyInstrumented() {
		return this.isAlreadyInstrumented;
	}

	// property to identify whether the class is an interface
	private boolean isInterface = false;

	/**
	 * Property which holds value for the current class should be instrumented
	 * or not. Only those classes would return true which user mentioned in yaml
	 */
	private boolean isDoNotInstrumentSuggested;

	/**
	 * <p>
	 * Gets whether the class is an interface
	 * </p>
	 * 
	 * @return true if it is interface
	 */
	public boolean getInterface() {
		return this.isInterface;
	}

	// property to identify whether the class is implement the PatternValidator
	// interface.
	/**
	 * <p>
	 * Gets whether the class is implement the PatternValidator interface
	 * </p>
	 * 
	 * @return true if instrumented
	 */
	public boolean isDoNotInstrumentSuggested() {
		return isDoNotInstrumentSuggested;
	}

	/**
	 * This method is called when a class being visited
	 * 
	 * @param name
	 *            In the format "util/a/b/c"
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visit(int,
	 *      int, java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String[])
	 */
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		setClassName(name);
		super.visit(version, access, name, signature, superName, interfaces);

		// Don't isntrument classes which are mentioned by user for not being
		// instrumented. Also if user mentioned a package which should not
		// be instumented any class lying in that package should not be
		// instrumented

		if (isExcludeClassFromInstrumentation(name)) {
			LOGGER.debug("Excluding this class: " + name);
			return;
		}

		// Do not attempt to instrument interfaces or classes that have already
		// been instrumented
		if ((access & Opcodes.ACC_INTERFACE) != 0) {
			LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.INTERFACE_NOT_INSTRUMENT),
					getClassName()));
			this.isInterface = true;
		} else if (CollectionUtil.arrayContains(interfaces,
				CLASSNAME_CLASS_HAS_BEEN_INSTRUMENTED)) {
			LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.CLASS_ALREADY_INSTRUMENTED),
					getClassName()));
			this.isAlreadyInstrumented = true;
		}
	}

	private boolean isExcludeClassFromInstrumentation(String name) {
		
		JobConfig jobConfig = (JobConfig)getConfig();
		List<String> doNotInstrumentList = jobConfig
				.getCompleteDoNotInstrumentList();
		List<String> instrumentAnywaysList = jobConfig.getIncludeAnywaysList();

		if (instrumentAnywaysList != null) {
			for (String includeList : instrumentAnywaysList) {
				if (name.startsWith(includeList)) {
					return false;
				}
			}
		}

		for (String excludeNameList : doNotInstrumentList) {
			name.startsWith(excludeNameList);
			isDoNotInstrumentSuggested = true;
			return true;
		}

		return false;

	}
}
