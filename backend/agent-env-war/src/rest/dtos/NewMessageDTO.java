package rest.dtos;

import java.io.Serializable;

public class NewMessageDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2065479793757460880L;
	private String subject;
	private String content;
	private String sender;
	private String recipient;
	
	public NewMessageDTO() {
		super();
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public String getSender() {
		return sender;
	}

	public String getRecipient() {
		return recipient;
	}
}
