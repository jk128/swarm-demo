package ch.maxant.demo.swarm.framework.cdi;

import ch.maxant.demo.swarm.framework.web.WebContext;
import org.slf4j.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.time.Duration;
import java.time.Instant;

@Audited
@Interceptor
public class AuditInterceptor {

    //just an example of how we can use the token. in reality, the user would be logged in and we could just inject the security context. TODO get that working. see https://groups.google.com/forum/#!topic/wildfly-swarm/G_-uGRUeiVo

    //since neither @Context HttpHeaders httpHeaders; nor @Context private HttpServletRequest servletRequest; works here, we use a web filter to access the token:
    @Inject
    WebContext webContext;

    //read from project-stages.yml. can also be read as a system property
    @Inject
    @ConfigurationValue("auditlog.someValue")
    String someValue;

    @Inject
    Logger logger;

    @AroundInvoke
    public Object doit(InvocationContext context) throws Exception {

        String name = webContext.getName();

        Instant start = Instant.now();
        try {
            return context.proceed();
        } finally {
            logger.info("AUDIT: {} just called {}#{} in {}", name, context.getMethod().getDeclaringClass().getSimpleName(), context.getMethod().getName(), Duration.between(start, Instant.now()));
        }
    }

}
