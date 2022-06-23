package agents;

import java.io.Serializable;

import messagemanager.ACLMessage;

public interface Agent extends Serializable {
	public void init(AID aid);
	public AID getAID();
	public void handleMessage(ACLMessage message);
}
