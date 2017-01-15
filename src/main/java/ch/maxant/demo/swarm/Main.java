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

import java.net.URL;
import java.net.URLClassLoader;

public class Main {

    public static final String PRIMARY_DS = "primaryDS";
    public static final String PRIMARY_DS_JNDI_NAME = "jboss/datasources/" + PRIMARY_DS;

    public static void main(String[] args) throws Exception {

        // done for https://issues.jboss.org/browse/SWARM-990:
        //System.setProperty("swarm.debug.bootstrap", "true");
        //System.setProperty("swarm.export.deployment", "true");

        logClasspath();

        //for keycloak - TODO replace with truststore attribute in keycloak.json
        System.setProperty("javax.net.ssl.trustStore", "/usr/java/latest/jre/lib/security/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

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

    private static void logClasspath() {
        System.out.println("=============== CLASSPATH: ==============================");
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }
        System.out.println("=========================================================");
    }

    static Swarm buildSwarm() throws Exception {
        Swarm swarm = new Swarm();

        //TODO i think this can be done easier, no? ie with just project-stages.yml and no code here?
        //see https://groups.google.com/forum/#!topic/wildfly-swarm/0E0-FyRJzJk ?
        swarm.fraction(new DatasourcesFraction()
                .jdbcDriver(swarm.stageConfig().resolve("primary_database.jdbcDriver.name").getValue(), (d) -> {
                    d.driverClassName(swarm.stageConfig().resolve("primary_database.jdbcDriver.driverClassName").getValue());
                    d.xaDatasourceClass(swarm.stageConfig().resolve("primary_database.jdbcDriver.xaDatasourceClass").getValue());
                    d.driverModuleName(swarm.stageConfig().resolve("primary_database.jdbcDriver.driverModuleName").getValue());
                })
                .dataSource(PRIMARY_DS, (ds) -> {
                    ds.driverName(swarm.stageConfig().resolve("primary_database.datasource.driverName").getValue());
                    ds.connectionUrl(swarm.stageConfig().resolve("primary_database.datasource.url").getValue());
                    ds.userName(swarm.stageConfig().resolve("primary_database.datasource.username").getValue());
                    ds.password(swarm.stageConfig().resolve("primary_database.datasource.password").getValue());
                })
        );

        swarm.fraction(new JPAFraction()
                               .defaultDatasource(PRIMARY_DS_JNDI_NAME)
        );

        //see https://groups.google.com/forum/#!topic/wildfly-swarm/0E0-FyRJzJk
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

        //configure keycloak for security
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

        return swarm;
    }

}
