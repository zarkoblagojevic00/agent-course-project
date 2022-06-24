package messagemanager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Session Bean implementation class MessageManagerBean
 */
@Stateless
@LocalBean
public class MessageManagerBean implements MessageManagerRemote {

	/**
	 * Default constructor.
	 */
	public MessageManagerBean() {
	}

	@EJB
	private JMSFactory factory;

	private Session session;
	private MessageProducer defaultProducer;

	@PostConstruct
	public void postConstruct() {
		session = factory.getSession();
		defaultProducer = factory.getProducer(session);
	}

	@PreDestroy
	public void preDestroy() {
		try {
			session.close();
		} catch (JMSException e) {
		}
	}

	public void post(ACLMessage msg) {
		try {
			defaultProducer.send(msg.toMessage(session));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Session getSession() {
		return factory.getSession();
	}

	@Override
	public MessageConsumer getConsumer() {
		return factory.getConsumer(session);
	}

	@Override
	public List<String> getPerformatives() {
		List<String> result = new ArrayList<String>();
		for(Performative p : Performative.values())
			result.add(p.toString());
		return result;
	}


}
