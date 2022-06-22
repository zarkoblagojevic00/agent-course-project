package agents;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import chatmanager.ChatManager;
import chatmanager.ResponseMessageDTO;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import model.Host;
import model.Message;
import model.UserWithHostDTO;
import rest.connection.restclient.clientproxies.MessageResteasyClientProxy;
import rest.dtos.NewMessageDTO;
import sessionmanager.SessionManagerRemote;
import util.JsonMarshaller;
import ws.WebSocket;
import ws.WebSocketResponse;

@Stateful
@Remote(Agent.class)
public class UserAgent extends DiscreetAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChatManager chatManager;
	
	
	@EJB
	private SessionManagerRemote sessionManager;
	
	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private ConnectionManagerRemote connectionManager;
	
	@EJB
	private WebSocket ws;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created User Agent!");
		chatManager = new ChatManager();
	}

	@Override
	protected void handleMessageDiscreetly(AgentMessage message) {
		switch (message.getType()) {
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
			
		case OTHER_USER_LOGIN:
		case OTHER_USER_LOGOUT:
		case OTHER_USER_REGISTER:
			informClientOfUserActivity(message);
			break;
			
		default:
			ws.onMessage(getAgentId(), "Invalid option.");
			break;
		}
	}

	
	private void receiveMessage(AgentMessage message) {
		Message receivedMessage = (Message) message.getArgument("payload");
		ResponseMessageDTO dto = chatManager.receiveMessage(receivedMessage);
		echoMessagesToWebsocket(Arrays.asList(dto));
	}
	
	private void sendMessageToUser(AgentMessage message) {
		NewMessageDTO dto = (NewMessageDTO) message.getArgument("payload");
		Message parsedMessage = sessionManager.unpackMessage(dto);
		
		Host recipientHost = parsedMessage.getRecipient().getHost();
		if (recipientHost.getAlias().equals(connectionManager.getCurrentNode().getAlias())) {
			sendMessage(parsedMessage, Arrays.asList(parsedMessage.getRecipient().getUsername()));
		} else {
			String recipientAlias = "";
			if (recipientHost.getMasterAlias() == null) {
				recipientAlias = connectionManager.getCurrentNode().getMasterAlias();
			} else {
				recipientAlias = recipientHost.getAlias();
			}
			new MessageResteasyClientProxy(recipientAlias)
			.performAction(rest -> rest.messageUserFromOtherNode(dto));
			
			ResponseMessageDTO wsMessage = chatManager.sendMessage(parsedMessage);
			echoMessagesToWebsocket(Arrays.asList(wsMessage));
		}
		
	}

	private void sendMessageToAll(AgentMessage message) {
		NewMessageDTO dto = (NewMessageDTO) message.getArgument("payload");
		Message parsedMessage = sessionManager.unpackMessage(dto);
		sendMessage(parsedMessage, sessionManager.getOtherLocalRecipients(getAgentId()));
		
		for (String recipientAlias: connectionManager.getAllNodeAliases()) {
			new MessageResteasyClientProxy(recipientAlias)
			.performAction(rest -> rest.messageAllFromOtherNode(dto));
		}
		
	}
	
	private void sendMessage(Message parsedMessage, List<String> forwardTo) {
		AgentMessage forwardMessage = new AgentMessage(getAgentId(), AgentMessage.Type.RECEIVE_MESSAGE, forwardTo);
		forwardMessage.addArgument("payload", parsedMessage);
		messageManager.post(forwardMessage);
		
		ResponseMessageDTO wsMessage = chatManager.sendMessage(parsedMessage);
		echoMessagesToWebsocket(Arrays.asList(wsMessage));
	}
	
	private void getAllMessages() {
		echoMessagesToWebsocket(chatManager.getAllUserMessages());
	}
	
	private void getAllChatMessages() {
		echoMessagesToWebsocket(chatManager.getAllChat());
		
	}

	private void getUserChatMessages(AgentMessage message) {
		String username = (String) message.getArgument("chatWith");
		echoMessagesToWebsocket(chatManager.getChatWithUser(username));
	}
	
	private void informClientOfUserActivity(AgentMessage message) {
		WebSocketResponse webSocketResponse = new WebSocketResponse(message.getType(), true, (UserWithHostDTO) message.getArgument("activeUser"));
		ws.onMessage(getAgentId(), JsonMarshaller.toJson(webSocketResponse));
	}

	
	private void echoMessagesToWebsocket(List<ResponseMessageDTO> messages) {
		WebSocketResponse webSocketResponse = new WebSocketResponse(AgentMessage.Type.RECEIVE_MESSAGE, true, messages);
		ws.onMessage(getAgentId(), JsonMarshaller.toJson(webSocketResponse));
	}

	
}
