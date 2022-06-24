package sessionmanager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.ChatMasterAgent;
import agents.UserAgent;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.Performative;
import model.Host;
import model.Message;
import model.User;
import model.UserWithHostDTO;
import rest.dtos.NewMessageDTO;
import rest.restclient.proxies.MessageResteasyClientProxy;
import util.JsonMarshaller;


@Singleton
@LocalBean
@Remote(SessionManagerRemote.class)
public class SessionManagerBean implements SessionManagerRemote {

	private Map<String, User> registered = new HashMap<>();
	private Map<String, User> loggedIn = new HashMap<>();
	
	@EJB
	private AgentManagerRemote agentManager;
	
	@EJB
	private ConnectionManagerRemote connectionManager;
	
	
	/**
	 * Default constructor.
	 */
	public SessionManagerBean() {
	}

	@Override
	public boolean register(User user) {
		if (!isUsernameUnique(user.getUsername())) {
			return false;
		}
		
		registered.put(user.getUsername(), user);
		
		// inform other nodes of register
		for (Host node: connectionManager.getAllNodes()) {
			new MessageResteasyClientProxy(node.getAlias())
			.performAction(rest -> {
				rest.sendMessage(getMessageForOtherChatMaster(node, Performative.REGISTER_FROM_OTHER_NODE, user));
			});
		}
		return true;
	}

	

	@Override
	public AID login(User user) {
		String username = user.getUsername();
		
		if(!isUserRegistered(user) || isUserLoggedIn(user)) {
			return null;
		}
		
		Host currentNode = connectionManager.getCurrentNode();
		user.setHost(currentNode);
		loggedIn.put(username, user);
		AID startedAID = agentManager.startAgent(UserAgent.AGENT_TYPE, username);
		
		// inform other nodes of login
		for (Host node: connectionManager.getAllNodes()) {
			new MessageResteasyClientProxy(node.getAlias())
			.performAction(rest -> {
				rest.sendMessage(getMessageForOtherChatMaster(node, Performative.LOG_IN_FROM_OTHER_NODE, user));
			});
		}
		
		return startedAID;
	}

	private boolean isUserLoggedIn(User user) {
		return loggedIn.get(user.getUsername()) != null;
	}

	@Override
	public List<UserWithHostDTO> getLoggedInUsers() {
		return Collections.unmodifiableList(mapUsersToUserWithHost(loggedIn));
	}
	
	@Override
	public List<UserWithHostDTO> getRegisteredUsers() {
		return Collections.unmodifiableList(mapUsersToUserWithHost(registered));
	}
	
	@Override
	public boolean logout(String username) {
		User existing = loggedIn.get(username);
		if (existing == null) return false;
		
		loggedIn.remove(username);
		agentManager.stopAgent(new AID(username, connectionManager.getCurrentNode(), UserAgent.AGENT_TYPE));
		
		// inform other nodes of logout
		for (Host node: connectionManager.getAllNodes()) {
			new MessageResteasyClientProxy(node.getAlias())
			.performAction(rest -> {
				rest.sendMessage(getMessageForOtherChatMaster(node, Performative.LOG_OUT_FROM_OTHER_NODE, username));
			});
		}
		return true;
	}
	
	@Override
	public List<AID> getLocalRecipients() {
		return agentManager.getRunningAgents().stream()
				.filter(aid -> aid.getType().getName().equals(UserAgent.class.getSimpleName()))
				.filter(aid -> aid.getHost().equals(connectionManager.getCurrentNode()))
				.collect(Collectors.toList());
	}
	
	@Override
	public User getLoggedInUser(String username) {
		return loggedIn.get(username);
	}
	
	@Override
	public Message unpackMessage(NewMessageDTO dto) {
		User sender = parseUser(dto.getSender());
		User recipient = parseUser(dto.getRecipient());
		return new Message(sender, recipient, LocalDateTime.now(), dto.getSubject(), dto.getContent());
	}

