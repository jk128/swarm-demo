package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;
import ch.maxant.demo.swarm.framework.cdi.Audited;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static ch.maxant.demo.swarm.CdiSetup.PRIMARY;

@Audited //works on cdi bean too
public class UserService {

    @Inject
    UserRepository userRepository;

    @PersistenceContext(name = PRIMARY)
    EntityManager em;

    public List<User> getAll() {
        System.out.println("UserRepo instance: " + System.identityHashCode(userRepository) + "\t\tUserService instance: " + System.identityHashCode(this) + "\t\tEM instance: " + System.identityHashCode(em));
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

    public void save(User user) {
        em.persist(user);
    }
}
