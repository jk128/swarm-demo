//TODO move to framework package
package ch.maxant.demo.swarm;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class WeldJUnit4Runner extends BlockJUnit4ClassRunner {
   private final Class klass;
   private final Weld weld;
   private final WeldContainer container;

   public WeldJUnit4Runner(final Class klass) throws InitializationError {
       super(klass);
       this.klass = klass;
       this.weld = new Weld();

       //the following is "abnormal" in that we don't want to scan absolutely everything, because we don't want to pick
       //up all the swarm beans. and its not just the swarm beans which are the problem, there are observers in swarm
       //libraries which are called after scanning which then have certain expectations which aren't fulfilled. so
       //we want to simply skip EVERYTHING that isn't directly related to this application.
       //TODO find a better way to do this!? ie without classes, and just packages. or just leave out swarm packages?
       this.weld.disableDiscovery();
       //we have to add packages from src/main/java and src/test/java seperately since
       this.weld.addPackage(true, this.getClass()); //so that src/test/java is loaded
       this.weld.addPackage(true, Main.class); //so that src/main/java is loaded
       this.weld.addAlternative(TestCdiSetup.class); //so that alternative beans are available //TODO does it work without this, coz of beans.xml under src/test?

       this.container = weld.initialize();
   }

   @Override
   protected Object createTest() throws Exception {
       final Object test = container.instance().select(klass).get();

       return test;
   }
}