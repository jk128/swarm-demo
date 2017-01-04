package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;
import ch.maxant.demo.swarm.framework.cdi.Audited;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
@Audited //works on cdi bean too
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    EntityManager em;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllUsingEntityManager(){
        return em.createNamedQuery(User.NQFindAll.NAME).getResultList();
    }

    public User findUserByComparison(User user) {
        if(user.getId() != null){
            return em.find(User.class, user.getId());
        }else if(user.getName() != null){
            return em.createNamedQuery(User.NQFindByName.NAME, User.class)
                    .setParameter(User.NQFindByName.PARAM_NAME, user.getName())
                    .getSingleResult();
        } else {
            throw new RuntimeException("you must supply either ID or name");
        }
    }
}
