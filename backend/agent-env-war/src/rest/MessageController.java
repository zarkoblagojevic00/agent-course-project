package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import rest.dtos.NewMessageDTO;

@Remote
public interface MessageController {
	
	// necessary
	@POST
	@Path("/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public void messageAll(NewMessageDTO message);
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public void messageUser(NewMessageDTO message);
	
	@GET
	@Path("/{username}")
	public void getUserMessages(@PathParam("username") String username);
	
	
	// utility
	@GET
	@Path("/chat/{username}")
	public void getGlobalChat(@PathParam("username") String username);
	
	@GET
	@Path("/chat/{username}/{recipient}")
	public void getChatWithOtherUser(@PathParam("username") String username, @PathParam("recipient") String recipient);
	
	@POST
	@Path("/all/other")
	@Consumes(MediaType.APPLICATION_JSON)
	public void messageAllFromOtherNode(NewMessageDTO message);
	
	@POST
	@Path("/user/other")
	@Consumes(MediaType.APPLICATION_JSON)
	public void messageUserFromOtherNode(NewMessageDTO message);
}
