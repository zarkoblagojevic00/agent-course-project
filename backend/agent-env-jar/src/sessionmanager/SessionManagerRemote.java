package sessionmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import model.Message;
import model.User;
import model.UserWithHostDTO;
import rest.dtos.NewMessageDTO;
import sessionmanager.dtos.SessionInfoDTO;

@Remote
public interface SessionManagerRemote {

	public SessionInfoDTO login(User user);

	public boolean register(User user);
	
	public UserWithHostDTO getUserWithHost(String username);

	public List<UserWithHostDTO> getLoggedInUsers();
	
	public ArrayList<User> getFullLoggedInUsers();
	
	public ArrayList<User> getFullRegisteredUsers();
	
	public List<UserWithHostDTO> getRegisteredUsers();
	
	public boolean logout(String username);

	public List<String> getLocalRecipients();

	public User getLoggedInUser(String username);
	
	public Message unpackMessage(NewMessageDTO dto);

	public List<String> getOtherLocalRecipients(String username);
	
	public void receiveLoggedInUsersFromMasterNode(Collection<User> users);
	
	public void receiveRegisteredUsersFromMasterNode(Collection<User> users);

	public boolean addRegisteredFromOtherNode(User user);

	public boolean addLoggedInFromOtherNode(User user);

	public boolean logoutFromOtherNode(String username);

	public void logOutAllUsersFromnode(String nodeAlias);
}
