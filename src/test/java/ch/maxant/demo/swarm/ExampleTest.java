package ch.maxant.demo.swarm;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wildfly.swarm.Swarm;

import static ch.maxant.demo.swarm.Example.buildTestDeployment;
import static com.jayway.restassured.RestAssured.given;

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
        given(buildSpec())
                .when()
                .get("/")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)
                .extract().body().asString().equals("Using test bean");
    }

    private RequestSpecification buildSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:8080");
        return builder.build();
    }
}
