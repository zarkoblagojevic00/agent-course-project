package agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import messagemanager.ACLMessage;
import model.harvester.Hit;
import util.JsonMarshaller;
import ws.HarvestWebSocker;

@Singleton
@Remote(Agent.class)
public class MasterHarvestAgent extends DiscreetAgent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8827828009650245198L;
	
	@EJB
	private HarvestWebSocker wsHarvest;
	
	public static final AgentType AGENT_TYPE = new AgentType(MasterHarvestAgent.class.getSimpleName(), false);
	
	private final Integer numberOfSearchersToWaitResults;
	private Integer numberOfSearchersThatReturnedResults;
	List<Hit> currentSearchResults;
	
	public MasterHarvestAgent() {
		super();
		// TODO: If there are 2 nodes this var should be 2 else should be 1
		numberOfSearchersToWaitResults = 2;
		numberOfSearchersThatReturnedResults = 0;
		currentSearchResults = new ArrayList<>();
	}

	@PostConstruct
	public void init() {
		System.out.println("Created MasterHarvester agent.");
	}

	@Override
	protected void handleMessageDiscreetly(ACLMessage message) {
		switch (message.getPerformative()) {
		case GET_SEARCHED:
			receiveSearched(message);
			break;
		default:
			System.out.println("MasterHarvest agent doesn't know how to answer performative: " + message.getPerformative());
		}

	}

	private void receiveSearched(ACLMessage message) {
		List<Hit> collectedHits = Arrays.asList(JsonMarshaller.fromJson(message.getContent(), Hit[].class));
		currentSearchResults.addAll(collectedHits);
		
		synchronized (numberOfSearchersThatReturnedResults) {
			numberOfSearchersThatReturnedResults++;
		}
		
		if (numberOfSearchersThatReturnedResults.equals(numberOfSearchersToWaitResults)) {
			wsHarvest.send(JsonMarshaller.toJson(currentSearchResults));
			clearState();
		}
	}

	private void clearState() {
		numberOfSearchersThatReturnedResults = 0;
		currentSearchResults.clear();
	}

}
