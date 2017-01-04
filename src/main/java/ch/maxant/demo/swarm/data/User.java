package ch.maxant.demo.swarm.data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "T_USER")
@NamedQueries({
        @NamedQuery(name = User.NQFindAll.NAME, query = User.NQFindAll.QUERY),
        @NamedQuery(name = User.NQFindByName.NAME, query = User.NQFindByName.QUERY),
        @NamedQuery(name = User.NQFindByCreatedBetween.NAME, query = User.NQFindByCreatedBetween.QUERY)
})
public class User {

    public static class NQFindAll {
        public static final String NAME = "User.findAll";
        static final String QUERY = "select u from User u";
    }

    public static class NQFindByName {
        public static final String NAME = "User.findByName";
        public static final String PARAM_NAME = "name";
        static final String QUERY = "select u from User u where u.name = :" + PARAM_NAME;
    }

    public static class NQFindByCreatedBetween {
        public static final String NAME = "User.findByCreatedBetween";
        public static final String PARAM_FROM = "from";
        public static final String PARAM_TO = "to";
        static final String QUERY = "select u from User u where u.created > :" + PARAM_FROM + " and u.created < :" + PARAM_TO;
    }

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "PASSWD", length = 100)
    private String password;

    @Column(name = "CREATED")
    private LocalDate created;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    Set<Role> roles;

    @PrePersist
    public void prePersist() {
        this.created = LocalDate.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreated() {
        return created;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
