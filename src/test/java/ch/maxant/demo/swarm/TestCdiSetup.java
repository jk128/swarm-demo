package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * See src/test/resources/beans.xml which references this file.
 */
@Alternative
@Dependent
public class TestCdiSetup {

    @Alternative
    @Produces
    public UserRepository getMockUserRepository(){

        UserRepository repo = mock(UserRepository.class);

        User u1 = new User();
        u1.setId(1);
        u1.setName("U1");

        User u2 = new User();
        u2.setId(2);
        u2.setName("U2");

        List<User> users = Arrays.asList(u1, u2);

        when(repo.findAll()).thenReturn(users);

        return repo;
    }
}