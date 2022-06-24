package rest;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.Path;

import agentmanager.AgentManagerRemote;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.Performative;
import model.Host;
import rest.restclient.proxies.AgentResteasyClientProxy;
import rest.restclient.proxies.ConnectionResteasyClientProxy;
import rest.restclient.proxies.MessageResteasyClientProxy;
import sessionmanager.SessionManagerRemote;
import ws.WebSocket;

@Singleton
@Startup
@Remote(ConnectionController.class)
@Path("/connections")
public class ConnectionControllerBean implements ConnectionController {
	@EJB
	private ConnectionManagerRemote connectionManager;
	
	@EJB 
	private SessionManagerRemote sessionManager;
	
	@EJB 
	private AgentManagerRemote agentManager;
	
	@EJB
	private WebSocket webSocket;
	
	@PostConstruct
	private void init() {
		if (connectionManager.getCurrentNode().getMasterAlias() != null)
			initiateHandshake();
	}
	
	@Override
	public void registerNode(Host node) {
		System.out.println(String.format("Registering new node: %s", node.getAlias()));
		connectionManager.addNode(node);
		fetchAgentClassesFromNewNode(node);
		fetchAgentsFromNewNode(node);
		informExistingNodesThatNewNodeIsRegistered(node);
		informNewNodeOfExistingNodes(node);
		informNewNodeOfExistingAgentClasses(node);
		informNewNodeOfExistingRunningAgents(node);
		informNewNodeOfLoggedInUsers(node);
		informNewNodeOfRegisteredUsers(node);
	}

	

	

	@Override
	public void addNode(Host node) {
		connectionManager.addNode(node);
	}

	@Override
	public void receiveNodesFromMaster(ArrayList<Host> nodes) {
		System.out.println("--------------------------- Receiving nodes ---------------------------");
		Host master = new Host("", connectionManager.getCurrentNode().getMasterAlias(),"");
		connectionManager.addNode(master);
		
		for (Host node: nodes) {
			System.out.println(String.format("NODE: %s %s", node.getAlias(), node.getAlias().equals(connectionManager.getCurrentNode().getAlias()) ? "(self)" : ""));
			connectionManager.addNode(node);
		}
	}
	
	@Override
	public void removeNode(Host node) {
		System.out.println("--------------------------- Removing node: " + node.getAlias() + "---------------------------");
		String nodeAlias = node.getMasterAlias() == null ? connectionManager.getCurrentNode().getMasterAlias() : node.getAlias();
		connectionManager.removeNode(nodeAlias);
		sessionManager.logOutAllUsersFromNode(node.getAlias());
		agentManager.removeAllAgentInfoFromNode(node);
	}
	
	@Override
	public boolean ping() {
		System.out.println("--------------------------- PINGED ---------------------------");
		return true;
	}
	
	// done by new node
	private void initiateHandshake() {
		new ConnectionResteasyClientProxy(connectionManager.getCurrentNode().getMasterAlias())
			.performAction((rest) -> rest.registerNode(connectionManager.getCurrentNode()));
	}
	
	// done by master node
	private void fetchAgentClassesFromNewNode(Host node) {
		new AgentResteasyClientProxy(node.getAlias())
		.performAction(rest -> agentManager.updateAgentTypes(rest.getAllAgentTypes()));
	}
	
	// done by master node
	private void fetchAgentsFromNewNode(Host node) {
		new AgentResteasyClientProxy(node.getAlias())
		.performAction(rest -> agentManager.updateRunningAgents(rest.getAllRunningAgents()));
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// done by master node
	private void informExistingNodesThatNewNodeIsRegistered(Host newNode) {
		for (String existingNodeAlias: connectionManager.getAllNodeAliases()) {
			if (existingNodeAlias.equals(newNode.getAlias())) continue;
			
			new ConnectionResteasyClientProxy(existingNodeAlias)
				.performAction(rest -> rest.addNode(newNode));
		}
	}
	
	// done by master node
	private void informNewNodeOfExistingNodes(Host node) {
		ArrayList<Host> nodes = new ArrayList<Host>(connectionManager.getAllNodes());
		new ConnectionResteasyClientProxy(node.getAlias())
			.performAction(rest -> rest.receiveNodesFromMaster(nodes));
	}
	
	// done by master node
	private void informNewNodeOfExistingAgentClasses(Host node) {
		new AgentResteasyClientProxy(node.getAlias())
		.performAction(rest -> rest.updateAgentTypes(agentManager.getAgentTypes()));
	}

	// done by master node
	private void informNewNodeOfExistingRunningAgents(Host node) {
		new AgentResteasyClientProxy(node.getAlias())
		.performAction(rest -> rest.updateRunningAgents(agentManager.getRunningAgents()));
	}

	// done by master node
	private void informNewNodeOfLoggedInUsers(Host node) {
		new MessageResteasyClientProxy(node.getAlias())
		.performAction(rest -> rest.sendMessage(
				sessionManager.getMessageForOtherChatMaster(
						node, 
						Performative.RECEIVE_LOGGED_IN_FROM_MASTER_NODE, 
						sessionManager.getFullLoggedInUsers())));
	}

	// done by master node
	private void informNewNodeOfRegisteredUsers(Host node) {
		new MessageResteasyClientProxy(node.getAlias())
		.performAction(rest -> rest.sendMessage(
				sessionManager.getMessageForOtherChatMaster(
						node, 
						Performative.RECEIVE_REGISTERED_FROM_MASTER_NODE, 
						sessionManager.getFullRegisteredUsers())));
	}
	
	@Schedule(hour = "*", minute = "*", second = "*/30", persistent = false)
	private void heartbeatProtocol() {
		System.out.println("--------------------------- Initiating Heartbeat ---------------------------");
		for(String nodeAlias : connectionManager.getAllNodeAliases()) {
			
			System.out.println("--------------------------- Pinging: " + nodeAlias + " ---------------------------" );
			new ConnectionResteasyClientProxy(nodeAlias)
			.performAction(rest -> {
				int tries = 1;
				while (tries <= 2) {
					try {
						boolean success = rest.ping();
						if (success) {
							System.out.println("--------------------------- Node: " + nodeAlias + " responded on try: " + tries +" ---------------------------" );
							return;
						}
					} catch (Exception e) {
						System.out.println("--------------------------- No response from: " + nodeAlias + " on try: " + tries +" ---------------------------" );
					} finally {
						tries++;						
					}
				}
				
				System.out.println("--------------------------- Node: " + nodeAlias + " is dead. ---------------------------");
				
				Host node = connectionManager.getNode(nodeAlias);
				removeNode(node);
				informOtherNodesOfShutDown(node);
				
			});
			
		}
	}

	@PreDestroy
	private void shutDown() {
		informOtherNodesOfShutDown(connectionManager.getCurrentNode());
	}	
	
	private void informOtherNodesOfShutDown(Host node) {
		for (String nodeAlias: connectionManager.getAllNodeAliases()) {
			new ConnectionResteasyClientProxy(nodeAlias)
			.performAction(rest -> rest.removeNode(node));
		}
	}
	
}