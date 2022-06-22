package model;

import java.io.Serializable;

public class UserWithHostDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5259669189642305365L;
	private String username;
	private String hostAlias;
	
	public UserWithHostDTO(String username, String hostAlias) {
		super();
		this.username = username;
		this.hostAlias = hostAlias;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getHostAlias() {
		return hostAlias;
	}
}
