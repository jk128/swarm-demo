package ch.maxant.demo.swarm;

import ch.maxant.demo.swarm.data.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static ch.maxant.demo.swarm.TestCdiSetup.doInTransaction;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(WeldJUnit4Runner.class)
public class UserTest {

    @Inject
    EntityManager em;

    @Test
    public void testValidation_failCozOfMissingNameAndPassword() throws Exception {
        try {
            doInTransaction(em, () -> {
                User u = new User();
                u.setId(99);

                em.persist(u);
                em.flush();
                fail("why no exception");
            });
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
            Set<String> msgs = e.getConstraintViolations().stream().map(cv ->
                    cv.getPropertyPath() + " " + cv.getMessage()
            ).collect(toSet());
            assertThat(msgs, containsInAnyOrder(
                    "name may not be null",
                    "password may not be null"));
        }
    }

    @Test
    public void testValidation_failCozOfLength() throws Exception {
        try {
            doInTransaction(em, () -> {
                User u = new User();
                u.setId(99);
                u.setName("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789 TOO LONG");
                u.setPassword("asdf");

                em.persist(u);
                em.flush();
                fail("why no exception");
            });
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
            Set<String> msgs = e.getConstraintViolations().stream().map(cv ->
                    cv.getPropertyPath() + " " + cv.getMessage()
            ).collect(toSet());
            assertThat(msgs, containsInAnyOrder(
                    "name size must be between 2 and 100",
                    "password size must be between 5 and 100"));
        }
    }

    @Test
    public void testValidation_pass() throws Exception {
        doInTransaction(em, () -> {
            User u = new User();
            u.setId(99);
            u.setName("james jones");
            u.setPassword("12345");

            em.persist(u);
        });
    }
}
