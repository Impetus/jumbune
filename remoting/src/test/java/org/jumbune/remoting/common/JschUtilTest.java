package org.jumbune.remoting.common;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.jumbune.remoting.common.JschUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public class JschUtilTest {

	
	private String user;
	private String host;
	private String home;
	private String rsaFilePath;
	private String dsaFilePath;
	private Session session = null;

	@Before
	public void setUp() throws Exception {

		user = System.getProperty("user.name");
		home = System.getProperty("user.home");
		Process p = Runtime.getRuntime().exec("hostname");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		host = reader.readLine();
		rsaFilePath = home + "/.ssh/id_rsa";
		dsaFilePath = home + "/.ssh/id_dsa";
		session = JschUtil.createSession(user, host, rsaFilePath, "");
		java.util.Properties conf = new java.util.Properties();
		conf.put("StrictHostKeyChecking", "no");
		session.setConfig(conf);

	}

	//@Test
	public void testCreateSession_1() throws Exception {
		String user = System.getProperty("user.name");
		Process p = Runtime.getRuntime().exec("hostname");
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String host = reader.readLine();
		String rsaFilePath = home + "/.ssh/id_rsa";
		String dsaFilePath = home + "/.ssh/id_dsa";
		Session result = JschUtil.createSession(user, host, rsaFilePath,
				dsaFilePath);
		java.util.Properties conf = new java.util.Properties();
		conf.put("StrictHostKeyChecking", "no");
		result.setConfig(conf);
		assertNotNull(result);
		result.disconnect();
	}

	//@Test
	public void testExecuteEchoCommandUsingShell_2() throws Exception {

		String echoCommand = "echo $HADOOP_HOME \n \n ";
		String result = JschUtil.executeCommandUsingShell(session,
				echoCommand,"echo $HADOOP_HOME");
		assertNotNull(result);
	}

	//@Test(expected = java.io.IOException.class)
	public void testExecSlaveCleanUpTask_5() throws Exception {
		String[] commands = new String[] { "" };
		String directory = "tmp/";

		String result = JschUtil.execSlaveCleanUpTask(commands, directory);

		assertNotNull(result);
	}

	//@Test
	public void testGetChannel_1() throws Exception {

		String command = "";

		Channel result = JschUtil.getChannel(session, command);

		assertNotNull(result);
	}

	//@Test
	public void testGetShellChannel_1() throws Exception {

		String command = "";

		Channel result = JschUtil.getShellChannel(session, command);
		assertNotNull(result);
	}

	@After
	public void tearDown() throws Exception {
		session.disconnect();
	}
}