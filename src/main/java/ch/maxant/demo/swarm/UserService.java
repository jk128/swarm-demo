package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;
import ch.maxant.demo.swarm.framework.cdi.Secure;
import org.jboss.security.annotation.SecurityDomain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
@Secure
@SecurityDomain("domain") //TODO required?
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
