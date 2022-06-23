package ws;

import java.io.IOException;

import javax.websocket.Session;

public abstract class BaseWebSocket {
	public void sendMessage(Session session, String message) {
		if(session.isOpen()) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
