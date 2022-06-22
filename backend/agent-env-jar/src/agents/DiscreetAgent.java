package agents;

import messagemanager.AgentMessage;

public abstract class DiscreetAgent extends AgentBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 706748295555820455L;

	@Override
	public void handleMessage(AgentMessage message) {
		if (shouldReceiveMessage(message)) {
			handleMessageDiscreetly(message);
		}
	}

	// Every agent must implement this method
	protected abstract void handleMessageDiscreetly(AgentMessage message);
	
	private boolean shouldReceiveMessage(AgentMessage message) {
		String agentId = getAgentId();
		return message.getRecipients().stream().anyMatch(r -> r.equals(agentId));
	}
	
	
}