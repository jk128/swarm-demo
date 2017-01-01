package ch.maxant.demo.swarm;

public class SomeServiceImpl implements SomeService {

    @Override
    public String doSomething(String s) {
        return "Hello " + s;
    }
}
