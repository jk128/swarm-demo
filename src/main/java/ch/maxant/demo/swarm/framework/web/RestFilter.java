package ch.maxant.demo.swarm.framework.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

/**
 * A little trick for getting the JSON Web Token out of the request header so we can use it in an interceptor.
 * See AuditInterceptor for an example of using the data extracted here which is put into a request scoped bean.
 * <br><br>
 * We can trust that the keycloak adapter already verified the token and that its good. so lets just read stuff from it here
 */
@WebFilter(urlPatterns = {"/*"})
public class RestFilter implements Filter {

    @Inject
    WebContext context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String header = ((HttpServletRequest)servletRequest).getHeader("Authorization");
        String token = null;
        String name = "unknown";
        if(header != null && header.startsWith("Bearer ")){
            token = header.substring(7);

            // since token is signed, jjst library refuses to parse it without the public signing key. we could go get it
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
        this.context.setToken(token);
        this.context.setName(name);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
