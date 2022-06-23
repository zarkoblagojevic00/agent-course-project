package ws;

import javax.ejb.Singleton;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/agents")
public class RunningAgentsWebSocket extends SetSessionWebSocket {

}
