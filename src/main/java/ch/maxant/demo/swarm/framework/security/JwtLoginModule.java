package ch.maxant.demo.swarm.framework.security;

import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import javax.security.auth.login.LoginException;
import java.security.acl.Group;

//TODO
//based on AnonLoginModule
public class JwtLoginModule extends UsernamePasswordLoginModule {

    protected Group[] getRoleSets() throws LoginException {
        SimpleGroup roles = new SimpleGroup("Roles");
        Group[] roleSets = {roles};
        return roleSets;
    }

    /**
     * Overriden to return null.
     * @return null always
     */
    protected String getUsersPassword() throws LoginException {
        return null;
    }
}
