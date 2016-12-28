package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.framework.cdi.JwtSecured;
import ch.maxant.demo.swarm.framework.cdi.Secure;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/")
@Stateless
@JwtSecured(realm = "tullia", application = "app")
//@Interceptors(JwtSecuredInterceptor.class)
@Secure
public class UserResource {

    @Inject
    AdminService adminService;

    @Inject
    private UserService service;

    @GET
    @Path("all")
    @Produces("application/json")
    @JwtSecured(realm = "tullia", application = "app")
    public List<User> get() {
        return service.getAll();
    }

    @GET
    @Path("loggedInUser")
    @Produces("application/json")
    public String loggedInUser() {
        return adminService.getLoggedInUserAndVerifyRole();
    }


}
