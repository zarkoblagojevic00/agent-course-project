package agents;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import chatmanager.ChatManager;
import connectionmanager.ConnectionManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import sessionmanager.SessionManagerRemote;
import ws.WebSocket;

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
	private WebSocket ws;

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
		
//		switch (message.getPerformative()) {
//		case RECEIVE_MESSAGE:
//			receiveMessage(message);
//			break;
//		case SEND_MESSAGE_USER:
//			sendMessageToUser(message);
//			break;
//		case SEND_MESSAGE_ALL:
//			sendMessageToAll(message);
//			break;
//		case GET_ALL_MESSAGES:
//			getAllMessages();
//			break;
//			
//		case GET_ALL_CHAT_MESSAGES:
//			getAllChatMessages();
//			break;
//			
//		case GET_USER_CHAT_MESSAGES:
//			getUserChatMessages(message);
//			break;
//			
//		case OTHER_USER_LOGIN:
//		case OTHER_USER_LOGOUT:
//		case OTHER_USER_REGISTER:
//			informClientOfUserActivity(message);
//			break;
//			
//		default:
//			ws.onMessage(getAID(), "Invalid option.");
//			break;
//		}
	}

	
//	private void receiveMessage(AgentMessage message) {
//		Message receivedMessage = (Message) message.getArgument("payload");
//		ResponseMessageDTO dto = chatManager.receiveMessage(receivedMessage);
//		echoMessagesToWebsocket(Arrays.asList(dto));
//	}
//	
//	private void sendMessageToUser(AgentMessage message) {
//		NewMessageDTO dto = (NewMessageDTO) message.getArgument("payload");
//		Message parsedMessage = sessionManager.unpackMessage(dto);
//		
//		Host recipientHost = parsedMessage.getRecipient().getHost();
//		if (recipientHost.getAlias().equals(connectionManager.getCurrentNode().getAlias())) {
//			sendMessage(parsedMessage, Arrays.asList(parsedMessage.getRecipient().getUsername()));
//		} else {
//			String recipientAlias = "";
//			if (recipientHost.getMasterAlias() == null) {
//				recipientAlias = connectionManager.getCurrentNode().getMasterAlias();
//			} else {
//				recipientAlias = recipientHost.getAlias();
//			}
//			new MessageResteasyClientProxy(recipientAlias)
//			.performAction(rest -> rest.messageUserFromOtherNode(dto));
//			
//			ResponseMessageDTO wsMessage = chatManager.sendMessage(parsedMessage);
//			echoMessagesToWebsocket(Arrays.asList(wsMessage));
//		}
//		
//	}
//
//	private void sendMessageToAll(AgentMessage message) {
//		NewMessageDTO dto = (NewMessageDTO) message.getArgument("payload");
//		Message parsedMessage = sessionManager.unpackMessage(dto);
//		sendMessage(parsedMessage, sessionManager.getOtherLocalRecipients(getAID()));
//		
//		for (String recipientAlias: connectionManager.getAllNodeAliases()) {
//			new MessageResteasyClientProxy(recipientAlias)
//			.performAction(rest -> rest.messageAllFromOtherNode(dto));
//		}
//		
//	}
//	
//	private void sendMessage(Message parsedMessage, List<String> forwardTo) {
//		AgentMessage forwardMessage = new AgentMessage(getAID(), AgentMessage.Type.RECEIVE_MESSAGE, forwardTo);
//		forwardMessage.addArgument("payload", parsedMessage);
//		messageManager.post(forwardMessage);
//		
//		ResponseMessageDTO wsMessage = chatManager.sendMessage(parsedMessage);
//		echoMessagesToWebsocket(Arrays.asList(wsMessage));
//	}
//	
//	private void getAllMessages() {
//		echoMessagesToWebsocket(chatManager.getAllUserMessages());
//	}
//	
//	private void getAllChatMessages() {
//		echoMessagesToWebsocket(chatManager.getAllChat());
//		
//	}
//
//	private void getUserChatMessages(AgentMessage message) {
//		String username = (String) message.getArgument("chatWith");
//		echoMessagesToWebsocket(chatManager.getChatWithUser(username));
//	}
//	
//	private void informClientOfUserActivity(AgentMessage message) {
//		WebSocketResponse webSocketResponse = new WebSocketResponse(message.getType(), true, (UserWithHostDTO) message.getArgument("activeUser"));
//		ws.onMessage(getAID(), JsonMarshaller.toJson(webSocketResponse));
//	}
//
//	
//	private void echoMessagesToWebsocket(List<ResponseMessageDTO> messages) {
//		WebSocketResponse webSocketResponse = new WebSocketResponse(AgentMessage.Type.RECEIVE_MESSAGE, true, messages);
//		ws.onMessage(getAID(), JsonMarshaller.toJson(webSocketResponse));
//	}
//
//	
}
