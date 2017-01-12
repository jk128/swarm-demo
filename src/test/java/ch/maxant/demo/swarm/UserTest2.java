package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserTest2 {

    public static void main(String[] args) throws Exception {
        new UserTest2().testValidation_failCozOfName();
    }

    @Test
    public void testValidation_failCozOfName() throws Exception {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2");
/*
        String url = (String) emf.getProperties().get("hibernate.connection.url");
        Flyway fw = new Flyway();
        fw.setDataSource(url, null, null); //password can be null, since its in the URL. otherwise it is returned with stars if we ask hibernate for it
        fw.migrate();
*/
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User u = new User();
        u.setId(99);
        em.persist(u);
        em.flush();
        em.getTransaction().commit();
        em.close();
        emf.close();
    }
}
