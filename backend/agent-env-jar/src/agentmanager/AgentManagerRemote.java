package agentmanager;

import java.util.List;

import javax.ejb.Remote;

import agents.AID;
import agents.Agent;
import agents.AgentType;
import model.Host;

@Remote
public interface AgentManagerRemote {
	public AID startAgent(AgentType type, String name);
	public void stopAgent(AID aid);
	public List<AID> getRunningAgents();
	public List<Agent> getRunningAgentsFull();
	public Agent getRunningAgent(AID aid);
	public List<AgentType> getAgentTypes();
	public void updateAgentTypes(List<AgentType> newTypes);
	public void updateRunningAgents(List<AID> aids);
	public void removeAllAgentInfoFromNode(Host node);
	public void removeAgent(String name);
}
