# What is this?

A demo showing a Swarm application with:

- JAX-RS
- CDI
- EJB
- JPA
- JTA
- SpringData
- Security via Keycloak (including social login)
- Project Stages (Configuration)
- Flyway
- Unit tests including CDI and JPA (`**/*Test.java`)
- Tests with running Swarm but H2 Database and mock beans (`**/*AT.java`)
- Integration Tests with final Swarm running (mysql), including login with keycloak (`**/*IT.java`)
- Topology with Consul
- JAX-RS 2.0 Client + Service Location via Consul + Keycloak Token passing 
- SSL (not yet, see issues)
- Monitoring (not yet, see issues)
- Java Bean Validation

#Useful Links

- https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/
- [System Properties](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration_properties.html)
- debug logging for SSL: add `-Djavax.net.debug=all`
- http://stackoverflow.com/questions/25962753/how-to-ignore-unexpected-fields-in-jax-rs-2-0-client

# Issues

- https://issues.jboss.org/browse/SWARM-910
- https://issues.jboss.org/browse/SWARM-967
- https://issues.jboss.org/browse/SWARM-975
- https://issues.jboss.org/browse/SWARM-976
- https://issues.jboss.org/browse/SWARM-990
- (https://issues.jboss.org/browse/SWARM-958)

# TODO

- dockerize build
- bean validation on UserResource / jax-rs so that it happens before saving to DB
- AT doesn't run from IDE coz of classloading problem :-(
- add custom service locator based on consul health and tags
- give test and actual archives nice names - tried `swarm.app.name`, `swarm.app.path` and `swarm.app.artifact` all of which cause problems either during tests or afterwards when running the jar. altho not so bad since when run from JAR it uses jar name.
- fix logging during tests:

        2017-01-04 23:51:56,262 ERROR [stderr] (main) SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
        2017-01-04 23:51:56,262 ERROR [stderr] (main) SLF4J: Defaulting to no-operation (NOP) logger implementation
        2017-01-04 23:51:56,263 ERROR [stderr] (main) SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

- See https://deltaspike.apache.org/documentation/projectstage.html for mocking in tests
- finish configing logging in Main properly. eg how to use own format? see swarm.logging.pattern-formatters.PATTERN.pattern 
- others? checkout other examples
- swarm and ITs and measuring coverage
- envers
- gradle mail
- add monitoring => see https://issues.jboss.org/browse/SWARM-976
- cors: https://github.com/wildfly-swarm/wildfly-swarm-examples/blob/master/jaxrs/health/src/main/java/org/wildfly/swarm/examples/jaxrs/health/CORSFilter.java
- upgrade to 2016.12.1 => https://issues.jboss.org/browse/SWARM-975
- keycloak => 
  - get it working, see https://groups.google.com/d/msg/wildfly-swarm/G_-uGRUeiVo/1pLI8USvAgAJ
- project-stages.yml: see https://issues.jboss.org/browse/SWARM-967

#Keycloak

Working with web.xml but not yet with `@RolesAllowed`; see https://groups.google.com/forum/#!topic/wildfly-swarm/G_-uGRUeiVo

Try adding `truststore` and `truststore-password` to keycloak.json, as shown here: https://keycloak.gitbooks.io/securing-client-applications-guide/content/topics/oidc/java/java-adapter-config.html, rather than setting truststore in main.

## Also interesting

- http://stackoverflow.com/questions/32992186/how-do-i-get-hold-of-http-request-headers-in-a-cdi-bean-thats-injected-into-a-j

## Debugged here:

- BearerTokenRequestAuthenticator
- called by org.keycloak.adapters.RequestAuthenticator
- extended by AbstractUndertowRequestAuthenticator
- http://grepcode.com/search/usages?id=repo1.maven.org$maven2@org.keycloak$keycloak-undertow-adapter@1.4.0.Final@org$keycloak$adapters$undertow@AbstractUndertowRequestAuthenticator&type=type&k=u
- RSATokenVerifier

# Gotchas

- Don't add the following two without test scope:

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxrs</artifactId>
            <version>${resteasy.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-client</artifactId>
            <version>${resteasy.version}</version>
            <scope>test</scope>
        </dependency>

  They interfere with Swarm. If you do add them (with test scope), they don't work anyway. So I used RestAssured for tests
  which works better anyway because the API supports implicit assertions.

# Mysql Issues

Needed to fix a problem with events, so that flyway worked.

This worked using sudo: http://serverfault.com/questions/562282/mysqldump-error-1557-corrupt-event-table/562303

Before hand I did this: http://serverfault.com/questions/100685/cannot-proceed-because-system-tables-used-by-event-scheduler-were-found-damaged 
BUT DONT DROP THE events table!!

Unfortunately on the laptop, i dropped mysql.events and had to rebuild it with a partial script taken from /usr/share/mysql/mysql_system_tables.sql
That didn't quite work until i updated the default value of modified to "1970-01-02 00:00:00" which probably ISNT right :-(
