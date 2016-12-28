package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;
import ch.maxant.demo.swarm.framework.cdi.JwtSecured;
import ch.maxant.demo.swarm.framework.cdi.JwtSecuredInterceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;

@ApplicationScoped
@JwtSecured(realm = "tullia", application = "app")
@Interceptors(JwtSecuredInterceptor.class)
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
