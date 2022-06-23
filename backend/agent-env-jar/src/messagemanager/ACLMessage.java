package messagemanager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import agents.AID;

public class ACLMessage implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8029650507896577780L;
	private Performative performative;
	private AID sender;
	private Set<AID> recipients = new HashSet<AID>();
	private AID replyTo;
	private String content;
	private Object contentObj;
	private HashMap<String, Object> userArgs = new HashMap<String, Object>();
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	private String replyWith;
	private String inReplyTo;
	private long replyBy;
	
	public ACLMessage() { }
	
	public ACLMessage(Performative performative, AID sender, Set<AID> recipients, AID replyTo, String content,
			Object contentObj, HashMap<String, Object> userArgs, String language, String encoding, String ontology,
			String protocol, String conversationId, String replyWith, String inReplyTo, long replyBy) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.recipients = recipients;
		this.replyTo = replyTo;
		this.content = content;
		this.contentObj = contentObj;
		this.userArgs = userArgs;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.replyWith = replyWith;
		this.inReplyTo = inReplyTo;
		this.replyBy = replyBy;
	}

	public ACLMessage(Performative performative, AID sender, Set<AID> recipients, String content) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.recipients = recipients;
		this.content = content;
	}
	
	public ACLMessage(Performative performative, AID sender, Set<AID> recipients, Object content) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.recipients = recipients;
		this.contentObj = content;
	}

	public Message toMessage(Session session) {
		Message message = null ;
		try {
			message = session.createObjectMessage(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return message;
	}

	public Performative getPerformative() {
		return performative;
	}

	public AID getSender() {
		return sender;
	}

	public Set<AID> getRecipients() {
		return recipients;
	}

	public AID getReplyTo() {
		return replyTo;
	}

	public String getContent() {
		return content;
	}
	
	public void setContentObj(Object o) {
		this.contentObj = o;
	}

	public Object getContentObj() {
		return contentObj;
	}

	public HashMap<String, Object> getUserArgs() {
		return userArgs;
	}

	public String getLanguage() {
		return language;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getConversationId() {
		return conversationId;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public long getReplyBy() {
		return replyBy;
	}

	
}