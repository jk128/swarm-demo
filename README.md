# What is this?

A demo showing a Swarm application with:

- JPA
- JAX-RS
- CDI
- JTA
- EJB
- SpringData
- Security
- SSL

Based on https://github.com/wildfly-swarm/wildfly-swarm-examples.

#Useful Links

- https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/
- [System Properties](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration_properties.html)

# TODO

- update maven to do pre-integration-test stuff and delete the @Before which calls main
  -- or maybe not... it could call through to MainTest which uses a different DB?! like an in-memory one
- remove spring files. fix beans.xml which is in there twice! => only required in META-INF since that is then added to the archive in Main
- add flyway - see below
- add option to use in memory db for testing. how could we mock backend calls?
  - add question to swarm forum
- do we even need modules.com.mysql.main? or just the module.xml somewhere in the resources. coz in the temp project it worked nicely when it was just under src/main/resouces/main
- fix web.xml? deploy it at least. spring data is working without spring. i dont want spring in here!
- test should use a different DB fraction with an in-memory db
  - use flyway so that the in mem DB will be initialised. mysql running outside won't, coz it is managed by first deployment which runs against it. ie make it the responsibility of the app to ensure the db is ready. flyway is docker safe afterall.
- envers
- logback
- finish configing logging in Main properly. eg how to use own format?
- jax-rs2 client for calling other services
- gradle mail
- project-stages.yml: https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration/project_stages.html
- flyway? https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/advanced/flyway.html
- custom security
- bean validation
- others? checkout other examples
- keycloak
- why is beans.xml required? is it releated to spring data? its not necessary for the examples project.
