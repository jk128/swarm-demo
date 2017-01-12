package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import static ch.maxant.demo.swarm.TestCdiSetup.doInTransaction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(WeldJUnit4Runner.class)
public class UserTest {

    @Inject
    EntityManager em;

    @Test
    public void testValidation_failCozOfName() throws Exception {
        doInTransaction(em, ()-> {
            User u = new User();
            u.setId(99);

            try{
                em.persist(u);
                em.flush();
                fail("why no exception");
                //TODO weird, but this dont fail...
            }catch(ValidationException e){
                assertEquals("", e.getMessage());
            }
        });
    }
}
