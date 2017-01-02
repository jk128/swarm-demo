# What is this?

A demo showing a Swarm application with:

- JPA
- JAX-RS
- CDI
- JTA
- EJB
- SpringData
- Security via Keycloak (including social login)
- SSL
- Project Stages (Configuration)

#Useful Links

- https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/
- [System Properties](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration_properties.html)

# Issues

- https://issues.jboss.org/browse/SWARM-967
- https://issues.jboss.org/browse/SWARM-910

# TODO

- add integration test with mock like on the `comparison_to_springboot` branch
  - uses in memory DB and flyway
  - this way we can remove security requirements, since we use a different web.xml :-)
  - create proper integration test which logs in and uses test_tullia user to get a token (see jst node for example) to run tests
- upgrade to 2016.12.1
- add consul
- See https://deltaspike.apache.org/documentation/projectstage.html for mocking in tests
- update maven to do pre-integration-test stuff and delete the @Before which calls main
  -- or maybe not... it could call through to MainTest which uses a different DB?! like an in-memory one
- remove spring files. fix beans.xml which is in there twice! => only required in META-INF since that is then added to the archive in Main
- flyway? https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/advanced/flyway.html
- add option to use in memory db for testing. how could we mock backend calls?
  - add question to swarm forum
- do we even need modules.com.mysql.main? or just the module.xml somewhere in the resources. coz in the temp project it worked nicely when it was just under src/main/resouces/main
- see pom: spring data is working without spring? i dont want spring in here!
- test should use a different DB fraction with an in-memory db
  - use flyway so that the in mem DB will be initialised. mysql running outside won't, coz it is managed by first deployment which runs against it. ie make it the responsibility of the app to ensure the db is ready. flyway is docker safe afterall.
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
-




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

