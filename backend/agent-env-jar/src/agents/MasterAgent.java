package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import model.User;
import model.UserWithHostDTO;
import sessionmanager.SessionManagerRemote;
import sessionmanager.dtos.SessionInfoDTO;
import util.JsonMarshaller;
import ws.WebSocket;
import ws.WebSocketResponse;

@Singleton
@Remote(Agent.class)
public class MasterAgent extends DiscreetAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String WEB_SOCKET_MASTER_SESSION_ID = "master";
	
	public static final String MASTER_AGENT_ID = "master";
	
	@EJB
	private SessionManagerRemote sessionManager;
	
	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private WebSocket ws;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created Master Agent!");
	}

	@Override
	protected void handleMessageDiscreetly(AgentMessage message) {
		switch (message.getType()) {
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
			ws.onMessage(WEB_SOCKET_MASTER_SESSION_ID, "Invalid option.");
			break;
		}
	}

	
	private void register(AgentMessage message) {
		User user = (User) message.getArgument("user");
		boolean success = sessionManager.register(user);
		String response = JsonMarshaller.toJson(new WebSocketResponse(message.getType(), success, null));
		ws.onMessage(WEB_SOCKET_MASTER_SESSION_ID, response);
		informOthersOfUserActivity(
				AgentMessage.Type.OTHER_USER_REGISTER, user.getUsername(), 
				sessionManager.getOtherLocalRecipients(user.getUsername())
		);
	}
	
	private void login(AgentMessage message) {
		User user = (User) message.getArgument("user");
		SessionInfoDTO dto = sessionManager.login(user);
		String response = JsonMarshaller.toJson(new WebSocketResponse(message.getType(), dto != null , dto));
		ws.onMessage(WEB_SOCKET_MASTER_SESSION_ID, response);
		informOthersOfUserActivity(
				AgentMessage.Type.OTHER_USER_LOGIN, user.getUsername(), 
				sessionManager.getOtherLocalRecipients(user.getUsername())
		);
		
	}
	
	private void getLoggedIn(AgentMessage message) {
		List<UserWithHostDTO> loggedIn = sessionManager.getLoggedInUsers();
		String response = JsonMarshaller.toJson(new WebSocketResponse(message.getType(), true, loggedIn));
		ws.onMessage((String) message.getArgument("username"), response); 
	}
	
	private void getRegistered(AgentMessage message) {
		List<UserWithHostDTO> registered = sessionManager.getRegisteredUsers();
		String response = JsonMarshaller.toJson(new WebSocketResponse(message.getType(), true, registered));
		ws.onMessage((String) message.getArgument("username"), response); 
	}
	
	private void logout(AgentMessage message) {
		String username = (String) message.getArgument("username");
		boolean success = sessionManager.logout(username);
		String response = JsonMarshaller.toJson(new WebSocketResponse(message.getType(), success, null));
		ws.onMessage(WEB_SOCKET_MASTER_SESSION_ID, response);
		informOthersOfUserActivity(AgentMessage.Type.OTHER_USER_LOGOUT, username, sessionManager.getOtherLocalRecipients(username));
	}
	
	@SuppressWarnings("unchecked")
	private void receiveRegisteredFromMasterNode(AgentMessage message) {
		sessionManager.receiveRegisteredUsersFromMasterNode((ArrayList<User>) message.getArgument("users"));
	}

	@SuppressWarnings("unchecked")
	private void receiveLoggedInFromMasterNode(AgentMessage message) {
		sessionManager.receiveLoggedInUsersFromMasterNode((ArrayList<User>) message.getArgument("users"));
	}
	
	private void registerFromOtherNode(AgentMessage message) {
		User user = (User) message.getArgument("user");
		boolean success = sessionManager.addRegisteredFromOtherNode(user);
		if (!success) return;
		informOthersOfUserActivity(AgentMessage.Type.OTHER_USER_REGISTER, user.getUsername(), sessionManager.getLocalRecipients());
	}

	private void loginFromOtherNode(AgentMessage message) {
		User user = (User) message.getArgument("user");
		boolean success = sessionManager.addLoggedInFromOtherNode(user);
		if (!success) return;
		informOthersOfUserActivity(AgentMessage.Type.OTHER_USER_LOGIN, user.getUsername(), sessionManager.getLocalRecipients());
	}

	private void logoutFromOtherNode(AgentMessage message) {
		String username = (String) message.getArgument("username");
		boolean success = sessionManager.logoutFromOtherNode(username);
		if (!success) return;
		informOthersOfUserActivity(AgentMessage.Type.OTHER_USER_LOGOUT, username, sessionManager.getLocalRecipients());
	}
	
	private void informOthersOfUserActivity(AgentMessage.Type activity, String username, List<String> others) {
		if (others.isEmpty()) return;
		AgentMessage newInformation = new AgentMessage(MASTER_AGENT_ID, activity, others);
		newInformation.addArgument("activeUser", sessionManager.getUserWithHost(username));
		messageManager.post(newInformation);
	}


	




	
}
