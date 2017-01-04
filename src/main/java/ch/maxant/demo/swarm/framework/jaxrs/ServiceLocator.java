package ch.maxant.demo.swarm.framework.jaxrs;

import ch.maxant.demo.swarm.framework.web.WebContext;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

@RequestScoped
public class ServiceLocator {

    @Inject
    private WebContext context;

    /**
     * @param name the service to locate
     * @param path the path to call
     * @return a builder on which methods like get/post/put/delete can be called.
     */
    public Invocation.Builder locateService(String name, String path){

        //https://java.net/jira/browse/JAX_RS_SPEC-462
        Client client = ClientBuilder.newBuilder()//.withConfig(config) or add other stuff here, e.g. .trustStore()/
                .property("javax.ws.rs.client.http.connectionTimeout", 1000)
                .property("javax.ws.rs.client.http.socketTimeout", 5000)
                .build();
        /*
        Client client = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(2, TimeUnit.SECONDS)
                .build();
        */

        //TODO use Topology to locate the serice and build the URL.
        //TODO read scheme from topology too - and add it for say the nodejs stuff!
        return client
                .target("http://localhost:8081")
                .path(path)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + context.getToken());
    }


}
