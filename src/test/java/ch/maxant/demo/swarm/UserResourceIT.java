package ch.maxant.demo.swarm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import io.undertow.util.StatusCodes;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserResourceIT {

    @BeforeClass
    public static void init(){
        System.setProperty("javax.net.ssl.trustStore", "/usr/java/latest/jre/lib/security/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    @Test
    public void testGetAll() throws Exception {

        RequestSpecification spec = getRequestSpecification();

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

    public static RequestSpecification getRequestSpecification() throws IOException {
        String token = getToken("test_tullia", "asdf");

        RequestSpecBuilder builder = new RequestSpecBuilder();

        builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:" + (8080 + Integer.getInteger("swarm.port.offset", 1)));
        builder.setAccept(ContentType.JSON);
        builder.addHeader("Authorization", "Bearer " + token);
        return builder.build();
    }

    public static String getToken(final String username, final String password) throws IOException {
        String url = "https://tullia.maxant.ch/auth/realms/tullia/protocol/openid-connect/token";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes("grant_type=password&client_id=app&username=" + username + "&password=" + password);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new ObjectMapper().readTree(response.toString()).get("access_token").asText();
    }
}
