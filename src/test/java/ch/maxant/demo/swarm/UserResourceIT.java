package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.framework.jaxrs.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.maxant.demo.swarm.TestUtils.buildRequestSpecificationWithJwt;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserResourceIT {

    @Test
    public void testGetAll() throws Exception {

        //since this test calls the fully fledged application, it needs a valid JSON Web Token
        RequestSpecification spec = buildRequestSpecificationWithJwt();

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

    ExecutorService service = Executors.newFixedThreadPool(50);

    Runnable r = () -> {
        RequestSpecification spec = null;
        try {
            spec = buildRequestSpecificationWithJwt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long start = System.currentTimeMillis();
        given(spec).when().get("/all").then().log().body().statusCode(200);
        System.out.println("Done in " + (System.currentTimeMillis()-start) + " ms");
        try{
            service.submit(UserResourceIT.this.create.call());
        }catch (Exception e){
            e.printStackTrace();
        }
    };

    Callable<Runnable> create = () -> r;

    @Ignore
    @Test
    public void testLow(){
        for(int i = 0; i < 50; i++){
            service.submit(r);
        }
        while(true){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
