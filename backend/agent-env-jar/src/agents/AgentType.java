package agents;

import java.io.Serializable;

public class AgentType implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean stateful;
	
	public AgentType() { }

	public AgentType(String name, boolean stateful) {
		super();
		this.name = name;
		this.stateful = stateful;
	}

	public String getName() {
		return name;
	}

	public boolean isStateful() {
		return stateful;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (stateful ? 1231 : 1237);
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
		AgentType other = (AgentType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (stateful != other.stateful)
			return false;
		return true;
	}
}
