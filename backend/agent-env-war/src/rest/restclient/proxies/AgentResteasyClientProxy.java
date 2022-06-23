package rest.restclient.proxies;

import rest.AgentController;
import rest.restclient.base.ResteasyClientProxy;

public class AgentResteasyClientProxy extends ResteasyClientProxy<AgentController> {

	public AgentResteasyClientProxy(String receiverHost) {
		super(receiverHost, "agents", AgentController.class);
	}

}
