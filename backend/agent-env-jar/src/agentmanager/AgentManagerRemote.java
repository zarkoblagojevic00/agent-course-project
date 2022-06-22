package agentmanager;

import java.util.List;

import javax.ejb.Remote;

import agents.Agent;

@Remote
public interface AgentManagerRemote {
	public void startAgent(String agentId, String name);
	public void stopAgent(String agentId);
	public List<Agent> getRunningAgents();
	public Agent getRunningAgent(String agentId);
}
