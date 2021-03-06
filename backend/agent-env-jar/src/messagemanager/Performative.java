package messagemanager;

public enum Performative {
	// chat master agent
	REGISTER,
	LOG_IN,
	LOG_OUT,
	GET_REGISTERED,
	GET_LOGGED_IN,
	
	// user agents
	SEND_MESSAGE_ALL,
	SEND_MESSAGE_USER,
	RECEIVE_MESSAGE,
	GET_ALL_MESSAGES, 
	GET_ALL_CHAT_MESSAGES, 
	GET_USER_CHAT_MESSAGES,
	
	OTHER_USER_LOGIN,
	OTHER_USER_REGISTER,
	OTHER_USER_LOGOUT, 
	
	RECEIVE_LOGGED_IN_FROM_MASTER_NODE, 
	RECEIVE_REGISTERED_FROM_MASTER_NODE,
	
	REGISTER_FROM_OTHER_NODE,
	LOG_IN_FROM_OTHER_NODE,
	LOG_OUT_FROM_OTHER_NODE,
	
	GET_HARVESTED,
	GET_SEARCHED
}
