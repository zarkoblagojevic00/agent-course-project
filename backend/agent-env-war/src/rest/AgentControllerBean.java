package rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.AgentType;

@Stateless
@Path("/agents")
@Remote(AgentController.class)
public class AgentControllerBean implements AgentController {

	@EJB
	private AgentManagerRemote agentManager;
	
	@Override
	public List<AgentType> getAllAgentTypes() {
		return agentManager.getAgentTypes();
	}

	@Override
	public List<AID> getAllRunningAgents() {
		return agentManager.getRunningAgents();
	}

	@Override
	public void startAgent(AgentType type, String name) {
		agentManager.startAgent(type, name); 
	}

	@Override
	public void stopAgent(AID aid) {
		agentManager.stopAgent(aid);
	}

	@Override
	public void updateAgentTypes(List<AgentType> types) {
		agentManager.updateAgentTypes(types);
	}

	@Override
	public void updateRunningAgents(List<AID> agents) {
		agentManager.updateRunningAgents(agents);
	}

}
