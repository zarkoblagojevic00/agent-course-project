package messagemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class AgentMessage implements Serializable {
	/**
	 * 
	 */
	public enum Type {
		// master agent
		REGISTER,
		LOG_IN,
		LOG_OUT,
		GET_REGISTERED,
		GET_LOGGED_IN,
		
		// user agents
		SEND_MESSAGE_ALL,
		SEND_MESSAGE_USER,
		RECEIVE_MESSAGE,
		GET_ALL_MESSAGES, 
		GET_ALL_CHAT_MESSAGES, 
		GET_USER_CHAT_MESSAGES,
		
		OTHER_USER_LOGIN,
		OTHER_USER_REGISTER,
		OTHER_USER_LOGOUT, 
		
		RECEIVE_LOGGED_IN_FROM_MASTER_NODE, 
		RECEIVE_REGISTERED_FROM_MASTER_NODE,
		
		REGISTER_FROM_OTHER_NODE,
		LOG_IN_FROM_OTHER_NODE,
		LOG_OUT_FROM_OTHER_NODE,
	}
	
	private static final long serialVersionUID = 4750922547689000321L;
	private String sender;
	private List<String> recipients;
 	private Map<String, Serializable> additionalArgs;
 	private AgentMessage.Type type;
	
	public AgentMessage() {
		additionalArgs = new HashMap<>();
		recipients = new ArrayList<>();
	}

	public AgentMessage(String sender, Type type) {
		super();
		this.sender = sender;
		this.recipients = new ArrayList<>();
		this.additionalArgs = new HashMap<>();
		this.type = type;
	}
	
	public AgentMessage(String sender, Type type, List<String> recepients) {
		super();
		this.sender = sender;
		this.recipients = recepients;
		this.additionalArgs = new HashMap<>();
		this.type = type;
	}
	
	public String getSender() {
		return sender;
	}
	
	public List<String> getRecipients() {
		return Collections.unmodifiableList(recipients);
	}

	public AgentMessage.Type getType() {
		return type;
	}
	
	public Serializable getArgument(String key) {
		return additionalArgs.get(key);
	}
	
	public void addArgument(String key, Serializable value) {
		additionalArgs.put(key, value);
	}
	
	public Message toMessage(Session session) {
		Message message = null ;
		try {
			message = session.createObjectMessage(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
}
