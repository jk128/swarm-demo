package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import ch.maxant.demo.swarm.data.UserRepository;
import org.flywaydb.core.Flyway;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.util.Arrays;
import java.util.List;

import static ch.maxant.demo.swarm.CdiSetup.PRIMARY;
import static ch.maxant.demo.swarm.Main.PRIMARY_DS_JNDI_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * See src/test/resources/beans.xml which references this file.
 */
@Alternative
@Dependent
public class TestCdiSetup {

    @PersistenceUnit(unitName=PRIMARY)
    EntityManagerFactory emf;

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

    @Alternative
    @Produces
    @ConfigurationValue("auditlog.someValue")
    public String getAuditlogSomeValue() {
        return "1";
    }

    @Alternative
    @Produces
    public EntityManager getTestEm() throws Exception {

        Object ds = null;
        try{
            InitialContext ic = new InitialContext();
            ds = ic.lookup(PRIMARY_DS_JNDI_NAME);
        }catch(Exception e){
            //ok, ds remains null
        }
        if(ds == null){
            //we are running outside of Swarm, so create a test EM with H2
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2");
            String url = (String) emf.getProperties().get("hibernate.connection.url");
            EntityManager em = emf.createEntityManager();
            Flyway fw = new Flyway();
            fw.setDataSource(url, null, null); //password can be null, since its in the URL. otherwise it is returned with stars if we ask hibernate for it
            fw.migrate();
            return em;
        }else{
            return emf.createEntityManager(); //we are running inside Swarm, so build an EM
        }
    }

    public static void doInTransaction(EntityManager em, Callback f) throws Exception {
        em.getTransaction().begin();
        try{
            f.call();
            em.flush();
            em.getTransaction().commit();
            em.close(); //to ensure cache is emptied
        }catch(Exception e){
            em.getTransaction().rollback();
            throw e;
        }
    }

    public interface Callback {
        void call() throws Exception;
    }
}