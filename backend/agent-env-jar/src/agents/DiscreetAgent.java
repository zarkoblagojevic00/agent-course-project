package agents;

import messagemanager.ACLMessage;

public abstract class DiscreetAgent extends BaseAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 706748295555820455L;

	@Override
	public void handleMessage(ACLMessage message) {
		if (shouldReceiveMessage(message)) {
			handleMessageDiscreetly(message);
		}
	}

	// Every agent must implement this method
	protected abstract void handleMessageDiscreetly(ACLMessage message);
	
	private boolean shouldReceiveMessage(ACLMessage message) {
		AID agentId = getAID();
		return message.getRecipients().stream().anyMatch(r -> r.equals(agentId));
	}
	
	
}