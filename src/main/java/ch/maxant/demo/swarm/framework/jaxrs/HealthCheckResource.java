package ch.maxant.demo.swarm.framework.jaxrs;

//import org.wildfly.swarm.monitor.Health;
//import org.wildfly.swarm.monitor.HealthStatus;

import javax.ws.rs.Path;

@Path("/health")
public class HealthCheckResource {

    /* TODO get monitoring to work
    @GET
    @Path("/health")
    @Health
    public HealthStatus checkDiskspace() {

        File path = new File(System.getProperty("user.home"));
        long freeBytes = path.getFreeSpace();
        long threshold = 1024 * 1024 * 100; // 100mb
        return freeBytes>threshold ?
                HealthStatus.
                        named("diskspace")
                        .up()
                        .withAttribute("freebytes", freeBytes) :
                HealthStatus.
                        named("diskspace")
                        .down()
                        .withAttribute("freebytes", freeBytes);
    }

    @GET
    @Path("/second-health")
    @Health
    public HealthStatus checkSomethingElse() {
        return HealthStatus
                .named("something-else")
                .up()
                .withAttribute("date", new Date().toString())
                .withAttribute("time", System.currentTimeMillis());
    }
    */
}
