package agents;

import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import chatmanager.ChatManager;
import chatmanager.ResponseMessageDTO;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.Performative;
import model.Host;
import model.Message;
import rest.dtos.NewMessageDTO;
import rest.restclient.proxies.MessageResteasyClientProxy;
import sessionmanager.SessionManagerRemote;
import util.JsonMarshaller;
import ws.LoggerWebSocket;

@Stateful
@Remote(Agent.class)
public class UserAgent extends DiscreetAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static AgentType AGENT_TYPE = new AgentType(UserAgent.class.getSimpleName(), true);
	
	private ChatManager chatManager;
	
	
	@EJB
	private SessionManagerRemote sessionManager;
	
	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private ConnectionManagerRemote connectionManager;
	
	@EJB
	private LoggerWebSocket wsLog;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created User Agent");
		chatManager = new ChatManager();
	}
	
	@PreDestroy
	public void preDestroy() {
		System.out.println("Stopping User Agent");
	}

	@Override
	protected void handleMessageDiscreetly(ACLMessage message) {
		
		switch (message.getPerformative()) {
		case RECEIVE_MESSAGE:
			receiveMessage(message);
			break;
		case SEND_MESSAGE_USER:
			sendMessageToUser(message);
			break;
		case SEND_MESSAGE_ALL:
			sendMessageToAll(message);
			break;
		case GET_ALL_MESSAGES:
			getAllMessages();
			break;
			
		case GET_ALL_CHAT_MESSAGES:
			getAllChatMessages();
			break;
			
		case GET_USER_CHAT_MESSAGES:
			getUserChatMessages(message);
			break;
		default:
			wsLog.send(String.format("User Agent :%s received invalid performative.", getAID().getName()));
			break;
		}
	}

	
	private void receiveMessage(ACLMessage message) {
		Message receivedMessage = JsonMarshaller.fromJson(message.getContent(), Message.class);
		ResponseMessageDTO dto = chatManager.receiveMessage(receivedMessage);
		String response = String.format(
				"%s received message from %s with content: \n%s",
				getAID().getName(),
				dto.getSender(),
				JsonMarshaller.toJsonPP(dto));
		wsLog.send(response);
	}
	
	private void sendMessageToUser(ACLMessage message) {
		NewMessageDTO dto =JsonMarshaller.fromJson(message.getContent(), NewMessageDTO.class);
		Message parsedMessage = sessionManager.unpackMessage(dto);
		
		Host recipientHost = parsedMessage.getRecipient().getHost();
		List<AID> recipientAID = sessionManager.getRecipientForMessage(dto.getRecipient());

		wsLog.send(String.format("%s sending message to %s", getAID().getName(), dto.getRecipient()));
		
		
		if (recipientHost.getAlias().equals(connectionManager.getCurrentNode().getAlias())) {
			messageManager.post(getMessageToSend(parsedMessage, recipientAID));
		} else {
			String recipientAlias = "";
			if (recipientHost.getMasterAlias() == null) {
				recipientAlias = connectionManager.getCurrentNode().getMasterAlias();
			} else {
				recipientAlias = recipientHost.getAlias();
			}
			new MessageResteasyClientProxy(recipientAlias)
			.performAction(rest -> rest.sendMessage(getMessageToSend(parsedMessage, recipientAID)));
		}
		
		chatManager.sendMessage(parsedMessage);
	}

	private void sendMessageToAll(ACLMessage message) {
		NewMessageDTO dto =JsonMarshaller.fromJson(message.getContent(), NewMessageDTO.class);
		Message parsedMessage = sessionManager.unpackMessage(dto);
		
		String currentUser = getAID().getName();
		wsLog.send(String.format("%s sending message to all other users...", currentUser));
		messageManager.post(getMessageToSend(parsedMessage, sessionManager.getOtherLocalRecipients(currentUser)));
		
		for (String recipientAlias: connectionManager.getAllNodeAliases()) {
			new MessageResteasyClientProxy(recipientAlias)
			.performAction(rest -> 
				rest.sendMessage(getMessageToSend(parsedMessage, sessionManager.getRecipientsForNode(recipientAlias))));
		}
		
		chatManager.sendMessage(parsedMessage);
	}
	
	private ACLMessage getMessageToSend(Message parsedMessage, List<AID> forwardTo) {
		return new ACLMessage(
				Performative.RECEIVE_MESSAGE, 
				getAID(), 
				new HashSet<>(forwardTo), 
				JsonMarshaller.toJson(parsedMessage));
	}
	
	private void getAllMessages() {
		String response = String.format(
				"All messages :\n%s",
				JsonMarshaller.toJsonPP(chatManager.getAllUserMessages()));
		wsLog.send(response);
	}
	
	private void getAllChatMessages() {
		String response = String.format(
				"%s chat with all :\n%s",
				getAID().getName(),
				JsonMarshaller.toJsonPP(chatManager.getAllChat()));
		wsLog.send(response);
	}

	private void getUserChatMessages(ACLMessage message) {
		String username = message.getContent();
		String response = String.format(
				"%s chat with %s :\n%s",
				getAID().getName(),
				username,
				JsonMarshaller.toJsonPP(chatManager.getChatWithUser(username)));
		wsLog.send(response);
	}
}
