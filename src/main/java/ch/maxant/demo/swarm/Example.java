package ch.maxant.demo.swarm;

import org.wildfly.swarm.Swarm;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@Path("/")
@ApplicationPath("/")
@Singleton
public class Example extends Application {

    @Inject
    private SomeService someService;

    @GET
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public String doSomething() {
        return someService.doSomething("John");
    }

    public static void main(String[] args) throws Exception {
        new Swarm().start().deploy();
    }
}
