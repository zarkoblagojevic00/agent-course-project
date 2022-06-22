package rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agents.MasterAgent;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import model.User;


@Stateless
@Path("/users")
public class UserControllerBean implements UserController {

	@EJB
	private MessageManagerRemote messageManager;
	
	@Override
	public void register(User user) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.REGISTER, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("user", user);
		messageManager.post(message);
	}

	@Override
	public void login(User user) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.LOG_IN, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("user", user);
		messageManager.post(message);
	}

	@Override
	public void getloggedInUsers(String username) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.GET_LOGGED_IN, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("username", username);
		messageManager.post(message);
	}
	
	@Override
	public void receiveLoggedInUsersFromMasterNode(List<User> users) {
		AgentMessage message = new AgentMessage("master_rest_external", AgentMessage.Type.RECEIVE_LOGGED_IN_FROM_MASTER_NODE, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("users", new ArrayList<User>(users));
		messageManager.post(message);
	}

	@Override
	public void receiveRegisteredUsersFromMasterNode(List<User> users) {
		AgentMessage message = new AgentMessage("master_rest_external", AgentMessage.Type.RECEIVE_REGISTERED_FROM_MASTER_NODE, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("users", new ArrayList<User>(users));
		messageManager.post(message);
	}

	@Override
	public void getRegisteredUsers(String username) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.GET_REGISTERED, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("username", username);
		messageManager.post(message);
	}

	@Override
	public void logout(String username) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.LOG_OUT, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("username", username);
		messageManager.post(message);
	}

	@Override
	public void registerFromOtherNode(User user) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.REGISTER_FROM_OTHER_NODE, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("user", user);
		messageManager.post(message);
	}

	@Override
	public void loginFromOtherNode(User user) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.LOG_IN_FROM_OTHER_NODE, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("user", user);
		messageManager.post(message);
	}

	@Override
	public void logoutFromOtherNode(String username) {
		AgentMessage message = new AgentMessage("rest_external", AgentMessage.Type.LOG_OUT_FROM_OTHER_NODE, Arrays.asList(MasterAgent.MASTER_AGENT_ID));
		message.addArgument("username", username);
		messageManager.post(message);
	}

	

}
