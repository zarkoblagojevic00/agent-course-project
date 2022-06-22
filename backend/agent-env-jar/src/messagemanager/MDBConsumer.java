package messagemanager;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agentmanager.AgentManagerRemote;
import agents.Agent;

/**
 * Message-Driven Bean implementation class for: MDBConsumer
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {


	@EJB
	private AgentManagerRemote agm;
	/**
	 * Default constructor.
	 */
	public MDBConsumer() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		try {
			deliverMessage(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

	private void deliverMessage(Message message) throws JMSException {
		AgentMessage agentMessage = (AgentMessage) ((ObjectMessage) message).getObject();
		
		for (String recipient: agentMessage.getRecipients()) {
			Agent agent = agm.getRunningAgent(recipient);
			Runnable runnable = () -> agent.handleMessage(agentMessage);
			new Thread(runnable).start();
		}
	
	}
	
}
