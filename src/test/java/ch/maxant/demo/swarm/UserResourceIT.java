package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.framework.jaxrs.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.junit.Test;

import java.io.IOException;

import static ch.maxant.demo.swarm.TestUtils.buildRequestSpecificationWithJwt;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class UserResourceIT {

    @Test
    public void testGetAll() throws Exception {

        //since this test calls the fully fledged application, it needs a valid JSON Web Token
        RequestSpecification spec = buildRequestSpecificationWithJwt();

        given(spec)
                .when()
                .get("/all")
                .then()
                .statusCode(StatusCodes.INTERNAL_SERVER_ERROR); //TODO coz of problem with initial request...

        //TODO here is the actual test:
        given(spec)
                .when()
                .get("/all")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)

                //https://github.com/rest-assured/rest-assured/wiki/Usage#example-3---complex-parsing-and-validation
                //http://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/Collection.html#find()
                .body("find { it.id == 1 }.name", is("John Smith"))
        ;
    }

    @Test
    public void testGetUser() throws IOException {
        RequestSpecification spec = buildRequestSpecificationWithJwt();

        given(spec)
                .when()
                .get("/simpleUser/1")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)
                .body("name", is("John Smith"))
                .body("id", is(1))
        ;
    }

    @Test
    public void testCreateWithValidation() throws IOException {
        RequestSpecification spec = buildRequestSpecificationWithJwt();

        ObjectMapper mapper = JacksonConfig.getMapper();

        User u = new User();
        u.setId((int)System.currentTimeMillis()); //statistically OK
        u.setName("John Jones");
        u.setPassword("12"); //too short!!
        String body = mapper.writeValueAsString(u);

        given(spec)
                .when()
                .body(body)
                .put("/save")
                .then()
                .log().body()
                .statusCode(StatusCodes.PRECONDITION_FAILED)
                .body("constraintViolations", hasSize(1))
                .body("constraintViolations[0].violation", equalTo("password size must be between 5 and 100"))
        ;

        u.setPassword("12345"); //this time ok
        body = mapper.writeValueAsString(u);

        given(spec)
                .when()
                .body(body)
                .put("/save")
                .then()
                .log().body()
                .statusCode(StatusCodes.CREATED)
        ;
    }
}
