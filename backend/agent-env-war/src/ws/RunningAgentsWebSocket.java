package ws;

import javax.ejb.Singleton;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/agents")
public class RunningAgentsWebSocket extends SetSessionWebSocket {
	@Override
	public void send(String message) {
		super.send(message);
	}

}
