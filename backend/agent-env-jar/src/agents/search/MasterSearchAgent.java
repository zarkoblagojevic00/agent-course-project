package agents.search;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import agents.AID;
import agents.Agent;
import agents.AgentType;
import agents.harvester.MitrosHarvesterAgent;
import agents.harvester.SkyMusicHarvesterAgent;
import messagemanager.ACLMessage;
import messagemanager.Performative;

@Singleton
@Remote(Agent.class)
public class MasterSearchAgent extends BaseSearchAgent implements Serializable {

		/**
	 * 
	 */
	private static final long serialVersionUID = -4335048290437921263L;
	
	public static final AgentType AGENT_TYPE = new AgentType(MasterSearchAgent.class.getSimpleName(), false);
	
	private static final List<String> LOCAL_AGENTS_TO_HARVEST = Arrays.asList(MitrosHarvesterAgent.class.getSimpleName(), SkyMusicHarvesterAgent.class.getSimpleName());
	
	public MasterSearchAgent() {
		super(2);
	}

	@Override
	protected void searchForHits(ACLMessage message) {
		AID sender = getAID();
		Set<AID>recipients = getRecipientsForLocalHarvest();
		ACLMessage harvestMessage = new ACLMessage(Performative.GET_HARVESTED, sender, recipients, currentSearchDTO.getType().toString());
		messageManager.post(harvestMessage);
	}

	private Set<AID> getRecipientsForLocalHarvest() {
		return agentManager.getRunningAgents().stream()
				.filter(aid -> LOCAL_AGENTS_TO_HARVEST.contains(aid.getType().getName()))
				.filter(aid -> aid.getHost().equals(connectionManager.getCurrentNode()))
				.collect(Collectors.toSet());
	}
}
