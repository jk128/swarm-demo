package ch.maxant.demo.swarm.framework.jpa.util;

import ch.maxant.demo.swarm.data.User;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class EntityManagerInitialiser {

    public static final String PRIMARY = "primary";

    @PersistenceContext(name = PRIMARY)
    EntityManager em;

    @Inject
    Logger logger;

    @PostConstruct
    public void ensureDbConnection() {
        //TODO delete me - this is an attempt to help avoid the problem that the initial request to the DB fails. in fact this proves the connection can indeed be build!
        logger.info(String.valueOf(em.createNativeQuery("select now();").getSingleResult()));

        logger.info("i can actually do EM calls too: " + em.createQuery("select u from User u", User.class).getResultList().get(0).getName());

        logger.info("EM seems to be all good");
    }
}