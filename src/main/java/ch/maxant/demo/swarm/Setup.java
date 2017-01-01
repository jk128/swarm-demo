package ch.maxant.demo.swarm;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class Setup {

    @Produces
    public SomeService createService(){
        return new SomeServiceImpl();
    }
}
