package rest.restclient.proxies;

import rest.ConnectionController;
import rest.restclient.base.ResteasyClientProxy;

public class ConnectionResteasyClientProxy extends ResteasyClientProxy<ConnectionController> {

	public ConnectionResteasyClientProxy(String receiverHost) {
		super(receiverHost, "connections", ConnectionController.class);
	}

}
