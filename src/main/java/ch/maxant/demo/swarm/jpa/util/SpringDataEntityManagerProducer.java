package ch.maxant.demo.swarm.jpa.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class SpringDataEntityManagerProducer {

    @PersistenceContext(name = "primary")
    EntityManager em;

    /** spring data needs this guy */
    @Produces
    public EntityManager getEm(){
        return em;
    }
}