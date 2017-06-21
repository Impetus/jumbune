package org.jumbune.remoting.server.ha.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.AgentNode;
import org.jumbune.remoting.common.AgentNodeStatus;
import org.jumbune.remoting.common.ZKUtils;
import org.jumbune.remoting.server.JumbuneAgent;

/**
 * The Class RsyncTask which will run rsync commands from leader to sync agent metadata on follower agent node.
 */
public class RSyncTask implements Runnable {

	/** The Constant LOGGER. */
	public static final Logger LOGGER = LogManager.getLogger(RSyncTask.class);

	private String zkConnectionString;
	
	public RSyncTask(String zkConnectionString) {
		this.zkConnectionString = zkConnectionString;
	}

	/* async execution of rsync command
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			long startTimeInMilis = Calendar.getInstance().getTimeInMillis();
			LOGGER.info("Started rsync execution at "
					+ Calendar.getInstance().getTime());

			// get agents node information from zookeeper
			List<AgentNode> agentNodes = ZKUtils.getAgents(new String[] {this.zkConnectionString});

			// iterate over agent list and run rsync command
			for (AgentNode agent : agentNodes) {
				LOGGER.debug("Agent Object :" + agent.toString());

				if (agent.getStatus().equals(AgentNodeStatus.LEADER)) {
					LOGGER.debug("Skipping rsync execution for Leader Agent Object :");
					continue;
				}

				if (agent.getPrivateKey() != null
						&& !agent.getPrivateKey().trim().equals("")) {
					rsyncWithPasswordlessSSH(agent);
				}
			}

			LOGGER.info("Total rsync execution time : "+ (Calendar.getInstance().getTimeInMillis() - startTimeInMilis)+ "ms.");
		} catch (Exception e) {
			LOGGER.error("Rsync execution failed.", e);
		}
	}

	/**
	 * Rsync with passwordless ssh.
	 *
	 * @param remoteAgentNode the remote agent node
	 */
	private void rsyncWithPasswordlessSSH(AgentNode remoteAgentNode) {

		ProcessBuilder processBuilder = null;
		String commandString = "";

		// create ProcessBuilder with rsync command arguments
		String[] command = {
				"rsync",
				"-azvO",
				"--delete",
				"-e",
				"/usr/bin/ssh -i " + remoteAgentNode.getPrivateKey(),
				".",
				remoteAgentNode.getAgentUser() + "@"
						+ remoteAgentNode.getHost() + ":"
						+ remoteAgentNode.getAgentHomeDir() };
		commandString = Arrays.toString(command);
		processBuilder = createProcessBuilder(command);

		processBuilder.redirectErrorStream(true);

		// Set up work directory
		processBuilder.directory(new File(JumbuneAgent.getAgentDirPath()));

		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			LOGGER.warn("Failed to start rsync process. Skipping rsync execution for this node.", e);
			return;
		}

		// Read out output
		BufferedReader br = null;
		String line = "";
		LOGGER.debug("Output of running command [{}] is:", commandString);

		try {
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = br.readLine()) != null) {
				LOGGER.debug(line);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to read rsync process output", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to close BufferedReader", e);
				}
			}
		}

		// Wait to get exit value
		try {
			int exitValue = process.waitFor();
			LOGGER.debug("Exit Value is " + exitValue);
			if (exitValue == 0) {
				LOGGER.debug("Successfully synced AGENT_HOME directory.");
			} else {
				LOGGER.warn("Failed to sync AGENT_HOME directory.");
			}
		} catch (InterruptedException e) {
			LOGGER.error("rsync process interrupted", e);
		}
	}

	/**
	 * Creates the process builder.
	 *
	 * @param command the command
	 * @return the process builder
	 */
	private ProcessBuilder createProcessBuilder(String[] command) {
		return new ProcessBuilder(command);
	}
}
