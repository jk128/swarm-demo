package ch.maxant.demo.swarm.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.cdi.Eager;

import java.util.List;

@Eager //see https://stackoverflow.com/questions/41621679/spring-data-jpa-repositories-use-in-ejb-timer-causes-transactionrequiredexceptio/41636083#41636083
//extend JpaRepository or use this annotation: @RepositoryDefinition(domainClass = User.class, idClass = Long.class) ?
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByName(String name);
}