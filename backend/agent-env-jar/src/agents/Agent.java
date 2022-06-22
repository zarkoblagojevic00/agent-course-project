package agents;

import java.io.Serializable;

import messagemanager.AgentMessage;

public interface Agent extends Serializable {
	public void init(String agentId);
	public void handleMessage(AgentMessage message);
	public String getAgentId();
}
