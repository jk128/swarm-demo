package ch.maxant.demo.swarm;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.jpa.JPAFraction;

public class Main {
    public static void main(String[] args) throws Exception {
        Swarm swarm = new Swarm();

//TODO        december 8th bbc4 vienna;

        //Context ctx = new InitialContext();
        //DataSource ds = (DataSource) ctx.lookup("jboss/datasources/ExampleDS");

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

        swarm.start();

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.addPackages(true, Main.class.getPackage());
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()), "classes/META-INF/persistence.xml");
        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()), "classes/META-INF/beans.xml"); //needed otherwise it doesnt seem to be able to create cdi beans
        deployment.addResource(UserResource.class);
        deployment.addModule("com.mysql");
        deployment.addAllDependencies();

/*
        // Builder for web.xml and jboss-web.xml
        WebXmlAsset webXmlAsset = deployment.findWebXmlAsset();
        webXmlAsset.setLoginConfig("BASIC", "realm");
        webXmlAsset.protect("/*")
                .withMethod("GET") //TODO add other methods too
                .withMethod("POST") //TODO add other methods too
                .withMethod("PUT") //TODO add other methods too
                .withMethod("DELETE") //TODO add other methods too
                .withRole("admin");

        deployment.setSecurityDomain("domain");
*/
        // Or, you can add web.xml and jboss-web.xml from classpath or somewhere
        // deployment.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml");
        // deployment.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/jboss-web.xml", Main.class.getClassLoader()), "jboss-web.xml");


        /*
        call using:

GET /all HTTP/1.1
Host: localhost:8080
Authorization: Basic UGVubnk6cGFzc3dvcmQ=
Cache-Control: no-cache
Postman-Token: feaa4370-3628-9b6b-32ba-08b25c211b0a

         */

        swarm.deploy(deployment);
    }
}
