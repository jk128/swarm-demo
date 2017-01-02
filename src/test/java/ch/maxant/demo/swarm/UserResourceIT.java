package ch.maxant.demo.swarm;

import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.junit.Test;

import static ch.maxant.demo.swarm.TestUtils.buildRequestSpecificationWithJwt;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserResourceIT {

    @Test
    public void testGetAll() throws Exception {

        //since this test calls the fully fledged application, it needs a valid JSON Web Token
        RequestSpecification spec = buildRequestSpecificationWithJwt();

        given(spec).
                when().
                get("/all").
                then().
                statusCode(StatusCodes.INTERNAL_SERVER_ERROR); //TODO coz of problem with initial request...

        //TODO here is the actual test:
        given(spec)
                .when()
                .get("/all")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)

                //https://github.com/rest-assured/rest-assured/wiki/Usage#example-3---complex-parsing-and-validation
                //http://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/Collection.html#find()
                .body("find { it.id == 1 }.name", is("John Smith"));
    }

}
