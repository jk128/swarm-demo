package ch.maxant.demo.swarm;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Dependent
public class Setup {

    @Produces
    @Alternative //@Specializes
    public SomeService createService(){
        return new SomeServiceImpl();
    }
}
