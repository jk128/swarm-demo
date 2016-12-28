package ch.maxant.demo.swarm.framework.cdi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

@JwtSecured
@Interceptor
public class JwtSecuredInterceptor {

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        return ctx.proceed();
    }

    @PostConstruct
    public void onStart() {
        Logger.getLogger("SecurityLog").info(() -> "Activating");
    }

    @PreDestroy
    public void onShutdown() {
        Logger.getLogger("SecurityLog").info(() -> "Deactivating");
    }

}
