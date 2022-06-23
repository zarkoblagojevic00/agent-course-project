package rest.restclient.proxies;

import rest.MessageController;
import rest.restclient.base.ResteasyClientProxy;

public class MessageResteasyClientProxy extends ResteasyClientProxy<MessageController> {

	public MessageResteasyClientProxy(String receiverHost) {
		super(receiverHost, "messages", MessageController.class);
	}

}
