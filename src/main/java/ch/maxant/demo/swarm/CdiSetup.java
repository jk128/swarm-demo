package ch.maxant.demo.swarm;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CdiSetup {

    public static final String PRIMARY = "primary";

    @PersistenceContext(name = PRIMARY)
    EntityManager em;

    /** DO NOT USE IN NORMAL CODE - USE @PersistenceContext INSTEAD.
     * ONLY spring data needs this guy. In fact, Spring needs this in Dependent scope and injecting that into
     * say an Application Scoped bean would (could?) be a bad idea.  So DO NOT use this for injecting the
     * entity manager!  Always inject a PersistenceContext which can magically handle threads, safely.
     * (Perhaps entity managers aren't "not thread safe" per se, rather they are just very dependent on
     * a given thread because thats where the session and connection are stored via threadlocal?)*/
    @Produces
    public EntityManager getEm(){
        return em;
    }

}