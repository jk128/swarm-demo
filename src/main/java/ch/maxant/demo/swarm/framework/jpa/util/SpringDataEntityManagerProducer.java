package ch.maxant.demo.swarm.framework.jpa.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static ch.maxant.demo.swarm.framework.jpa.util.EntityManagerInitialiser.PRIMARY;

@RequestScoped
public class SpringDataEntityManagerProducer {

    @PersistenceContext(name = PRIMARY)
    EntityManager em;

    /** spring data needs this guy */
    @Produces
    public EntityManager getEm(){
        return em;
    }
}