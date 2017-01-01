package ch.maxant.demo.swarm.framework.web;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class WebContext {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
