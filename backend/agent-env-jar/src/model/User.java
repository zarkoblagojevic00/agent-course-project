package model;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private Host host;

	public User() {};
	
	public User(String username, String password, Host host) {
		this.username = username;
		this.password = password;
		this.host = host;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", host=" + host + "]";
	}

	

}
