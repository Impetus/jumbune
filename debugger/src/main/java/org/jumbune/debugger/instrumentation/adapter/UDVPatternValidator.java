/**
 * 
 */
package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;


/**
 * This class validates classes that are mentioned for performing userdefined
 * validations. These validations should be performed for validating
 * instrumented jar
 * 
 */
public class UDVPatternValidator extends BaseAdapter {

	private static final Logger LOG = LogManager
			.getLogger(UDVPatternValidator.class);

	/**
	 * <p>
	 * Create a new instance of UDVPatternValidator.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public UDVPatternValidator(YamlLoader loader, ClassVisitor cv) {
		super(loader, cv);
		this.cv = cv;
	}

	// property to identify whether the class is implement the PatternValidator
	// interface.
	private boolean isValidValidation = true;

	/**
	 * <p>
	 * Gets whether the class is implement the PatternValidator interface
	 * </p>
	 * 
	 * @return true if instrumented
	 */
	public boolean getValidValidation() {
		return this.isValidValidation;
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
		YamlLoader loader=getLoader();
		setClassName(name);
		super.visit(version, access, name, signature, superName, interfaces);

		String keyValidationClass = loader
				.getMapReduceKeyValidator(getClassName());
		String valueValidationClass = loader
				.getMapReduceValueValidator(getClassName());
		boolean instrumentMapreduceUserdefinedValidation = loader
				.isInstrumentEnabled("instrumentUserDefValidate");

		LOG.info(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.CLASS_BEING_INSTRUMENTED),
				getClassName()));

		if (instrumentMapreduceUserdefinedValidation && (name.equals(keyValidationClass)
				|| name.equals(valueValidationClass))) {
			
				isValidValidation = false;
				for (int i = 0; i < interfaces.length; i++) {
					if (interfaces[i]
							.equals(InstrumentConstants.CLASSNAME_PATTERNVALIDATOR)) {
						isValidValidation = true;
						break;
					}
				}
				if (!isValidValidation) {
					LOG.info(MessageFormat.format(
							InstrumentationMessageLoader
									.getMessage(MessageConstants.NOT_IMPLEMENTING_PATTERNVALIDATOR),
							getClassName()));
				}
			
		}
		accept(cv);
	}
}
