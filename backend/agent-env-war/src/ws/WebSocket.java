package ws;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/{username}")
public class WebSocket extends BaseWebSocket {
	private Map<String, Session> sessions = new HashMap<String, Session>();
	
	@OnOpen
	public void onOpen(@PathParam("username") String username, Session session) {
		System.out.println("Session opened for: " + username);
		sessions.put(username, session);
	}
	
	@OnClose
	public void onClose(@PathParam("username") String username, Session session) {
		System.out.println("Session closed for: " + username);
		sessions.remove(username);
	}
	
	@OnError
	public void onError(@PathParam("username") String username, Session session, Throwable t) {
		System.out.println("Session error for: " + username);
		sessions.remove(username);
		t.printStackTrace();
	}
	
	public void onMessage(String username, String message) {
		Session session = sessions.get(username);
		sendMessage(session, message);
	}
	
	public void onMessage(String message) {
		sessions.values().forEach(session -> sendMessage(session, message));
	}
}
