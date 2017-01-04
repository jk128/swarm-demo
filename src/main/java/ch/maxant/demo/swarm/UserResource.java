package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.framework.cdi.Audited;
import ch.maxant.demo.swarm.framework.jaxrs.ServiceLocator;
import org.wildfly.swarm.topology.Advertise;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static ch.maxant.demo.swarm.UserResource.USER_SERVICE;

@Path("/")
@Stateless
@Audited //works on ejb too
//secured via web.xml and keycloak
//@RolesAllowed({"user"}) //TODO https://groups.google.com/forum/#!topic/wildfly-swarm/G_-uGRUeiVo
//@SecurityDomain("domain") //TODO required?
@Advertise(USER_SERVICE)
public class UserResource {

    public static final String USER_SERVICE = "user-service";

    @Inject
    AdminService adminService;

    @Inject
    private UserService service;

    @Inject
    private ServiceLocator serviceLocator;

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> get() {
        return service.getAll();
    }

    @GET
    @Path("allWithEm")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllWithEm() {
        return service.getAllUsingEntityManager();
    }

    @GET
    @Path("loggedInUser")
    @Produces("application/json")
    public String loggedInUser() {
        return adminService.getLoggedInUserAndVerifyRole();
    }

    @POST //actually a GET, but Resteasy cant cope with GET having a body!
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public User findUserByComparison(User user){
        return service.findUserByComparison(user);
    }

    @GET
    @Path("simpleUser/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimpleUser(@PathParam("id") String id){
        User user = new User();
        user.setId(Integer.parseInt(id));
        user = findUserByComparison(user);

        //now, to test jax-rs client, and "tolerant reading", lets call ourselves via REST, but NOT with the User class
        //rather with the SimpleUser class. that way we can see if irrelevant fields are ignored or not
        SimpleUser su = new SimpleUser();
        su.setName(user.getName());
        Entity<SimpleUser> u = Entity.json(su);
        Response response = serviceLocator
                .locateService(USER_SERVICE, "/user")
                .post(u);

        if(response.getStatus() >= 200 && response.getStatus() < 300) {
            //use "SimpleUser" to check "tolerant reader" => it shouldn't matter that there is more data - to be robust, we take only that data which interests us.
            su = response.readEntity(SimpleUser.class);
            return Response.ok(su).build();
        }else {
            String msg = "Downstream call failed with error: " + response.getStatus() + " - " + response.getStatusInfo().getReasonPhrase();
            return Response.serverError().entity(msg).build();
        }
    }

}
