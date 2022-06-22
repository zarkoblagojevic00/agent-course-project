package sessionmanager.dtos;

import java.io.Serializable;

public class SessionInfoDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7200969962633282573L;
	private String sessionId;
	private String username;
	private String hostAlias;
	
	public SessionInfoDTO(String sessionId, String user, String hostAlias) {
		super();
		this.sessionId = sessionId;
		this.username = user;
		this.hostAlias = hostAlias;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public String getUsername() {
		return username;
	}

	public String getHostAlias() {
		return hostAlias;
	}
}
