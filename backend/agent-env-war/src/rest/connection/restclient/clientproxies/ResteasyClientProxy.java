package rest.connection.restclient.clientproxies;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import rest.connection.restclient.EndpointProxyHandler;

public class ResteasyClientProxy<T> {
	private final static String URL_FORMAT = "http://%s/Chat-war/api/%s";
	private final Class<T> proxyClass;
	private final String url;
	
	public ResteasyClientProxy(String host, String endpoint, Class<T> proxyClass) {
		super();
		this.url = String.format(URL_FORMAT, host, endpoint);
		this.proxyClass = proxyClass;
	}

	public void performAction(EndpointProxyHandler<T> proxyHandler) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client.target(url);
		T rest = rtarget.proxy(proxyClass);
		
		// do work with rest proxy in a new thread
		Runnable runnable = () -> { 
			proxyHandler.handle(rest);
			client.close();
		};
		new Thread(runnable).start();
	}
}
