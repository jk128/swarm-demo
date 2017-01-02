package ch.maxant.demo.swarm;

import com.jayway.restassured.authentication.BasicAuthScheme;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Ignore
@RunWith(Arquillian.class)
public class UserResource2AT {

    //https://groups.google.com/forum/#!topic/wildfly-swarm/83DnCXXH2PY

    //TODO how to add mock services to the deployment, when we have to provide classnames rather than bean instances?
/* commented out because otherwise mock interferes with swarm starting normally, because there are two admin service classes
    public abstract static class MockUserRepository implements UserRepository {
        public List<User> findAll() {
            return Arrays.asList(new User());
        }
    }

    public static class MockAdminService extends AdminService {
    }

    @Deployment
    public static Archive createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.addResource(UserResource.class);
        deployment.addClass(MockUserRepository.class);
        deployment.addClass(MockAdminService.class);
        return deployment;
    }
*/
/*
    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return Main.buildSwarm();
    }
*/
/*
    @Drone
    WebDriver browser; //selenium

    @Test
    public void testGetAllUsingSelenium() {
        browser.navigate().to("http://localhost:8080/all");

        String pageSource = browser.getPageSource();
        assertTrue(pageSource, pageSource.contains("John Smith"));
    }
*/

    @Test
    public void testGetAll() {

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:8080");
        builder.setAccept(ContentType.JSON);
        BasicAuthScheme auth = new BasicAuthScheme();
        auth.setUserName("admin");
        auth.setPassword("admin");
        builder.setAuthentication(auth);
        RequestSpecification spec = builder.build();

// Now you can re-use the "responseSpec" in many different tests:
        given(spec).
                when().
                get("/all").
                then().
                body("price", is("John Smith"));
    }
}
