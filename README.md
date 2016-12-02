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

See also https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/content/

# TODO

- fix web.xml? deploy it at least. spring data is working without spring. i dont want spring in here!
- where does ExampleDS come from?

        java:jboss/datasources/ExampleDS

- why does initial request fail with connection problem?

        Caused by: org.hibernate.exception.GenericJDBCException: Unable to acquire JDBC Connection

- gradle mail
- flyway?
- custom security
- bean validation
- others? checkout other examples
- why is beans.xml required? is it releated to spring data? its not necessary for the examples project.
