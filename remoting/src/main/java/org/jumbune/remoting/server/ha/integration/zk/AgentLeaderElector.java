package org.jumbune.remoting.server.ha.integration.zk;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatch.State;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.AgentNode;
import org.jumbune.remoting.common.AgentNodeStatus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AgentLeaderElector {

	public static final Logger LOGGER = LogManager.getLogger(AgentLeaderElector.class);
	
	public static final Logger CONSOLE_LOGGER = LogManager.getLogger("EventLogger");
	
	private final Gson GSON = new Gson();
    
	private final Type agentNodesSetType = new TypeToken<HashSet<AgentNode>>(){}.getType();
	
	private CuratorFramework curator = null;

	private LeaderLatch leaderLatch = null;

	private AgentNode agentNode = null;

	private String zkHost = null;

	public AgentLeaderElector(AgentNode agentNode, String zkHost) {
		this.zkHost = zkHost;
		this.agentNode = agentNode;
		curator = CuratorFrameworkFactory.newClient(this.zkHost, new ExponentialBackoffRetry(1000, 3));
		curator.start();
		
		createAgentZnodes();
		
		leaderLatch = new LeaderLatch(this.curator, ZKConstants.AGENT_FOLLOWER_PATH);
		
		HashSet<AgentNode> followers = getFollowers();
		if (followers == null) {
			followers = new HashSet<>();
		}
		followers.add(agentNode);
		CONSOLE_LOGGER.info("setting followers data - " + followers);
		try {
			curator.setData().forPath(ZKConstants.AGENT_FOLLOWER_PATH, GSON.toJson(followers).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		CONSOLE_LOGGER.info("new participant[agent] for election - " + agentNode);
		LOGGER.debug("new participant[agent] for election - " + agentNode);
	}

	public void tryToBeLeader() {
		leaderLatch.addListener(new LeaderLatchListener() {
			@Override
			public void notLeader() {
			}

			@Override
			public void isLeader() {
				try {
					//setting new agent at /jumbune/agent/leader
					agentNode.setStatus(AgentNodeStatus.LEADER);
					curator.setData().forPath(ZKConstants.AGENT_LEADER_PATH, GSON.toJson(agentNode).getBytes());		
                    updateFollowersListOnZk();			
		            CONSOLE_LOGGER.info("Leader event[agent], new leader - " + agentNode);
					LOGGER.debug("Leader event[agent], new leader - " + agentNode);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}

	private void createAgentZnodes() {
		try {
			if (curator.checkExists().forPath(ZKConstants.AGENT_LEADER_PATH) == null) {
				curator.create().creatingParentsIfNeeded().forPath(ZKConstants.AGENT_LEADER_PATH);
				curator.setData().forPath(ZKConstants.AGENT_LEADER_PATH, "".getBytes());
			}
			if (curator.checkExists().forPath(ZKConstants.AGENT_FOLLOWER_PATH) == null) {
				curator.create().creatingParentsIfNeeded().forPath(ZKConstants.AGENT_FOLLOWER_PATH);
				curator.setData().forPath(ZKConstants.AGENT_FOLLOWER_PATH, "".getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private HashSet<AgentNode> getFollowers() {
		String followersJson = null;
		try {
			followersJson = new String (curator.getData().forPath(ZKConstants.AGENT_FOLLOWER_PATH), StandardCharsets.UTF_8);
			if(followersJson == null || followersJson.isEmpty()) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		HashSet<AgentNode> followers = GSON.fromJson(followersJson, agentNodesSetType);
         return followers;
	}
	
	private void updateFollowersListOnZk() throws Exception {
		//getting all the followers from /jumbune/agent/follower 
		//and removing the leader and writing followers' list back.
		HashSet<AgentNode> followers = getFollowers();
        followers.remove(agentNode);		            
        curator.setData().forPath(ZKConstants.AGENT_FOLLOWER_PATH, GSON.toJson(followers).getBytes());
	}
	
	private void clearAgentEntryOnShutdown() {
		HashSet<AgentNode> followers = getFollowers();
		try {
			if (followers.remove(agentNode)) {
				updateFollowersListOnZk();
			} else {
			String leaderJson = new String(curator.getData().forPath(ZKConstants.AGENT_LEADER_PATH), StandardCharsets.UTF_8);   
			  if((leaderJson != null) && !leaderJson.isEmpty() && (GSON.fromJson(leaderJson, AgentNode.class)).equals(agentNode)) {
				  curator.setData().forPath(ZKConstants.AGENT_LEADER_PATH, "".getBytes());
			   }
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}	
	
	public void stop() {
		try {
			clearAgentEntryOnShutdown();
			CONSOLE_LOGGER.info("CLOSING LEADER LATCH [ "+ leaderLatch +" ]");
			leaderLatch.close();
			curator.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void participateInElection() {
		try {
			tryToBeLeader();
			leaderLatch.start();
			leaderLatch.await();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public State getState() {
		return leaderLatch.getState();
	}

}