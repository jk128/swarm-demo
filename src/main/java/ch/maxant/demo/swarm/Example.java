package ch.maxant.demo.swarm;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;

@Path("/")
@ApplicationPath("/")
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
//        new Swarm().start().deploy();
        Swarm swarm = new Swarm();
        swarm.start().deploy(buildTestDeployment());
    }

    public static Archive buildTestDeployment() throws Exception {
        WARArchive archive = ShrinkWrap.create(WARArchive.class);
        archive.addServlet("asdf", Asdf.class.getName());
        archive.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
        archive.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "WEB-INF/beans.xml");
        archive.addPackages(true, "ch.maxant.demo");
        archive.addAllDependencies();
        return archive;
    }

    @WebServlet(urlPatterns = {"/asdf"})
    public static class Asdf extends HttpServlet {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.sendError(500, "WOW");
        }
    }
}
