package rest.connection.restclient.clientproxies;

import rest.UserController;

public class UserResteasyClientProxy extends ResteasyClientProxy<UserController> {

	public UserResteasyClientProxy(String receiverHost) {
		super(receiverHost, "users", UserController.class);
	}

}
