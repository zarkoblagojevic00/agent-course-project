package ws;

import messagemanager.AgentMessage;
import messagemanager.AgentMessage.Type;

public class WebSocketResponse {
	private AgentMessage.Type response;
	private boolean success;
	private Object payload;
	
	public WebSocketResponse(Type response, boolean success, Object payload) {
		super();
		this.response = response;
		this.success = success;
		this.payload = payload;
	}
	
	public AgentMessage.Type getResponse() {
		return response;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public Object getPayload() {
		return payload;
	}
}
