package ch.maxant.demo.swarm;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.keycloak.Secured;
import org.wildfly.swarm.logging.LoggingFraction;

public class Main {
    public static void main(String[] args) throws Exception {

        System.setProperty("swarm.port.offset", "1");

        Swarm swarm = buildSwarm();

        swarm.start();

        //JAXRSArchive deployment = buildDeployment();

        /*
        call using:

            GET /all HTTP/1.1
            Host: localhost:8080
            Authorization: Basic UGVubnk6cGFzc3dvcmQ=
            Cache-Control: no-cache
            Postman-Token: feaa4370-3628-9b6b-32ba-08b25c211b0a

         */

        //swarm.deploy(deployment);
        swarm.deploy();
    }

    static JAXRSArchive buildDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "appName.war"); //provides default Application and @ApplicationPath
        deployment.addPackages(true, Main.class.getPackage());
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()), "classes/META-INF/persistence.xml");
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()), "classes/META-INF/beans.xml"); //needed because of interceptors
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/ejb-jar.xml", Main.class.getClassLoader()), "classes/META-INF/ejb-jar.xml"); //needed because of interceptors
        deployment.addAsManifestResource(new ClassLoaderAsset("META-INF/ejb-jar.xml", Main.class.getClassLoader()), "ejb-jar.xml"); //needed because of interceptors
//why is this added in some demos, but doesnt really seem to be required?        deployment.addResource(UserResource.class);
        deployment.addModule("com.mysql");
        deployment.addAllDependencies();

/*
        // Builder for web.xml and jboss-web.xml - for security based on normal jboss stuff
        WebXmlAsset webXmlAsset = deployment.findWebXmlAsset();
        webXmlAsset.setLoginConfig("BASIC", "realm");
        webXmlAsset.protect("/*")
                .withMethod("GET")
                .withMethod("POST")
                .withMethod("PUT")
                .withMethod("DELETE")
                .withRole("admin");

        deployment.setSecurityDomain("domain");
*/
        // Or, you can add web.xml and jboss-web.xml from classpath or somewhere
        // deployment.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml");
        // deployment.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/jboss-web.xml", Main.class.getClassLoader()), "jboss-web.xml");

        deployment.as(Secured.class) //adds keycloak security
                ;/*
        //TODO get this working: https://groups.google.com/d/msg/wildfly-swarm/G_-uGRUeiVo/1pLI8USvAgAJ
                .protect()
                .withMethod("GET", "POST", "PUT", "DELETE")
                .withRole("uma_authorization");
                */

        return deployment;
    }

    static Swarm buildSwarm() throws Exception {
        Swarm swarm = new Swarm();

        swarm.fraction(new DatasourcesFraction()
                .jdbcDriver("com.mysql", (d) -> {
                    d.driverClassName("com.mysql.cj.jdbc.Driver");
                    d.xaDatasourceClass("com.mysql.cj.jdbc.MysqlXADataSource");
                    d.driverModuleName("com.mysql");
                })
                .dataSource("ExampleDS2", (ds) -> {
                    ds.driverName("com.mysql");
                    ds.connectionUrl("jdbc:mysql://localhost:3306/tullia_users?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
                    ds.userName("root");
                    ds.password("password");
                })
        );

        swarm.fraction(new JPAFraction()
                               .defaultDatasource("jboss/datasources/ExampleDS2")
        );

        swarm.fraction(
                new LoggingFraction()
                        .defaultFormatter()
                        .consoleHandler(Level.INFO, "PATTERN")
                        //.fileHandler(FILE_HANDLER_KEY, "sql-file.log", Level.FINE, "%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n")
                        .rootLogger(Level.INFO, "CONSOLE")
                        //.logger("wildflyswarm.filelogger", l -> l.level(Level.FINE).handler(FILE_HANDLER_KEY).useParentHandlers(false))
        );

//        String sslRealm = "SSLRealm";
//        swarm.fraction(ManagementFraction.createDefaultFraction()
/*                 .securityRealm(sslRealm, realm ->
                    realm.sslServerIdentity(ssi ->
                        ssi.keystorePassword("changeit")
                            .keystorePath("/usr/java/latest/jre/lib/security/cacerts")
                            .alias("auth.maxant.ch")
                    )
            )
*/
//        );
/*
        swarm.fraction(
                UndertowFraction.createDefaultFraction()
                        .server("default-server", server -> {
                            String https = "https";
                                    server.httpsListener(new HttpsListener(https)
                                            .securityRealm(sslRealm)//must match the realm added above
                                            .socketBinding(https));
                                } //must match the socket binding you have for ssl.
                        )
        );
*/

        //TODO https://issues.jboss.org/browse/SWARM-910
        //swarm.fraction(UndertowFraction.createDefaultHTTPSOnlyFraction("/usr/java/latest/jre/lib/security/cacerts", "changeit", "auth.maxant.ch"));

        //TODO instead of a db based login module, i want one which checks JWTs created with keycloak
        //security for basic authentication
        /*
        swarm.fraction(SecurityFraction.defaultSecurityFraction()
                .securityDomain(new SecurityDomain("my-domain")
                        .classicAuthentication(new ClassicAuthentication()
                                .loginModule(new LoginModule("Database")
                                        .code("Database")
                                        .flag(Flag.REQUIRED).moduleOptions(new HashMap<Object, Object>() {{
                                            put("dsJndiName", "java:jboss/datasources/MyXaDS");
                                            put("principalsQuery", "SELECT password FROM REST_DB_ACCESS WHERE name=?");
                                            put("rolesQuery", "SELECT role, 'Roles' FROM REST_DB_ACCESS WHERE name=?");
                                        }})))));
        */
        //TODO how to make passwords md5 hashed in DB?

        //TODO to get spring up and running we need to add the web.xml and spring-context.xml?

        return swarm;
    }
}
