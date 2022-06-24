package agents;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import model.User;
import model.UserWithHostDTO;
import sessionmanager.SessionManagerRemote;
import util.JsonMarshaller;
import ws.LoggerWebSocket;

@Singleton
@Remote(Agent.class)
public class ChatMasterAgent extends DiscreetAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static AgentType AGENT_TYPE = new AgentType(ChatMasterAgent.class.getSimpleName(), false);
	
	@EJB
	private SessionManagerRemote sessionManager;
	
	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private LoggerWebSocket wsLog;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created Chat Master Agent!");
	}

	@Override
	protected void handleMessageDiscreetly(ACLMessage message) {
		switch (message.getPerformative()) {
		case REGISTER:
			register(message);
			break;
		case LOG_IN:
			login(message);
			break;
		case GET_LOGGED_IN:
			getLoggedIn(message);
			break;
		case GET_REGISTERED:
			getRegistered(message);
			break;
		case LOG_OUT:
			logout(message);
			break;
		case RECEIVE_REGISTERED_FROM_MASTER_NODE:
			receiveRegisteredFromMasterNode(message);
			break;
		case RECEIVE_LOGGED_IN_FROM_MASTER_NODE:
			receiveLoggedInFromMasterNode(message);
			break;
		case REGISTER_FROM_OTHER_NODE:
			registerFromOtherNode(message);
			break;
		case LOG_IN_FROM_OTHER_NODE:
			loginFromOtherNode(message);
			break;
		case LOG_OUT_FROM_OTHER_NODE:
			logoutFromOtherNode(message);
			break;
		default:
			wsLog.send("Invalid option.");
			break;
		}
	}

	
	private void register(ACLMessage message) {
		User user = JsonMarshaller.fromJson(message.getContent(), User.class);
		boolean success = sessionManager.register(user);
		String response = success 
				? String.format("User %s registered succesfully.", JsonMarshaller.toJson(user))
				: String.format("User with username: %s already exists.", user.getUsername());
		
		wsLog.send(response);
	}
	
	private void login(ACLMessage message) {
		User user = JsonMarshaller.fromJson(message.getContent(), User.class);
		AID started = sessionManager.login(user);
		String response = started != null
				? String.format("User %s logged in succesfully.", JsonMarshaller.toJson(user))
				: String.format("User with username: %s already logged in or doesn't exist.", user.getUsername());
		wsLog.send(response);
	}
	
	private void getLoggedIn(ACLMessage message) {
		List<UserWithHostDTO> loggedIn = sessionManager.getLoggedInUsers();
		String response = JsonMarshaller.toJson(loggedIn);
		wsLog.send(response);
	}
	
	private void getRegistered(ACLMessage message) {
		List<UserWithHostDTO> registered = sessionManager.getRegisteredUsers();
		String response = JsonMarshaller.toJson(registered);
		wsLog.send(response);
	}
	
	private void logout(ACLMessage message) {
		String username = message.getContent();
		boolean success = sessionManager.logout(username);
		String response = success 
				? String.format("User %s logged out succesfully.", JsonMarshaller.toJson(username))
				: String.format("User with username: %s failed to log out.", username);
		
		wsLog.send(response);
	}
	
	private void receiveRegisteredFromMasterNode(ACLMessage message) { 
		List<User> registered = Arrays.asList(JsonMarshaller.fromJson(message.getContent(), User[].class));
		sessionManager.receiveRegisteredUsersFromMasterNode(registered);
		wsLog.send(String.format("Local ChatMasterAgent received registered users from MASTER NODE: \n%s", JsonMarshaller.toJsonPP(registered)));
	}

	private void receiveLoggedInFromMasterNode(ACLMessage message) {
		List<User> loggedIn = Arrays.asList(JsonMarshaller.fromJson(message.getContent(), User[].class));
		sessionManager.receiveLoggedInUsersFromMasterNode(loggedIn);
		wsLog.send(String.format("Local ChatMasterAgent received logged in users from MASTER NODE: \n%s", JsonMarshaller.toJsonPP(loggedIn)));

	}
	
	private void registerFromOtherNode(ACLMessage message) {
		User user = JsonMarshaller.fromJson(message.getContent(), User.class);
		boolean success = sessionManager.addRegisteredFromOtherNode(user);
		emitActionFromOtherNode(message, user.getUsername(), success);
	}

	private void loginFromOtherNode(ACLMessage message) {
		User user = JsonMarshaller.fromJson(message.getContent(), User.class);
		boolean success = sessionManager.addLoggedInFromOtherNode(user);
		emitActionFromOtherNode(message, user.getUsername(), success);
	}

	private void logoutFromOtherNode(ACLMessage message) {
		String username = message.getContent();
		boolean success = sessionManager.logoutFromOtherNode(username);
		emitActionFromOtherNode(message, username, success);
	}
	
	private void emitActionFromOtherNode(ACLMessage message, String username, boolean success) {
		AID sender = message.getSender();
		String response = String.format(
			"ChatMasterAgent %s to execute command %s from: %s@%s for user: %s",
			success ? "SUCCEEDED" : "FAILED",
			message.getPerformative().toString(),
			sender.getName(),
			sender.getHost().getAlias().split(":")[0],
			username);
		wsLog.send(response);
	}

}
