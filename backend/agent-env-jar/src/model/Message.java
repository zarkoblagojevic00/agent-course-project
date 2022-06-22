package model;

import java.io.Serializable;
import java.time.LocalDateTime;


public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private User sender;
	private User recipient;
	private LocalDateTime created;
	private String subject;
	private String content;
	
	public Message() {
		
	}
	
	public Message(User sender, User receiver, LocalDateTime created, String subject, String content) {
		super();
		this.sender = sender;
		this.recipient = receiver;
		this.created = created;
		this.subject = subject;
		this.content = content;
	}

	public User getSender() {
		return sender;
	}

	public User getRecipient() {
		return recipient;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	@Override
	public boolean equals(Object obj) {
		Message message = (Message)obj;
		return sender.equals(message.sender) &&
				recipient.equals(message.recipient) &&
				created.equals(message.created) &&
				subject.equals(message.subject) &&
				content.equals(message.content);
	}
	
	@Override
	public String toString() {
		return "sender: " + sender.getUsername() + " recipient: " + recipient.getUsername() + " subject: " + subject + " content: " + content + " created: " + created;
	}
}
