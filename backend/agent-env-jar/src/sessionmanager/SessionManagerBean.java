package sessionmanager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import agentmanager.AgentManagerRemote;
import connectionmanager.ConnectionManagerRemote;
import model.Message;
import model.User;
import model.UserWithHostDTO;
import rest.connection.restclient.clientproxies.UserResteasyClientProxy;
import rest.dtos.NewMessageDTO;
import sessionmanager.dtos.SessionInfoDTO;
import util.JNDILookup;


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
		for (String nodeAlias: connectionManager.getAllNodeAliases()) {
			new UserResteasyClientProxy(nodeAlias)
			.performAction(rest -> rest.registerFromOtherNode(user));
		}
		return true;
	}

	@Override
	public SessionInfoDTO login(User user) {
		String username = user.getUsername();
		
		if(!isUserRegistered(user) || isUserLoggedIn(user)) {
			return null;
		}
		
		user.setHost(connectionManager.getCurrentNode());
		loggedIn.put(username, user);
		agentManager.startAgent(username, JNDILookup.UserAgentLookup);
		
		// inform other nodes of login
		for (String nodeAlias: connectionManager.getAllNodeAliases()) {
			new UserResteasyClientProxy(nodeAlias)
			.performAction(rest -> rest.loginFromOtherNode(user));
		}
		
		return new SessionInfoDTO(username, username, user.getHost().getAlias());
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
		agentManager.stopAgent(username);
		
		// inform other nodes of logout
		for (String nodeAlias: connectionManager.getAllNodeAliases()) {
			new UserResteasyClientProxy(nodeAlias)
			.performAction(rest -> rest.logoutFromOtherNode(username));
		}
		
		return true;
	}
	
	@Override
	public List<String> getLocalRecipients() {
		return getLoggedInUsers().stream()
				.filter(uwh -> uwh.getHostAlias().equals(connectionManager.getCurrentNode().getAlias()))
				.map(UserWithHostDTO::getUsername).collect(Collectors.toList());
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
	public List<String> getOtherLocalRecipients(String username) {
		return getLocalRecipients().stream().filter(un -> !username.equals(un)).collect(Collectors.toList());
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
		return true;
	}

	@Override
	public void logOutAllUsersFromnode(String nodeAlias) {
		loggedIn.values().removeIf(u -> u.getHost().getAlias().equals(nodeAlias));
	}
	
}
