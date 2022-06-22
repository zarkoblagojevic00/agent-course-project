package connectionmanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import model.Host;

public class NodeInitializer {
	public static final String IPV4_PORT_FORMAT = "%s:8080";
	
	public Host createNode() {
		String nodeAddress = getNodeAddress();
		String alias = String.format(IPV4_PORT_FORMAT,  getNodeAlias());
		String masterAlias = getMasterAlias();
		System.out.print(String.format("*** %s started at: %s ***", alias, nodeAddress));
		return new Host(nodeAddress, alias, masterAlias);
	}
	
	
	private String getNodeAddress() {		
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			return (String) mBeanServer.getAttribute(http, "boundAddress");			
		} catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getNodeAlias() {		
		return System.getProperty("jboss.node.name");
	}
		
	private String getMasterAlias() {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream fileInput  = loader.getResourceAsStream("connectionmanager/util/connection.properties");
			Properties connectionProperties = new Properties();
			connectionProperties.load(fileInput);
			fileInput.close();
			
			String masterIpv4 = connectionProperties.getProperty("master");
			
			if (masterIpv4.isEmpty()) {
				return null;
			}
			
			return String.format(IPV4_PORT_FORMAT, masterIpv4); 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
