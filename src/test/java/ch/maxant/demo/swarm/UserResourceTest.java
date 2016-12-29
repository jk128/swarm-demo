package ch.maxant.demo.swarm;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserResourceTest {

    @BeforeClass
    public static void init() throws Exception {
        //TODO replace with maven pre-integration-test
        Main.main(new String[]{});
    }

    @Test
    public void testGetAll() {

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:8081");
        builder.setAccept(ContentType.JSON);
        builder.addHeader("Authorization", "Bearer " + "TODO-token");
        RequestSpecification spec = builder.build();

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
