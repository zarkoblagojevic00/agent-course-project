package agents;

import java.io.Serializable;

import model.Host;


public class AID implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private Host host;
	private AgentType type;
	
	public AID() { }
	
	public AID(String name, Host host, AgentType type) {
		super();
		this.name = name;
		this.host = host;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Host getHost() {
		return host;
	}
	
	public AgentType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AID other = (AID) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
