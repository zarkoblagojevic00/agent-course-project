package rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;

@Stateless
@Path("/messages")
public class MessageControllerBean implements MessageController {

	@EJB
	private MessageManagerRemote messageManager;
	
	@Override
	public void sendMessage(ACLMessage message) {
		messageManager.post(message);
	}

	@Override
	public List<String> getPerformatives() {
		return messageManager.getPerformatives();
	}

}