	private User parseUser(String username) {
		return username.equals("all") ? new User("all", "", connectionManager.getCurrentNode()) : getLoggedInUser(username);
	}

	@Override
	public List<AID> getOtherLocalRecipients(String username) {
		return getLocalRecipients().stream().filter(aid -> !username.equals(aid.getName())).collect(Collectors.toList());
	}

	
	private boolean isUsernameUnique(String username) {
		return !registered.containsKey(username);
	}
	
	private boolean isUserRegistered(User user) {
		User existing = registered.get(user.getUsername());
		if (existing == null) return false;
		
		return existing.getPassword().equals(user.getPassword());
	}
	
	private List<UserWithHostDTO> mapUsersToUserWithHost(Map<String, User> users) {
		return users.values().stream().map(this::mapUserToUserWithHost).collect(Collectors.toList());
	}

	private UserWithHostDTO mapUserToUserWithHost(User user) {
		// only registered users don't have HOST 
		User loggedInUser = loggedIn.get(user.getUsername());
		String hostAlias = "";
		if (loggedInUser != null) {
			hostAlias = loggedInUser.getHost().getAlias();
		}
		return new UserWithHostDTO(user.getUsername(), hostAlias);
	}
	
	@Override
	public UserWithHostDTO getUserWithHost(String username) {
		return mapUserToUserWithHost(registered.get(username));
	}

	@Override
	public void receiveLoggedInUsersFromMasterNode(Collection<User> users) {
		for (User user: users) {
			loggedIn.put(user.getUsername(), user);
		}
	}

	@Override
	public void receiveRegisteredUsersFromMasterNode(Collection<User> users) {
		for (User user: users) {
			registered.put(user.getUsername(), user);
		}
	}

	@Override
	public ArrayList<User> getFullLoggedInUsers() {
		return new ArrayList<User>(loggedIn.values());
	}
	
	@Override
	public ArrayList<User> getFullRegisteredUsers() {
		return new ArrayList<User>(registered.values());
	}

	@Override
	public boolean addRegisteredFromOtherNode(User user) {
		registered.put(user.getUsername(), user);
		return true;
	}

	@Override
	public boolean addLoggedInFromOtherNode(User user) {
		loggedIn.put(user.getUsername(), user);
		return true;
	}

	@Override
	public boolean logoutFromOtherNode(String username) {
		loggedIn.remove(username);
		agentManager.removeAgent(username);
		return true;
	}

	@Override
	public void logOutAllUsersFromNode(String nodeAlias) {
		loggedIn.values().removeIf(u -> u.getHost().getAlias().equals(nodeAlias));
	}
	
	@Override
	public ACLMessage getMessageForOtherChatMaster(Host node, Performative performative, Object content) {
		AID sender = agentManager.getRunningAgents().stream()
				.filter(a -> a.getHost().equals(connectionManager.getCurrentNode()))
				.filter(a -> a.getType().getName().equals(ChatMasterAgent.class.getSimpleName()))
				.findFirst().get();
		
		
		Set<AID> recipients = agentManager.getRunningAgents().stream()
				.filter(a -> !a.getHost().equals(connectionManager.getCurrentNode()))
				.filter(a -> a.getType().getName().equals(ChatMasterAgent.class.getSimpleName()))
				.collect(Collectors.toSet());
		String jsonContent = JsonMarshaller.toBson(content);
		return new ACLMessage(performative, sender, recipients, jsonContent);
	}

	@Override
	public List<AID> getRecipientsForNode(String recipientAlias) {
		return agentManager.getRunningAgents().stream()
			.filter(a -> a.getType().getName().equals(UserAgent.class.getSimpleName()))
			.filter(a -> !a.getHost().equals(connectionManager.getCurrentNode()))
			.collect(Collectors.toList());
	}

	@Override
	public List<AID> getRecipientForMessage(String recipient) {
		return agentManager.getRunningAgents().stream()
				.filter(aid -> aid.getName().equals(recipient))
				.collect(Collectors.toList());
	}
}
