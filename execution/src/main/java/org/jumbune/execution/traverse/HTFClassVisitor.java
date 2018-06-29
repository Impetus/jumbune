/**
 * 
 */
package org.jumbune.execution.traverse;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author sasomani
 */
public class HTFClassVisitor extends ClassVisitor {
	private static Logger logger = LogManager.getLogger("launcher");
	private List<String> jobClassList;
	private static final char FULLY_QUALIFIED_CLASS_SEPARATOR = '/';
	private static final char DOT = '.';
	private String className;

	public HTFClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}

	/**
	 * this method is called for each class visited in given jar.
	 * 
	 * @param
	 * 
	 * @return void
	 */
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		className = name;
		cv.visit(version, access, name, signature, superName, interfaces);

	}

	/**
	 * this method is called for each method presented in the visited class.
	 * 
	 * @param
	 * 
	 * @return void
	 */

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		if (jobClassList == null) {
			jobClassList = new ArrayList<String>();
		}
		if (name.equalsIgnoreCase("main")
				&& access == Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC
				&& desc.equals("([Ljava/lang/String;)V")) {
			className = className.replace(FULLY_QUALIFIED_CLASS_SEPARATOR, DOT);
			logger.debug("Job class  : " + className);
			jobClassList.add(className);
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

	// visitEnd
	public void visitEnd() {
		cv.visitEnd();
	}

	public List<String> getJobClassList() {
		return jobClassList;
	}
}
