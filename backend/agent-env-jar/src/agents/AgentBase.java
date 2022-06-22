package agents;

public abstract class AgentBase implements Agent {

	private static final long serialVersionUID = 1L;
	private String agentId;
	
	@Override
	public void init(String agentId) {
		this.agentId = agentId;
	}

	@Override
	public String getAgentId() {
		return agentId;
	}

}
