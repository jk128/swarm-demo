package ch.maxant.demo.swarm;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import static ch.maxant.demo.swarm.data.Roles.ADMIN;

@Stateless
@RolesAllowed(ADMIN)
public class AdminService {

    @Resource
    SessionContext ctx;

    public String getLoggedInUserAndVerifyRole() {
        if(!ctx.isCallerInRole(ADMIN)){
            throw new SecurityException("User is not in role " + ADMIN);
        }
        return ctx.getCallerPrincipal().getName();
    }
}
