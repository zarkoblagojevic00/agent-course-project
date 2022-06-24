package ws;

import javax.ejb.Singleton;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/types")
public class AgentTypesWebSocket extends SetSessionWebSocket {
	@Override
	public void send(String message) {
		super.send(message);
	}
}
