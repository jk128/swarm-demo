# What is this?

A demo showing a Swarm application with:

- JAX-RS
- CDI
- EJB
- JPA
- JTA
- SpringData
- Security via Keycloak (including social login)
- SSL
- Project Stages (Configuration)
- Flyway
- Tests with running Swarm but H2 Database and mock beans
- Integration Tests with final Swarm running (mysql), including login with keycloak

#Useful Links

- https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/
- [System Properties](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration_properties.html)
- debug logging for SSL: add `-Djavax.net.debug=all`

# Issues

- https://issues.jboss.org/browse/SWARM-967
- https://issues.jboss.org/browse/SWARM-910

# TODO

- upgrade to 2016.12.1
- See https://deltaspike.apache.org/documentation/projectstage.html for mocking in tests
- envers
- finish configing logging in Main properly. eg how to use own format?
- jax-rs2 client for calling other services
- gradle mail
- bean validation
- others? checkout other examples
- keycloak => 
  - get it working, see https://groups.google.com/d/msg/wildfly-swarm/G_-uGRUeiVo/1pLI8USvAgAJ
- project-stages.yml: see https://issues.jboss.org/browse/SWARM-967
- swarm and ITs and measuring coverage
- cors: https://github.com/wildfly-swarm/wildfly-swarm-examples/blob/master/jaxrs/health/src/main/java/org/wildfly/swarm/examples/jaxrs/health/CORSFilter.java
- add custom service locator based on consul health and tags

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


----------------------------------------------------


# Mysql Issues

Needed to fix a problem with events, so that flyway worked.

This worked using sudo: http://serverfault.com/questions/562282/mysqldump-error-1557-corrupt-event-table/562303

Before hand I did this: http://serverfault.com/questions/100685/cannot-proceed-because-system-tables-used-by-event-scheduler-were-found-damaged 
BUT DONT DROP THE events table!!

Unfortunately on the laptop, i dropped mysql.events and had to rebuild it with a partial script taken from /usr/share/mysql/mysql_system_tables.sql
That didn't quite work until i updated the default value of modified to "1970-01-02 00:00:00" which probably ISNT right :-(
