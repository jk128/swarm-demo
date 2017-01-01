package ch.maxant.demo.swarm;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Alternative
@Dependent
public class TestSetup {

    @Alternative
    @Produces
    public SomeService createMockService(){
        return new SomeService() {
            @Override
            public String doSomething(String s) {
                return "Using test bean";
            }
        };
    }
}
