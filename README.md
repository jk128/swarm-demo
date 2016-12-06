# What is this?

A demo showing a Swarm application with:

- JPA
- JAX-RS
- CDI
- JTA
- EJB
- SpringData
- Security

Based on https://github.com/wildfly-swarm/wildfly-swarm-examples.

#Useful Links

- https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/
- [System Properties](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration_properties.html)

# TODO

- fix web.xml? deploy it at least. spring data is working without spring. i dont want spring in here!
- test should use a different DB fraction with an in-memory db
  - use flyway so that the in mem DB will be initialised. mysql running outside won't, coz it is managed by first deployment which runs against it. ie make it the responsibility of the app to ensure the db is ready. flyway is docker safe afterall.
- envers
- logback
- finish configing logging in Main properly. eg how to use own format?
- why does initial request fail with connection problem?

        Caused by: org.hibernate.exception.GenericJDBCException: Unable to acquire JDBC Connection

- jax-rs2 client for calling other services
- gradle mail
- project-stages.yml: https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/configuration/project_stages.html
- flyway? https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/advanced/flyway.html
- custom security
- bean validation
- others? checkout other examples
- keycloak
- why is beans.xml required? is it releated to spring data? its not necessary for the examples project.
