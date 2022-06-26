package agents.search;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import agents.AID;
import agents.Agent;
import agents.AgentType;
import agents.harvester.PlayerRsHarvesterAgent;
import messagemanager.ACLMessage;
import messagemanager.Performative;
import rest.restclient.proxies.MessageResteasyClientProxy;

@Singleton
@Remote(Agent.class)
public class SlaveSearchAgent extends BaseSearchAgent implements Serializable {

	public SlaveSearchAgent() {
		super(1);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8944974427893611345L;
	private static final List<String> REMOTE_AGENTS_TO_HARVEST = Arrays.asList(PlayerRsHarvesterAgent.class.getSimpleName());
	
	public static final AgentType AGENT_TYPE = new AgentType(SlaveSearchAgent.class.getSimpleName(), false);

	@Override
	protected void searchForHits(ACLMessage message) {
		AID sender = getAID();
		List<AID> recipients = getRecipientsForRemoteHarvest();
		boolean isLocal = false;
		if (recipients.isEmpty()) {
			isLocal = true;
			System.out.println("No other nodes to perform Slave search. Slave looking locally...");
			recipients = getRecipientsForLocalHarvest();
		}
		
		ACLMessage harvestMessage = new ACLMessage(Performative.GET_HARVESTED, sender, new HashSet<>(recipients), currentSearchDTO.getType().toString());
		
		AID recipientAID = recipients.get(0);
		
		String recipient = recipientAID.getHost().getMasterAlias() == null 
				? connectionManager.getCurrentNode().getMasterAlias()
				: recipientAID.getHost().getAlias();		
		
		if (isLocal) {
			messageManager.post(harvestMessage);
		} else {
			new MessageResteasyClientProxy(recipient)
			.performAction(rest -> rest.sendMessage(harvestMessage));
		}
		
	}
	
	private List<AID> getRecipientsForRemoteHarvest() {
		return agentManager.getRunningAgents().stream()
				.filter(aid -> REMOTE_AGENTS_TO_HARVEST.contains(aid.getType().getName()))
				.filter(aid -> !aid.getHost().equals(connectionManager.getCurrentNode()))
				.collect(Collectors.toList());
	}
	
	private List<AID> getRecipientsForLocalHarvest() {
		return agentManager.getRunningAgents().stream()
				.filter(aid -> REMOTE_AGENTS_TO_HARVEST.contains(aid.getType().getName()))
				.filter(aid -> aid.getHost().equals(connectionManager.getCurrentNode()))
				.collect(Collectors.toList());
	}

}
