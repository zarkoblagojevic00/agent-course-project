package agentmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import agents.AID;
import agents.Agent;
import agents.AgentType;
import agents.ChatMasterAgent;
import agents.MasterHarvestAgent;
import agents.UserAgent;
import agents.harvester.MitrosHarvesterAgent;
import agents.harvester.PlayerRsHarvesterAgent;
import agents.harvester.SkyMusicHarvesterAgent;
import agents.search.MasterSearchAgent;
import agents.search.SlaveSearchAgent;
import connectionmanager.ConnectionManagerRemote;
import model.Host;
import rest.restclient.proxies.AgentResteasyClientProxy;
import util.JNDILookup;
import util.JsonMarshaller;
import ws.AgentTypesWebSocket;
import ws.RunningAgentsWebSocket;

/**
 * Session Bean implementation class AgentManagerBean
 */
@Singleton
@LocalBean
@Remote(AgentManagerRemote.class)
public class AgentManagerBean implements AgentManagerRemote {
	@EJB
	private ConnectionManagerRemote	connectionManager;
	
	@EJB
	private AgentTypesWebSocket wsTypes;
	
	@EJB
	private RunningAgentsWebSocket wsAgents;
	
	private Map<AID, Agent> runningAgents;
	
	private static final List<AgentType> AGENT_TYPES = Arrays.asList( 
			new AgentType(ChatMasterAgent.class.getSimpleName(), false),
			new AgentType(UserAgent.class.getSimpleName(), true),
			MasterHarvestAgent.AGENT_TYPE,
			MitrosHarvesterAgent.AGENT_TYPE,
			PlayerRsHarvesterAgent.AGENT_TYPE,
			SkyMusicHarvesterAgent.AGENT_TYPE,
			MasterSearchAgent.AGENT_TYPE,
			SlaveSearchAgent.AGENT_TYPE
			
	);
	
    public AgentManagerBean() {}
    
    @PostConstruct
    private void init() {
    	runningAgents = new HashMap<AID, Agent>();
    }
    
    @Override
	public AID startAgent(AgentType type, String name) {
		Agent agent = (Agent) JNDILookup.lookUpAgent(type);
		final AID aid = new AID(name, connectionManager.getCurrentNode(), type);
		agent.init(aid);
		runningAgents.put(aid, agent);
		
		// inform local clients
		wsAgents.send(JsonMarshaller.toJson(getRunningAgents()));
		
		// inform rest of the nodes
		for (String nodeAlias: connectionManager.getAllNodeAliases()) {
			new AgentResteasyClientProxy(nodeAlias)
			.performAction(rest -> rest.updateRunningAgents(getRunningAgents()));
		}
		return aid;
	}

	@Override
	public void stopAgent(AID aid) {
		runningAgents.remove(aid);
		wsAgents.send(JsonMarshaller.toJson(getRunningAgents()));
	}

	@Override
	public List<AID> getRunningAgents() {
		return Collections.unmodifiableList(new ArrayList<>(runningAgents.keySet()));
	}
	
	@Override
	public List<Agent> getRunningAgentsFull() {
		return Collections.unmodifiableList(new ArrayList<>(runningAgents.values()));
	}

	@Override
	public Agent getRunningAgent(AID aid) {
		return runningAgents.get(aid);
	}

	@Override
	public List<AgentType> getAgentTypes() {
		return AGENT_TYPES;
	}

	@Override
	public void updateAgentTypes(List<AgentType> newTypes) {
		AGENT_TYPES.removeIf(existing -> !newTypes.contains(existing));
		for(AgentType newType: newTypes) {
			if (!AGENT_TYPES.contains(newType)) AGENT_TYPES.add(newType);
		}
		
		wsTypes.send(JsonMarshaller.toJson(AGENT_TYPES));
	}

	@Override
	public void updateRunningAgents(List<AID> aids) {
		Host currentNode = connectionManager.getCurrentNode();
		List<AID> existingForeignAIDs = getRunningAgents().stream()
				.filter(aid -> !aid.getHost().equals(currentNode))
				.collect(Collectors.toList());
		
		List<AID> newForeignAIDs = aids.stream()
				.filter(aid -> !aid.getHost().equals(currentNode))
				.collect(Collectors.toList());
		
		for(AID existingForeignAID: existingForeignAIDs) {
			if (!newForeignAIDs.contains(existingForeignAID)) {
				runningAgents.remove(existingForeignAID);
			}
		}
		
		for(AID newForeignAID: newForeignAIDs) {
			if (!existingForeignAIDs.contains(newForeignAID))
				runningAgents.put(newForeignAID, null);
		}
		
		wsAgents.send(JsonMarshaller.toJson(getRunningAgents()));
	}

	@Override
	public void removeAllAgentInfoFromNode(Host node) {
		runningAgents.keySet().removeIf(k -> k.getHost().equals(node));
		wsAgents.send(JsonMarshaller.toJson(getRunningAgents()));
	}

	@Override
	public void removeAgent(String name) {
		runningAgents.keySet().removeIf(aid -> aid.getName().equals(name));
		wsAgents.send(JsonMarshaller.toJson(getRunningAgents()));
	}
}
