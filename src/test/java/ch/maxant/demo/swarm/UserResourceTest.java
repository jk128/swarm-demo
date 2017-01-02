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
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserResourceTest {

    private Swarm swarm;

    @Before
    public void setup() throws Exception {
        swarm = buildSwarm();
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
                .get("/all")
                .then()
                .log().body()
                .statusCode(StatusCodes.OK)

                //https://github.com/rest-assured/rest-assured/wiki/Usage#example-3---complex-parsing-and-validation
                //http://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/Collection.html#find()
                .body("find { it.id == 1 }.name", is("U1"))
                .body("find { it.id == 2 }.name", is("U2"));
    }

    private RequestSpecification buildSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:" + (8080 + Integer.getInteger("swarm.port.offset", 1)));
        return builder.build();
    }

    static Archive buildTestDeployment() throws Exception {
        WARArchive archive = ShrinkWrap.create(WARArchive.class);
        archive.setWebXML(new File("src/test/resources/web.xml")); //no security here please
        archive.addAsWebInfResource(new File("src/test/resources/beans.xml"));
        archive.addAsWebInfResource(new File("src/main/resources/META-INF/persistence.xml"), "classes/META-INF/persistence.xml");
        archive.addPackages(true, "ch.maxant.demo");
        archive.addAllDependencies();
        return archive;
    }

    static Swarm buildSwarm() throws Exception {
        Swarm swarm = new Swarm();

        //NO security
        //H2 database - see pom which excludes mysql during unit tests so its not autodetected by swarm
/*
        swarm.fraction(new JPAFraction()
                .defaultDatasource("jboss/datasources/ExampleDS")
        );
*/

        swarm.fraction(new DatasourcesFraction()
                .jdbcDriver(swarm.stageConfig().resolve("database.jdbcDriver.name").getValue(), (d) -> {
                    d.driverClassName(swarm.stageConfig().resolve("database.jdbcDriver.driverClassName").getValue());
                    d.xaDatasourceClass(swarm.stageConfig().resolve("database.jdbcDriver.xaDatasourceClass").getValue());
                    d.driverModuleName(swarm.stageConfig().resolve("database.jdbcDriver.driverModuleName").getValue());
                })
                .dataSource("primaryDS", (ds) -> {
                    ds.driverName(swarm.stageConfig().resolve("database.datasource.driverName").getValue());
                    ds.connectionUrl(swarm.stageConfig().resolve("database.datasource.url").getValue());
                    ds.userName(swarm.stageConfig().resolve("database.datasource.username").getValue());
                    ds.password(swarm.stageConfig().resolve("database.datasource.password").getValue());
                })
        );

        swarm.fraction(
                new LoggingFraction()
                        .defaultFormatter()
                        .consoleHandler(Level.INFO, "PATTERN")
                        //.fileHandler(FILE_HANDLER_KEY, "sql-file.log", Level.FINE, "%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n")
                        .rootLogger(Level.INFO, "CONSOLE")
                //.logger("wildflyswarm.filelogger", l -> l.level(Level.FINE).handler(FILE_HANDLER_KEY).useParentHandlers(false))
        );

        return swarm;
    }

}