package rest.restclient.base;

public interface EndpointProxyHandler<T> {
	public void handle(T endpointProxy);
}
