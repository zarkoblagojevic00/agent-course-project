package ws;

import javax.ejb.Singleton;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/types")
public class AgentTypesWebSocket extends SetSessionWebSocket {

}
