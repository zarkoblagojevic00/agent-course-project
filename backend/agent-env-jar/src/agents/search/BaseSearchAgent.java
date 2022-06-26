package agents.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.DiscreetAgent;
import agents.MasterHarvestAgent;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.Performative;
import model.harvester.Hit;
import model.harvester.SearchDTO;
import util.JsonMarshaller;

public abstract class BaseSearchAgent extends DiscreetAgent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8915953523490867140L;
	
	private final Integer numberOfHarvestersToWaitResults;
	private Integer numberOfHarvestersThatReturnedResults;
	private List<Hit> currentHarvestResults;
	protected SearchDTO currentSearchDTO;
	
	@EJB
	protected AgentManagerRemote agentManager;
	
	@EJB
	protected ConnectionManagerRemote connectionManager;
	
	@EJB
	protected MessageManagerRemote messageManager;
	
	
	public BaseSearchAgent(int numberOfHarvestersToWaitResults) {
		super();
		this.numberOfHarvestersToWaitResults = numberOfHarvestersToWaitResults;
		numberOfHarvestersThatReturnedResults = 0;
		currentHarvestResults = new ArrayList<>();
	}

	@PostConstruct
	public void init() {
		System.out.println("Created Search agent.");
	}

	@Override
	protected void handleMessageDiscreetly(ACLMessage message) {
		switch (message.getPerformative()) {
		case GET_SEARCHED:
			currentSearchDTO = JsonMarshaller.fromJson(message.getContent(), SearchDTO.class);
			searchForHits(message);
			break;
		case GET_HARVESTED:
			receiveHarvestedHits(message);
			break;
		default:
			System.out.println("Search agent doesn't know how to answer performative: " + message.getPerformative());
		}
	}

	protected abstract void searchForHits(ACLMessage message);

	private void receiveHarvestedHits(ACLMessage message) {
		List<Hit> harvestedHits = Arrays.asList(JsonMarshaller.fromJson(message.getContent(), Hit[].class));
		currentHarvestResults.addAll(harvestedHits);
		
		synchronized (numberOfHarvestersThatReturnedResults) {
			numberOfHarvestersThatReturnedResults++;
		}
		
		if (numberOfHarvestersThatReturnedResults.equals(numberOfHarvestersToWaitResults)) {
			List<Hit> searchedHits = searchHarvestResults();
			notifyMaster(searchedHits);
			clearState();
		}
	}
	
	private List<Hit> searchHarvestResults() {
		return currentHarvestResults.stream()
				.filter(h -> h.getInstrumentType() == currentSearchDTO.getType())
				.filter(h -> h.getItem().toLowerCase().contains(currentSearchDTO.getKeyword().toLowerCase()))
				.filter(h -> currentSearchDTO.getFromPrice() <= h.getPrice() && h.getPrice() <= currentSearchDTO.getToPrice())
				.collect(Collectors.toList());
	}
	
	private void notifyMaster(List<Hit> searchedHits) {
		AID sender = getAID();
		Set<AID> recipients = agentManager.getRunningAgents().stream()
				.filter(aid -> aid.getType().getName().equals(MasterHarvestAgent.class.getSimpleName()))
				.filter(aid -> aid.getHost().equals(connectionManager.getCurrentNode()))
				.collect(Collectors.toSet());
		
		ACLMessage message = new ACLMessage(Performative.GET_SEARCHED, sender, recipients, JsonMarshaller.toBson(searchedHits));
		System.out.println(JsonMarshaller.toJsonPP(message));
		
		messageManager.post(message);
	}

	private void clearState() {
		numberOfHarvestersThatReturnedResults = 0;
		currentSearchDTO = null;
		currentHarvestResults.clear();
	}

	

}
