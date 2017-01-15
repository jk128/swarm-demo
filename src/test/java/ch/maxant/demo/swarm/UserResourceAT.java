package ch.maxant.demo.swarm;

import io.undertow.util.StatusCodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wildfly.swarm.Swarm;

import static ch.maxant.demo.swarm.TestUtils.*;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

//WATCH OUT: does not run from IDE, because hibernate-entitymanager is on the (test) classpath. Maven let's it remove it from the classpath for '**/*AT.java', IntelliJ doesn't.
public class UserResourceAT {

    private static Swarm swarm;

    @BeforeClass
    public static void init() throws Exception {
        swarm = buildSwarmWithH2();
        swarm.start().deploy(buildTestDeployment());
    }

    @AfterClass
    public static void shutdown() throws Exception {
        swarm.stop();
    }

    /** This test uses a mock for the UserRepository - see TestCdiSetup */
    @Test
    public void testGetAll() {
        given(buildRequestSpecification())
                .when()
                .get("/all")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)

                //https://github.com/rest-assured/rest-assured/wiki/Usage#example-3---complex-parsing-and-validation
                //http://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/Collection.html#find()
                .body("find { it.id == 1 }.name", is("U1"))
                .body("find { it.id == 2 }.name", is("U2"));
    }

    /** This test uses h2 for its database when it accesses data via the entity manager */
    @Test
    public void testGetAllWithEm() {
        given(buildRequestSpecification())
                .when()
                .get("/allWithEm")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)
                .body("find { it.id == 1 }.name", is("John Smith"))
                .body("find { it.id == 2 }.name", is("Jane Smith"));
    }
}