package ch.maxant.demo.swarm;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

/**
 * this test uses a stub service instead of a real one (see beans.xml#alternatives and TestSetup) as well as the
 * {@link #buildTestDeployment()} method below.
 */
public class ExampleTest {

    private Swarm swarm;

    @Before
    public void setup() throws Exception {
        swarm = new Swarm();
        swarm.start().deploy(buildTestDeployment());
    }

    @After
    public void tearDown() throws Exception {
        swarm.stop();
    }

    @Test
    public void test() {
        String result = given(buildSpec())
                .when()
                .get("/")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)
                .extract().body().asString();
        assertEquals("Using test bean", result);
    }

    private RequestSpecification buildSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:8080");
        return builder.build();
    }

    public static Archive buildTestDeployment() throws Exception {
        WARArchive archive = ShrinkWrap.create(WARArchive.class);
        archive.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
        archive.addAsWebInfResource(new File("src/test/resources/beans.xml"));
        archive.addPackages(true, "ch.maxant.demo");
        archive.addAllDependencies();
        return archive;
    }
}
