package ws;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

public class SetSessionWebSocket extends BaseWebSocket {
private Set<Session> sessions = new HashSet<Session>();
	
	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
	}
	
	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
	}
	
	
	public void send(String message) {
		for(Session session : sessions)
			sendMessage(session, message);
	}
	
}
