package connectionmanager;

import java.util.List;

import javax.ejb.Remote;

import model.Host;

@Remote
public interface ConnectionManagerRemote {
	public void addNode(Host node);
	public void removeNode(String nodeAlias);
	public List<String> getAllNodeAliases();
	public List<Host> getAllNodes();
	public Host getCurrentNode();
	public Host getNode(String nodeAlias);
}
