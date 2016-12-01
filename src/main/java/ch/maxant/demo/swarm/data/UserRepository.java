package ch.maxant.demo.swarm.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//or use this annotation: @RepositoryDefinition(domainClass = User.class, idClass = Long.class)
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByName(String name);
}