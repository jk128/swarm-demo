package ch.maxant.demo.swarm.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//or use this annotation: @RepositoryDefinition(domainClass = User.class, idClass = Long.class)
//@Eager // not working, but see https://stackoverflow.com/questions/41621679/spring-data-jpa-repositories-use-in-ejb-timer-causes-transactionrequiredexceptio/41636083#41636083
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByName(String name);
}