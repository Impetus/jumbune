package org.jumbune.debugger.instrumentation.adapter;

import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * This class is base instrumenter. All the adapters using ASM tree api should extend this class.
*/
public class BaseAdapter extends ClassNode implements InstrumentConstants {
	private String clazzName;
	private String superClazzName;
	private boolean isMapperClazz = false;
	private boolean isReducerClazz = false;
	private boolean isOldApiClazz = false;
	private final YamlLoader yLoader;

	protected YamlLoader getLoader() {
		return yLoader;
	}

	protected static final int CONTEXT_VARIABLE_IN_MAPREDUCE = 3;

	/**
	 * <p>
	 * Create a new instance of BaseAdapter.
	 * </p>
	 * 
	 * @param api
	 *            API version of ASM
	 */
	public BaseAdapter(YamlLoader loader, int api) {
		super(api);
		this.yLoader = loader;
	}

	/**
	 * <p>
	 * Create a new instance of BaseAdapter.
	 * </p>
	 * 
	 * @param cv
	 */
	public BaseAdapter(YamlLoader loader, ClassVisitor cv) {
		super(Opcodes.ASM4);
		this.cv = cv;
		this.yLoader = loader;
	}

	/**
	 * <p>
	 * Create a new instance of BaseAdapter.
	 * </p>
	 * 
	 * @param api
	 * @param cv
	 */
	public BaseAdapter(YamlLoader loader, int api, ClassVisitor cv) {
		super(api);
		this.cv = cv;
		this.yLoader = loader;
	}

	/**
	 * <p>
	 * Get the name of the class being instrumented
	 * </p>
	 * 
	 * @return String class name being instrumented
	 */
	public final String getClassName() {
		return this.clazzName;
	}

	/**
	 * <p>
	 * Sets the name of the class being instrumented
	 * </p>
	 * 
	 * @param clazzName
	 *            Qualified Name of the class being instrumented
	 */
	public final void setClassName(String clazzName) {
		this.clazzName = ConfigurationUtil.convertInternalClassNameToQualifiedName(clazzName);
	}

	/**
	 * <p>
	 * Get the name of the super class of the class being instrumented
	 * </p>
	 * 
	 * @return String super class of the class being instrumented
	 */
	public final String getSuperClassName() {
		return superClazzName;
	}

	/**
	 * <p>
	 * Sets the name of the super class of the class being instrumented
	 * </p>
	 * 
	 * @param superClassName
	 *            name of super class of the class being instrumented
	 */
	private void setSuperClassName(String superClassName) {
		this.superClazzName = superClassName;
	}

	/**
	 * <p>
	 * This method finds whether the class being instrumented is a mapper or not.
	 * </p>
	 * 
	 * @return boolean true id the class being instrumented is a mapper
	 */
	public final boolean isMapperClass() {
		return isMapperClazz;
	}

	/**
	 * <p>
	 * Sets whether the class being instrumented is a mapper or not
	 * </p>
	 * 
	 * @param isMapperClass
	 *            true if the class being instrumented is a mapper
	 */
	private void setMapperClass(boolean isMapperClass) {
		this.isMapperClazz = isMapperClass;
	}

	/**
	 * <p>
	 * This method finds whether the class being instrumented is a reducer or not.
	 * </p>
	 * 
	 * @return boolean true id the class being instrumented is a reducer
	 */
	public final boolean isReducerClass() {
		return isReducerClazz;
	}

	/**
	 * <p>
	 * Sets whether the class being instrumented is a reducer or not
	 * </p>
	 * 
	 * @param isReducerClass
	 *            true if the class being instrumented is a reducer
	 */
	private void setReducerClass(boolean isReducerClass) {
		this.isReducerClazz = isReducerClass;
	}

	/**
	 * This method is called when a class being visited
	 * 
	 * @param name
	 *            In the format "util/a/b/c"
	 * @see org.objectweb.asm.tree.ClassNode#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		setClassName(name);

		setSuperClassName(superName);

		// for both new and old API //PM
		if (isMapperClass(superName, interfaces)) {
			setMapperClass(true);
		} else if (isReducerClass(superName, interfaces)) {
			setReducerClass(true);
		}

		setOldApiClazz(superName, interfaces);

		// User provided classes
		if (yLoader.getMapperSuperClasses() != null && CollectionUtil.arrayContains(yLoader.getMapperSuperClasses(), superName)) {
			setMapperClass(true);
		} else if (yLoader.getReducerSuperClasses() != null && CollectionUtil.arrayContains(yLoader.getReducerSuperClasses(), superName)) {
			setReducerClass(true);
		}

		super.visit(version, access, name, signature, superName, interfaces);
	}

	/**
	 * <p>
	 * This method finds whether the class being visited is reduce
	 * </p>
	 * 
	 * @param superName
	 *            Super class name being visited
	 * @param interfaces
	 *            Interfaces being implemented by class being visited
	 * @return true if the method being visited is reduce
	 */
	private boolean isReducerClass(String superName, String[] interfaces) {
		return (CLASSNAME_MAPREDUCEBASE.equals(superName) && CollectionUtil.arrayContains(interfaces, INTERFACENAME_REDUCER))
				|| CLASSNAME_REDUCER.equals(superName);
	}

