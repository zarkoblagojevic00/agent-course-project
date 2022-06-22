package rest.connection.restclient.clientproxies;

import rest.connection.ConnectionController;

public class ConnectionResteasyClientProxy extends ResteasyClientProxy<ConnectionController> {

	public ConnectionResteasyClientProxy(String receiverHost) {
		super(receiverHost, "connections", ConnectionController.class);
	}

}
