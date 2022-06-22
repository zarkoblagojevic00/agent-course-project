package agentmanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import agents.Agent;
import agents.MasterAgent;
import util.JNDILookup;

/**
 * Session Bean implementation class AgentManagerBean
 */
@Singleton
@Remote(AgentManagerRemote.class)
@LocalBean
public class AgentManagerBean implements AgentManagerRemote {
	
	Map<String, Agent> runningAgents;
	
    public AgentManagerBean() {
    	runningAgents = new HashMap<String, Agent>();
    	startAgent(MasterAgent.MASTER_AGENT_ID, JNDILookup.MasterAgentLookup);
    }

    @Override
	public void startAgent(String agentId, String name) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		agent.init(agentId);
		runningAgents.put(agentId, agent);
	}

	@Override
	public void stopAgent(String agentId) {
		runningAgents.remove(agentId);
	}

	@Override
	public List<Agent> getRunningAgents() {
		return Collections.unmodifiableList((List<Agent>) runningAgents.values()) ;
	}

	@Override
	public Agent getRunningAgent(String agentId) {
		return runningAgents.get(agentId);
	}
}