	/**
	 * <p>
	 * This method finds whether the class being visited is mapper
	 * </p>
	 * 
	 * @param superName
	 *            Super class name being visited
	 * @param interfaces
	 *            Interfaces being implemented by class being visited
	 * @return true if the method being visited is reduce
	 */
	private boolean isMapperClass(String superName, String[] interfaces) {

		return (CLASSNAME_MAPREDUCEBASE.equals(superName) && CollectionUtil.arrayContains(interfaces, INTERFACENAME_MAPPER))
				|| CLASSNAME_MAPPER.equals(superName);
	}

	@Override
	public void visitEnd() {
		accept(cv);
	}

	/**
	 * <p>
	 * This method finds whether the method being visited is map
	 * </p>
	 * 
	 * @param mn
	 *            MethodNode method being visited
	 * @return true if the method being visited is map
	 */
	public boolean isMapMethod(MethodNode mn) {
		return isMapperClass() && MAP_METHOD.equals(mn.name) && !InstrumentUtil.isSysntheticAccess(mn.access);
	}

	/**
	 * <p>
	 * This method finds whether the method being visited is reduce
	 * </p>
	 * 
	 * @param mn
	 *            MethodNode method being visited
	 * @return true if the method being visited is reduce
	 */
	public boolean isReduceMethod(MethodNode mn) {
		return isReducerClass() && REDUCE_METHOD.equals(mn.name) && !InstrumentUtil.isSysntheticAccess(mn.access);
	}

	/**
	 * <p>
	 * This method finds whether the class being visited is deprecated/old api
	 * </p>
	 * 
	 * @param superName
	 *            Super class name being visited
	 * @param interfaces
	 *            Interfaces being implemented by class being visited
	 * @return true if the method being visited is reduce
	 */
	private void setOldApiClazz(String superName, String[] interfaces) {
		if (CLASSNAME_MAPREDUCEBASE.equals(superName)
				&& (CollectionUtil.arrayContains(interfaces, INTERFACENAME_MAPPER) || CollectionUtil.arrayContains(interfaces, INTERFACENAME_REDUCER))) {
			isOldApiClazz = true;
		}
	}

	/**
	 * This method returns if the class being visited belongs to old api.
	 * 
	 * @return true if the visited class belongs to old api
	 */
	public boolean isOldApiClazz() {
		return isOldApiClazz;
	}

	/**
	 * <p>
	 * This method gets the class name which will be logged in the log files
	 * </p>
	 * 
	 * @return String Class name to be logged in class name
	 */
	public String getLogClazzName() {
		return isMapperClass() || isReducerClass() ? "" : getClassName();
	}

	/**
	 * <p>
	 * Gets the previous line number for the given node
	 * </p>
	 * 
	 * @param ain
	 *            node
	 * @return int line number
	 */
	public int getPreviousLineNumber(AbstractInsnNode ain) {
		AbstractInsnNode lineNode = ain.getPrevious();
		while (!(lineNode instanceof LineNumberNode)) {
			lineNode = lineNode.getPrevious();
		}
		return ((LineNumberNode) lineNode).line;
	}

	/**
	 * <p>
	 * Gets the next line number for the given node
	 * </p>
	 * 
	 * @param ain
	 *            node
	 * @return int line number
	 */
	public int getNextLineNumber(AbstractInsnNode ain) {
		AbstractInsnNode lineNode = ain.getNext();
		while (!(lineNode instanceof LineNumberNode)) {
			lineNode = lineNode.getNext();
		}
		return ((LineNumberNode) lineNode).line;
	}

	/**
	 * <p>
	 * Gets the previous line number node for the given node
	 * </p>
	 * 
	 * @param ain
	 *            node
	 * @return ain line number node
	 */
	public AbstractInsnNode getPreviousLineNode(AbstractInsnNode ain) {
		AbstractInsnNode lineNode = ain.getPrevious();
		while (!(lineNode instanceof LineNumberNode)) {
			lineNode = lineNode.getPrevious();
		}
		return lineNode;
	}

	/**
	 * <p>
	 * Gets the next line number node for the given node
	 * </p>
	 * 
	 * @param ain
	 *            node
	 * @return ain line number node
	 */
	public AbstractInsnNode getNextLineNode(AbstractInsnNode ain) {
		AbstractInsnNode lineNode = ain.getNext();
		while (!(lineNode instanceof LineNumberNode)) {
			lineNode = lineNode.getNext();
		}
		return lineNode;
	}
}
