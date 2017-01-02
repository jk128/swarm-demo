package ch.maxant.demo.swarm.framework.web;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class WebContext {
    private String token;
    private String name;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
