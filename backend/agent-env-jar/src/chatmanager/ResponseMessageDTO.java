package chatmanager;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6902574011417265479L;
	private String subject;
	private String content;
	private String sender;
	private String recipient;
	private LocalDateTime created;
	
	public ResponseMessageDTO() {
		super();
	}

	public ResponseMessageDTO(String subject, String content, String sender, String recipient, LocalDateTime created) {
		super();
		this.subject = subject;
		this.content = content;
		this.sender = sender;
		this.recipient = recipient;
		this.created = created;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	
	public LocalDateTime getCreated() {
		return created;
	}
}
