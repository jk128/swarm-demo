package ch.maxant.demo.swarm.data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "T_USER")
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "select u from User u"),
        @NamedQuery(name = "User.findByCreatedBetween", query = "select u from User u where u.created > :from and u.created < :to")
})
public class User {

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
    public void prePersist(){
        this.created = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
