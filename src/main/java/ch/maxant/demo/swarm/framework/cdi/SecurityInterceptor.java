package ch.maxant.demo.swarm.framework.cdi;

import ch.maxant.demo.swarm.framework.jwt.Verifier;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.Collection;

@Secure
@Interceptor
public class SecurityInterceptor {

    @Context
    HttpHeaders httpHeaders;

    //read from project-stages.yml. can also be read as a system property
    @Inject
    @ConfigurationValue("maxant.security.publicKey")
    String publicKey;

    //read from project-stages.yml. can also be read as a system property
    @Inject
    @ConfigurationValue("maxant.security.realm")
    String realm; //TODO use this to fetch the public key rather than having it injected above?

    //read from project-stages.yml. can also be read as a system property
    @Inject
    @ConfigurationValue("maxant.security.application")
    String application;



    @AroundInvoke
    public Object checkSecurity(InvocationContext context) throws Exception {

        //see http://stackoverflow.com/questions/32992186/how-do-i-get-hold-of-http-request-headers-in-a-cdi-bean-thats-injected-into-a-j

        String[] allowedRoles = null;

        RolesAllowed annotation = context.getMethod().getAnnotation(RolesAllowed.class);
        if(annotation != null){
            allowedRoles = annotation.value();
        }else{
            annotation = context.getMethod().getDeclaringClass().getAnnotation(RolesAllowed.class);
            if(annotation != null) {
                allowedRoles = annotation.value();
            }
        }

        if(allowedRoles != null && allowedRoles.length > 0){
            String authorizationHeader = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
            String token = null;
            if(authorizationHeader != null){
                if(authorizationHeader.startsWith("Bearer ")){
                    token = authorizationHeader.substring(7);
                }else{
                    throw new SecurityException("Unexpected authorization type: '" + authorizationHeader + "'. Should start with 'Bearer'."); //TODO how to return a 401/403?
                }
            }

            if(token == null){
                throw new SecurityException("No bearer token found in authorization header"); //TODO how to return a 401/403?
            }

            Collection<String> roles = new Verifier(publicKey).verifyAndGetRoles(token, "resource_access." + application + ".roles");

            //is there an intersection in the roles?
            boolean hasAllowedRole = false;
            for(String allowedRole : allowedRoles){
                if(roles.contains(allowedRole)){
                    hasAllowedRole = true;
                    break;
                }
            }

            if(!hasAllowedRole){
                throw new SecurityException("User does not have one of the allowed roles"); //TODO how to return a 401/403?
            }
        }

        return context.proceed();
    }

}
