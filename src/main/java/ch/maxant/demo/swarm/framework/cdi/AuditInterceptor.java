package ch.maxant.demo.swarm.framework.cdi;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.time.Duration;
import java.time.Instant;

@Audited
@Interceptor
public class AuditInterceptor {

    //just an example of how we can use the token. in reality, the user would be logged in and we could just inject the security context.
    @Context
    HttpHeaders httpHeaders;

    //read from project-stages.yml. can also be read as a system property
    @Inject
    @ConfigurationValue("auditlog.someValue")
    String someValue;

    @Inject
    Logger logger;

    @AroundInvoke
    public Object doit(InvocationContext context) throws Exception {

        String name = "unknown";

        if (httpHeaders != null) {
            String authorizationHeader = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
            String token = null;
            if (authorizationHeader != null) {
                if (authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);
                } else {
                    throw new RuntimeException("Unexpected authorization type: '" + authorizationHeader + "'. Should start with 'Bearer'.");
                }
            }

            //we can trust that the keycloak adapter already verified the token and that its good. so lets just read stuff from it here
            if (token != null) {
                Claims body = Jwts.parser().parseClaimsJws(token).getBody();
                name = body.get("given_name", String.class);
                name += " " + body.get("family_name", String.class);
            }
        }

        Instant start = Instant.now();
        try {
            return context.proceed();
        } finally {
            logger.info("AUDIT: {} just called {}#{} in {}", name, context.getMethod().getDeclaringClass().getSimpleName(), context.getMethod().getName(), Duration.between(start, Instant.now()));
        }
    }

}
