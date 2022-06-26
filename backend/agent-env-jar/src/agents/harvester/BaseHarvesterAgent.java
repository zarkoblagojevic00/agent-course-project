package agents.harvester;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.UserAgent;

import agents.AID;
import agents.DiscreetAgent;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.Performative;
import model.harvester.Hit;
import model.harvester.InstrumentType;
import rest.restclient.proxies.MessageResteasyClientProxy;
import util.JsonMarshaller;

public abstract class BaseHarvesterAgent extends DiscreetAgent implements Harvester {

	/**
	 * 
	 */
	
	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private ConnectionManagerRemote connectionManager;
	
	private static final long serialVersionUID = 3200620171501448277L;
	private Map<InstrumentType, List<Hit>> hits;
	protected String rootPath;
	private Map<InstrumentType, String> paths;
	private String elementMatcher;
	
	public BaseHarvesterAgent(String rootPath, Map<InstrumentType, String> paths, String elementMatcher) {
		super();
		hits = new HashMap<>();
		this.rootPath = rootPath;
		this.paths = paths;
		this.elementMatcher = elementMatcher;
	}
	
	@PostConstruct
	public void init() {
		System.out.println("Created Harvester agent.");
		System.out.println("Starting to harvest...");
		harvest();
		System.out.println("Harvest finished");
	}
	
	@Override
	protected void handleMessageDiscreetly(ACLMessage message) {
		switch (message.getPerformative()) {
		case GET_HARVESTED:
			sendHarvestedHits(message);
			break;
		default:
			System.out.println("Harvester agent doesn't know how to answer performative: " + message.getPerformative());
		}
	}

	private void sendHarvestedHits(ACLMessage message) {
		InstrumentType type = InstrumentType.valueOf(message.getContent());
		List<Hit> instrumentHits = getHarvested(type);
		AID sender = getAID();
		AID harvestCollector = message.getSender();
		List<AID> recipients = Arrays.asList(harvestCollector);
		
		ACLMessage harvesterResponse = new ACLMessage(Performative.GET_HARVESTED, sender, new HashSet<>(recipients), JsonMarshaller.toBson(instrumentHits));
		System.out.println(String.format(
				"Harvester %s is responding to collector: %s with content: %s\n", 
				sender.getName(), 
				harvestCollector.getName(), 
				JsonMarshaller.toJsonPP(harvesterResponse.getContent())));
		
		if (sender.getHost().equals(harvestCollector.getHost())) {
			messageManager.post(harvesterResponse);
		} else {
			String recipient = harvestCollector.getHost().getMasterAlias() == null 
					? connectionManager.getCurrentNode().getMasterAlias()
					: harvestCollector.getHost().getAlias();		
			new MessageResteasyClientProxy(recipient).performAction(rest -> rest.sendMessage(harvesterResponse));
		}
	}
		

	@Override
	public List<Hit> getHarvested(InstrumentType type) {
		return Collections.unmodifiableList(hits.get(type));
	}

	private void harvest() {
		for (InstrumentType type: paths.keySet()) {
			hits.put(type, harvestInstrument(type)); 
		}
		
	}
	
	private List<Hit> harvestInstrument(InstrumentType type) {
		UserAgent agent = new UserAgent();
		try {
			agent.visit(getAppendedToRoot(paths.get(type)));
			Elements productElements = agent.doc.findEvery(elementMatcher);
			return productElements.toList().stream().map(this.getElementToHitMapper(type)).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	protected abstract Function<Element, Hit> getElementToHitMapper(InstrumentType type);
	
	private String getAppendedToRoot(String path) {
		return String.format("%s%s", rootPath, path);
	}
}
