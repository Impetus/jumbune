package org.jumbune.remoting.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;

/**
 * The Class RemotingMethodInvocationUtil.
 * This class contains a method <code>invokeMethodFromRemotingMethodConstants(CommandWritable commandWritable, Command command)</code> 
 * to facilitate invocation of all the methods from single method just by passing
 * in, CommandWritable and Command instances along with the classname that the target methods reside in. 
 * It can be used to invoke any of the methods listed in {@link org.jumbune.remoting.common.RemotingMethodConstants}.
 * For method definition, see {@link org.jumbune.remoting.server.invocations.CommandAsObjectResponserMethods} and 
 * {@link org.jumbune.remoting.server.invocations.CommandDelegatorMethods}
 */
public final class RemotingMethodInvocationUtil {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(RemotingMethodInvocationUtil.class);

	/**
	 *  This method invokes when the command is to be invoked is in a form of method.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @return the object
	 * @throws ReflectiveOperationException the reflective operation exception
	 */
	public static Object invokeMethodFromRemotingMethodConstants (
			CommandWritable commandWritable, Command command, Class<?> methodHandlerClass) throws ReflectiveOperationException {
		java.lang.reflect.Method method = null;
			method = methodHandlerClass.getDeclaredMethod(
					commandWritable.getMethodToBeInvoked(), Command.class);
			LOGGER.debug("going to invoke method:"+method.getName());
			return (Object) method.invoke(
					methodHandlerClass.newInstance(), command);
	}

	
}
