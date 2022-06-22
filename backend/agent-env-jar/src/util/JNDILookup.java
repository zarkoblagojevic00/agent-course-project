package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import agents.Agent;
import agents.MasterAgent;
import agents.UserAgent;
import messagemanager.MessageManagerBean;
import messagemanager.MessageManagerRemote;
import sessionmanager.SessionManagerBean;
import sessionmanager.SessionManagerRemote;

public abstract class JNDILookup {

	public static final String JNDIPathChat = "ejb:Chat-ear/Chat-jar//";
	
	public static final String AgentManagerLookup = JNDIPathChat + AgentManagerBean.class.getSimpleName() + "!"
			+ AgentManagerRemote.class.getName();
	
	public static final String MessageManagerLookup = JNDIPathChat + MessageManagerBean.class.getSimpleName() + "!"
			+ MessageManagerRemote.class.getName();
	
	public static final String ChatManagerLookup = JNDIPathChat + SessionManagerBean.class.getSimpleName() + "!"
			+ SessionManagerRemote.class.getName();
	
	public static final String UserAgentLookup = JNDIPathChat + UserAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	
	public static final String MasterAgentLookup = JNDIPathChat + MasterAgent.class.getSimpleName() + "!"
			+ Agent.class.getName();

	@SuppressWarnings("unchecked")
	public static <T> T lookUp(String name, Class<T> c) {
		T bean = null;
		try {
			Context context = new InitialContext();

			System.out.println("Looking up: " + name);
			bean = (T) context.lookup(name);

			context.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return bean;
	}
}
