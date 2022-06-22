package rest.connection.restclient;

public interface EndpointProxyHandler<T> {
	public void handle(T endpointProxy);
}
