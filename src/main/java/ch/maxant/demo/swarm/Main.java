package ch.maxant.demo.swarm;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.config.security.Flag;
import org.wildfly.swarm.config.security.SecurityDomain;
import org.wildfly.swarm.config.security.security_domain.ClassicAuthentication;
import org.wildfly.swarm.config.security.security_domain.authentication.LoginModule;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.security.SecurityFraction;

public class Main {
    public static void main(String[] args) throws Exception {

        System.setProperty("javax.net.ssl.trustStore", "/usr/java/latest/jre/lib/security/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");


        System.setProperty("swarm.port.offset", "1");

        Swarm swarm = buildSwarm();

        swarm.start();

        /*
        call using:

            GET /all HTTP/1.1
            Host: localhost:8080
            Authorization: Basic UGVubnk6cGFzc3dvcmQ=
            Cache-Control: no-cache
            Postman-Token: feaa4370-3628-9b6b-32ba-08b25c211b0a
         */

        swarm.deploy();
    }

    static Swarm buildSwarm() throws Exception {
        Swarm swarm = new Swarm();

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

        swarm.fraction(new JPAFraction()
                               .defaultDatasource("jboss/datasources/primaryDS")
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
        //TODO how to make passwords md5 hashed in DB? prolly with `put("hashAlgorithm", "MD5")` in the above code

        swarm.fraction(SecurityFraction.defaultSecurityFraction()
                .securityDomain(new SecurityDomain("domain")
                        .classicAuthentication(new ClassicAuthentication()
                                .loginModule(new LoginModule("org.keycloak.adapters.jboss.KeycloakLoginModule")
                                        .code("org.keycloak.adapters.jboss.KeycloakLoginModule")
                                        .flag(Flag.REQUIRED)
                                        )
                        )
                )
        );

        //TODO to get spring up and running we need to add the web.xml and spring-context.xml?

        return swarm;
    }
}
