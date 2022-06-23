package ws;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ejb.Singleton;
import javax.websocket.server.ServerEndpoint;

import agents.AID;
import messagemanager.ACLMessage;


@Singleton
@ServerEndpoint("/ws/logger")
public class LoggerWebSocket extends SetSessionWebSocket {
	
	@Override
	public void send(String message) {
		super.send(getCurrentDateFormatted() + message);
		System.out.println(message);
	}

	public void log(ACLMessage message) {
		send(ACLToLoggingString(message));
	}
	
	private String ACLToLoggingString(ACLMessage message) {
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(getCurrentDateFormatted());
		sb.append("----- New ACL Message -----");
		sb.append("\n");
		
		sb.append("Sender: ");
		sb.append(message.getSender().getType().getName());
		sb.append("-");
		sb.append(message.getSender().getName());
		sb.append("\n");
		
		sb.append("Recipient(s): [ ");
		for(AID recipient : message.getRecipients()) {
			sb.append(recipient.getType().getName());
			sb.append("-");
			sb.append(recipient.getName());
			sb.append(" ");
		}
		sb.append("]\n");
		
		sb.append("Performative: ");
		sb.append(message.getPerformative().toString());
		sb.append("\n");
		
		sb.append("Content: ");
		sb.append(message.getContent());
		sb.append("\n");
		
		sb.append("User args:");
		sb.append(message.getUserArgs().toString());
		sb.append("\n");
		
		return sb.toString();
	}
	
	private String getCurrentDateFormatted() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		return "[" + dtf.format(LocalDateTime.now()) + "] ";
	}
}
