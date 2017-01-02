package ch.maxant.demo.swarm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.undertow.WARArchive;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

import static ch.maxant.demo.swarm.Main.PRIMARY_DS;

public final class TestUtils {

    public static RequestSpecification buildRequestSpecificationWithJwt() throws IOException {
        String token = getToken("test_tullia", "asdf");

        RequestSpecBuilder builder = buildBasicRequestSpecification();
        builder.addHeader("Authorization", "Bearer " + token);
        return builder.build();
    }

    private static String getBaseUriForLocalhost() {
        return "http://localhost:" + (8080 + Integer.getInteger("swarm.port.offset", 1));
    }

    public static String getToken(final String username, final String password) throws IOException {

        //Since we're have to use SSL to contact keycloak, we need to tell Java which cacerts to use.
        //This MUST have parent certificate installed!
        //TODO how come I need to set this? if the standard jre has the certificate installed...
        System.setProperty("javax.net.ssl.trustStore", "/usr/java/latest/jre/lib/security/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

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

    public static RequestSpecification buildRequestSpecification() {
        RequestSpecBuilder builder = buildBasicRequestSpecification();
        return builder.build();
    }

    private static RequestSpecBuilder buildBasicRequestSpecification() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri(getBaseUriForLocalhost());
        builder.setAccept(ContentType.JSON);
        return builder;
    }

    public static Archive buildTestDeployment() throws Exception {
        WARArchive archive = ShrinkWrap.create(WARArchive.class);
        archive.setWebXML(new File("src/test/resources/web.xml")); //no security here please
        archive.addAsWebInfResource(new File("src/test/resources/beans.xml"));
        archive.addAsWebInfResource(new File("src/main/resources/META-INF/persistence.xml"), "classes/META-INF/persistence.xml");
        for(File mig : new File("src/main/resources/db/migration").listFiles()){
            archive.addAsWebInfResource(mig, "classes/db/migration/" + mig.getName());
        }
        archive.addPackages(true, "ch.maxant.demo");
        archive.addAllDependencies();
        return archive;
    }

    public static Swarm buildSwarmWithH2() throws Exception {
        Swarm swarm = new Swarm();

        //NO security
        //H2 database - see pom which excludes mysql during unit tests so its not autodetected by swarm

        swarm.fraction(new DatasourcesFraction()
                .jdbcDriver("h2", (d) -> {
                    d.driverClassName("org.h2.Driver");
                    d.xaDatasourceClass("org.h2.jdbcx.JdbcDataSource");
                    d.driverModuleName("com.h2database.h2");
                })
                .dataSource(PRIMARY_DS, (ds) -> {
                    ds.driverName("h2");
                    ds.connectionUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                    ds.userName("sa");
                    ds.password("sa");
                })
        );

        swarm.fraction(new JPAFraction()
                .defaultDatasource("jboss/datasources/primaryDS")
        );

        return swarm;
    }

}
