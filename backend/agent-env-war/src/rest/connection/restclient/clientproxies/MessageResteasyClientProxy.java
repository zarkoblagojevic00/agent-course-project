package rest.connection.restclient.clientproxies;

import rest.MessageController;

public class MessageResteasyClientProxy extends ResteasyClientProxy<MessageController> {

	public MessageResteasyClientProxy(String receiverHost) {
		super(receiverHost, "messages", MessageController.class);
	}

}
