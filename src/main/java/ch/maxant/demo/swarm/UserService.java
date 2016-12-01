package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

//@ApplicationScoped - injection didnt work. deleteme
@Stateless
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
