 /**
 * 
 */
package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.InstructionsBean;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.ContextWriteParams;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.jumbune.debugger.instrumentation.utils.MethodByteCodeUtil;
import org.jumbune.utils.beans.LogInfoBean;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * This class adds regular expression validations and add calls for User defined
 * classes which perform validations of key/values
 * 
 */
public class ContextWriteValidationAdapter extends BaseAdapter {
	private static final Logger LOG = LogManager
			.getLogger(ContextWriteValidationAdapter.class);

	private static String validateingMessage=null;

	/**
	 * Creates new instance of ContextWriteValidationAdapter
	 * @param loader
	 * @param cv
	 */
	public ContextWriteValidationAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		validateingMessage = InstrumentationMessageLoader
				.getMessage(MessageConstants.VALIDATION_KEY_VALUE);
	}

	/**
	 * This method first copies the key/value to new temporary variables and
	 * pass these variables to either PatternMatcher class or the class
	 * specified by user. The call to these classes is embedded in Log method so
	 * the result returned will be printed. To summarize this method will modify
	 * context.write method for copying parameters to local variables and it
	 * will then add a call for Log class which prints the result of
	 * validation/matching by taking in message a call to these
	 * PatternMatcher/User defined validation class. Sample output:
	 * 
	 * context.write(tempKey = key, tempVal = value); Log.info("result " +
	 * PatternMatcher.match(tempVal, regEx)); Log.info("validation result " +
	 * UserSpecifiedClass.method(tempVal));
	 */
	@Override
	public void visitEnd() {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		if (validateUserValidationClass(yamlLoader.getUserValidations(),
				getClassName())
				|| validateRegexValidationClass(yamlLoader.getRegex(),
						getClassName())) {
			for (int i = 0; i < methods.size(); i++) {
				MethodNode mn = (MethodNode) methods.get(i);
				int variableIndex = mn.maxLocals;

				/**
				 * context.write/output.collect has been written in the
				 * map/reduce methods and other user defined functions. Applying
				 * filter to skip synthetic methods.
				 */
				if (!InstrumentUtil.isSysntheticAccess(mn.access)) {
					InsnList insnList = mn.instructions;
					AbstractInsnNode[] insnArr = insnList.toArray();
					int writeCount = 0;
					for (int j = 0; j < insnArr.length; j++) {
						AbstractInsnNode abstractInsnNode = insnArr[j];

						if (abstractInsnNode instanceof MethodInsnNode) {
							MethodInsnNode min = (MethodInsnNode) abstractInsnNode;

							// write method
							if (InstrumentUtil.isOutputMethod(min)) {

								LOG.info(MessageFormat.format(
										InstrumentationMessageLoader
												.getMessage(MessageConstants.LOG_ADDING_REGEX_VALIDATION_CALL),
										getClassName() + "##" + mn.name,
										writeCount++));

								InstructionsBean insBean = MethodByteCodeUtil
										.readMethodAndCopyParamToTemporaryVariables(
												min.getPrevious(),
												variableIndex, insnList,
												mn.localVariables);

								// Add the instance of temporary key/value index
								// to ContextWriteParams
								ContextWriteParams
										.getInstance()
										.setTempKeyVariableIndex(
												insBean.getTemporaryVariablesIndexList()
														.get(KEY_INDEX));
								ContextWriteParams
										.getInstance()
										.setTempValueVariableIndex(
												insBean.getTemporaryVariablesIndexList()
														.get(VALUE_INDEX));

								InsnList patternValidationInsnList = new InsnList();
								LOG.debug("***** Just going to add validations  keyIndex "
										+ insBean
												.getTemporaryVariablesIndexList()
												.get(KEY_INDEX)
										+ " Value Index "
										+ insBean
												.getTemporaryVariablesIndexList()
												.get(VALUE_INDEX));

								LOG.debug("ContextWriteParams.getInstance() KEY --  "
										+ ContextWriteParams.getInstance()
												.getTempKeyVariableIndex());
								addValidations(patternValidationInsnList, 
										insBean, mn.name);

								if (patternValidationInsnList != null
										&& patternValidationInsnList.size() > 0) {
									insnList.insert(abstractInsnNode,
											patternValidationInsnList);
								}
							}
						}
					}
				}
				mn.visitMaxs(0, 0);
			}
		}
		accept(cv);
	}

	/**
	 * It first checks if user has enabled UserDefined validations for key/Value
	 * based on map/reduce methods if so it will create instructionList to
	 * create logging statements which call the given class for validating data.
	 * If for a given key/value validatingClass is not mentioned but a regEx is
	 * given then logging statement would be added which would call framework's
	 * PatternMatcher.match() and would return a boolean value. If both the
	 * userDefinedValidation and RegEx is given by user preference would be
	 * given to UserDefinedValidations
	 * 
	 * @param patternValidationInsnList
	 *            - List in which modified statements be populated
	 * @param insBean
	 *            - the InstructionBean holds the index of variables in which
	 *            key/value is temporarily saved
	 * @param methodName
	 *            - method which holds context.write either map/reduce
	 */
	private void addValidations(InsnList patternValidationInsnList, InstructionsBean insBean, String methodName) {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		String keyValidationClass = yamlLoader
				.getMapReduceKeyValidator(getClassName());
		String valueValidationClass = yamlLoader
				.getMapReduceValueValidator(getClassName());
		boolean instrumentMapreduceUserdefinedValidation = yamlLoader
				.isInstrumentEnabled("instrumentUserDefValidate");
		boolean instrumentMapreduceRegex = yamlLoader
				.isInstrumentEnabled("instrumentRegex");

		int keyIndex = 0;
		int valueIndex = 1;

		boolean[] isValidated = new boolean[2];
		String[] validators = new String[2];

		if (instrumentMapreduceUserdefinedValidation && validateUserValidationClass(yamlLoader.getUserValidations(),
					getClassName())) {
				validators[keyIndex] = keyValidationClass;
				validators[valueIndex] = valueValidationClass;

				int index = 0;
				for (String validator : validators) {
					boolean isKey = false;
					if (index == keyIndex){
						isKey = true;
					}

					if (!CollectionUtil.isNullOrEmpty(validator)) {
						isValidated[index] = true;
						patternValidationInsnList
								.add(validateUsingUserDefinedValidation(
										insBean, methodName, isKey, validator));
					}
					index++;
				}
			
		}

		addRegexValidations(patternValidationInsnList, insBean, methodName, instrumentMapreduceRegex, isValidated);

	}
	
	private void addRegexValidations(InsnList patternValidationInsnList, InstructionsBean insBean, String methodName,
			boolean instrumentMapreduceRegex, boolean[] isValidated) {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		String keyRegex = yamlLoader.getMapReduceKeyRegex(getClassName());
		String valueRegex = yamlLoader.getMapReduceValueRegex(getClassName());
		int keyIndex = 0;
		int valueIndex = 1;
		if (instrumentMapreduceRegex && validateRegexValidationClass(yamlLoader.getRegex(), getClassName())) {
				// Fetching regEx for validating key/value
				String[] validators=new String[2];
				validators[keyIndex] = keyRegex;
				validators[valueIndex] = valueRegex;

				int index = 0;
				for (boolean isValidationPerformed : isValidated) {
					boolean isKey = true;
					// Validation of key/value was not done using
					// PatternValidator, so we will check if corresponding regEx
					// is given apply that
					if (!isValidationPerformed) {
						if (index == valueIndex) {
							isKey = false;
						}
						patternValidationInsnList.add(addCallForPatternMatcher(
								methodName, insBean, isKey,
								validators[index]));
					}
					index++;
				}
			
		}
	}
	/**
	 * Validates whether the mapper/reducer class is mentioned in the Yaml
	 * configuration for regex validation
	 * 
	 * @param list
	 *            identify the list of mapper/reducer classes.
	 * @param className
	 *            identify the mapper/reducer class.
	 * @return true if class is is mentioned in the Yaml configuration for regex
	 *         validation
	 */
	private boolean validateRegexValidationClass(List<Validation> list,
			String className) {
		boolean isValidClass = false;
		for (int i = 0; i < list.size(); i++) {
			Validation validation = list.get(i);
			if (validation.getClassname() != null
					&& validation.getClassname().equals(className)) {
				isValidClass = true;
				break;
			}
		}

		return isValidClass;
	}

	/**
	 * Validates whether the mapper/reducer class is mentioned in the Yaml
	 * configuration for user validation
	 * 
	 * @param list
	 *            identify the list of mapper/reducer classes.
	 * @param className
	 *            identify the mapper/reducer class.
	 * @return true if class is is mentioned in the Yaml configuration for user
	 *         validation
	 */
	private boolean validateUserValidationClass(List<Validation> list,
			String className) {
		boolean isValidClass = false;
		for (int i = 0; i < list.size(); i++) {
			Validation validation = list.get(i);
			if (validation.getClassname() != null
					&& validation.getClassname().equals(className)) {
				isValidClass = true;
				break;
			}
		}

		return isValidClass;
	}

	/**
	 * This method add calls to LogUtil. taking message which contains a call to
	 * PatternMatcher class and will match key/value to given regular expression
	 * 
	 * @param methodName
	 *            - name of the method which is currently being traversed
	 * @param insBean
	 *            - the InstructionBean holds the index of variables in which
	 *            key/value is temporarily saved
	 * @return - InstructionList containing instruction set for matching
	 *         key/value against regular expression and logging the same
	 */
	private InsnList addCallForPatternMatcher(String methodName,
			InstructionsBean insBean,boolean isKey,
			String regEx) {

		int varIndex = 0;

		if (!isKey) {
			varIndex = 1;
		}
		LogInfoBean bean = new LogInfoBean(getLogClazzName(), methodName,
				validateingMessage, null);
		InsnList patternMatcherInsnList = new InsnList();

		LOG.info("User provided regEx " + regEx);

		// Adding call for Key regular Expression
		if (isNullString(regEx)) {
			prepareInsnListForRegEx(patternMatcherInsnList, isKey, bean,
					insBean, true);
		} else if (!CollectionUtil.isNullOrEmpty(regEx)
				&& (insBean.getTemporaryVariablesIndexList().get(varIndex) != InstrumentConstants.PARAMETER_NULL_INDEX)) {
			prepareInsnListForRegEx(patternMatcherInsnList, isKey, bean,
					insBean, false);
		}

		return patternMatcherInsnList;
	}

	/**
	 * This method prepares instructions for validating key/value against user
	 * defined validation. By calling the UserDefined validation class and
	 * logging it.This method handles both map/reduce validations
	 * 
	 * @param min
	 * @param insBean
	 * @param methodName
	 * @param isKey
	 * @param patternValidatorClass
	 * @return
	 */
	private InsnList validateUsingUserDefinedValidation(
			InstructionsBean insBean, String methodName, boolean isKey,
			String patternValidatorClass) {
		int variableIndex = 0;
		String validatorFieldName = null;

		LogInfoBean lBean = new LogInfoBean(getLogClazzName(), methodName,
				validateingMessage, null);

		InsnList validatingInsnList = new InsnList();
		if (isKey) {
			lBean.setMsgSuffix("K");
			validatorFieldName = InstrumentConstants.KEY_VALIDATOR;
		} else {
			lBean.setMsgSuffix("V");
			validatorFieldName = InstrumentConstants.VALUE_VALIDATOR;
			variableIndex = 1;
		}

		// Add logging only when null is not passed in context.write
		if (insBean.getTemporaryVariablesIndexList().get(variableIndex) != InstrumentConstants.PARAMETER_NULL_INDEX) {
			validatingInsnList.add(InstrumentUtil
					.addLoggerWithClassMethodCall(lBean, insBean
							.getTemporaryVariablesIndexList()
							.get(variableIndex), validatorFieldName,
							patternValidatorClass, getClassName()));
		}

		return validatingInsnList;
	}

	/**
	 * This method prepares instructionList which adds logging statement based
	 * on the fact if the regEx is null or not
	 * 
	 * @param patternMatcherInsnList
	 * @param isKey
	 * @param bean
	 * @param insBean
	 * @param isRegExNull
	 */
	private void prepareInsnListForRegEx(InsnList patternMatcherInsnList,
			boolean isKey, LogInfoBean bean, InstructionsBean insBean,
			boolean isRegExNull) {
		int variableIndex = 0;
		String pattern = null;

		if (isKey) {
			pattern = InstrumentConstants.KEY_PATTERN;
			bean.setMsgSuffix("K");
		} else {
			variableIndex = 1;
			pattern = InstrumentConstants.VALUE_PATTERN;
			bean.setMsgSuffix("V");
		}
		if (isRegExNull) {
			patternMatcherInsnList.add(InstrumentUtil
					.addRegExMatcherClassCall(bean, insBean
							.getTemporaryVariablesIndexList()
							.get(variableIndex)));
		} else if (insBean.getTemporaryVariablesIndexList().get(variableIndex) != InstrumentConstants.PARAMETER_NULL_INDEX) {
			// Add logging only when null is not passed in context.write
			patternMatcherInsnList.add(InstrumentUtil
					.addRegExMatcherClassCall(bean, insBean
							.getTemporaryVariablesIndexList()
							.get(variableIndex), pattern, getClassName()));
		}
	}

	/**
	 * This method just checks if the string given in regEx is 'null'
	 * 
	 * @param regEx
	 * @return true if the regEx given is null
	 */
	private boolean isNullString(String regEx) {
		final String nullStr = "null";

		if (nullStr.equals(regEx)) {
			return true;
		}
		return false;
	}
}