package ch.maxant.demo.swarm.framework.cdi;

import ch.maxant.demo.swarm.framework.web.WebContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Audited
@Interceptor
public class AuditInterceptor {

    //just an example of how we can use the token. in reality, the user would be logged in and we could just inject the security context.

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

        String name = "unknown";

        String token = webContext.getToken();

        //we can trust that the keycloak adapter already verified the token and that its good. so lets just read stuff from it here
        if (token != null) {

            //since token is signed, jjst library refuses to parse it without the public signing key. we could go get it
            // but since we trust that the keycloak adapter already verified the token, we're only interested in the body.
            // so lets use jackson to parse it.
            String[] split = token.split("\\.");
            byte[] bytes = Base64.getDecoder().decode(split[1]);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = (ObjectNode) mapper.readTree(bytes);
            name = body.get("given_name").asText();
            name += " " + body.get("family_name").asText();
            name += " (" + body.get("preferred_username").asText() + ")";

            //if we didn't trust the token, we could check it against the signature:
            //http://stackoverflow.com/questions/28294663/how-to-convert-from-string-to-publickey
            //byte[] bytes = Base64.getDecoder().decode(publicKey); //publicKey comes from https://tullia.maxant.ch/auth/realms/tullia/.well-known/uma-configuration
            //X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            //KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //PublicKey pubKey = keyFactory.generatePublic(keySpec);
            //Claims body = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(token).getBody(); SEE how "setSigningKey" is set?
        }

        Instant start = Instant.now();
        try {
            return context.proceed();
        } finally {
            logger.info("AUDIT: {} just called {}#{} in {}", name, context.getMethod().getDeclaringClass().getSimpleName(), context.getMethod().getName(), Duration.between(start, Instant.now()));
        }
    }

}
