package connectionmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import connectionmanager.util.NodeInitializer;
import model.Host;

@Singleton
@LocalBean
public class ConnectionManagerBean implements ConnectionManagerRemote {
	private final Host currentNode;
	private Map<String, Host> nodes; 
	
	public ConnectionManagerBean() {
		nodes = new HashMap<>();
		currentNode = new NodeInitializer().createNode();
	}
	
	@Override
	public void addNode(Host node) {
		if (node.getAlias().equals(currentNode.getAlias())) return;
		System.out.println(String.format("Adding node: %s", node.getAlias()));
		nodes.put(node.getAlias(), node);
	}

	@Override
	public void removeNode(String nodeAlias) {
		nodes.remove(nodeAlias);
	}

	@Override
	public Host getCurrentNode() {
		return currentNode;
	}

	@Override
	public List<String> getAllNodeAliases() {
		return getAllNodes()
				.stream()
				.map(Host::getAlias)
				.collect(Collectors.toList());
	}

	@Override
	public List<Host> getAllNodes() {
		return Collections.unmodifiableList(new ArrayList<Host>(nodes.values()));
	}

	@Override
	public Host getNode(String nodeAlias) {
		return nodes.get(nodeAlias);
	}

}
