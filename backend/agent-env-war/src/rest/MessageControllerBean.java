package rest;

import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import rest.dtos.NewMessageDTO;
import sessionmanager.SessionManagerRemote;

@Stateless
@Path("/messages")
public class MessageControllerBean implements MessageController {

	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private SessionManagerRemote sessionManager;
	
	@Override
	public void messageAll(NewMessageDTO message) {
		AgentMessage agentMessage = new AgentMessage("rest_external", AgentMessage.Type.SEND_MESSAGE_ALL, Arrays.asList(message.getSender()));
		agentMessage.addArgument("payload", message);
		messageManager.post(agentMessage);
	}

	@Override
	public void messageUser(NewMessageDTO message) {
		AgentMessage agentMessage = new AgentMessage("rest_external", AgentMessage.Type.SEND_MESSAGE_USER, Arrays.asList(message.getSender()));
		agentMessage.addArgument("payload", message);
		messageManager.post(agentMessage);
	}

	@Override
	public void getUserMessages(String username) {
		AgentMessage agentMessage = new AgentMessage("rest_external", AgentMessage.Type.GET_ALL_MESSAGES, Arrays.asList(username));
		messageManager.post(agentMessage);
	}

	@Override
	public void getGlobalChat(String username) {
		AgentMessage agentMessage = new AgentMessage("rest_external", AgentMessage.Type.GET_ALL_CHAT_MESSAGES, Arrays.asList(username));
		messageManager.post(agentMessage);
	}

	@Override
	public void getChatWithOtherUser(String username, String recipient) {
		AgentMessage agentMessage = new AgentMessage("rest_external", AgentMessage.Type.GET_USER_CHAT_MESSAGES, Arrays.asList(username));
		agentMessage.addArgument("chatWith", recipient);
		messageManager.post(agentMessage);
	}

	@Override
	public void messageAllFromOtherNode(NewMessageDTO message) {
		AgentMessage agentMessage = new AgentMessage(message.getSender(), AgentMessage.Type.RECEIVE_MESSAGE, sessionManager.getLocalRecipients());
		agentMessage.addArgument("payload", sessionManager.unpackMessage(message));
		messageManager.post(agentMessage);
	}

	@Override
	public void messageUserFromOtherNode(NewMessageDTO message) {
		AgentMessage agentMessage = new AgentMessage(message.getSender(), AgentMessage.Type.RECEIVE_MESSAGE, Arrays.asList(message.getRecipient()));
		agentMessage.addArgument("payload", sessionManager.unpackMessage(message));
		messageManager.post(agentMessage);
	}

}
