package ch.maxant.demo.swarm.framework.web;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A little trick for getting the JSON Web Token out of the request header so we can use it in an interceptor. See AuditInterceptor for an example.
 */
@WebFilter(urlPatterns = {"/*"})
public class RestFilter implements Filter {

    @Inject
    WebContext context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String header = ((HttpServletRequest)servletRequest).getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            header = header.substring(7);
        }
        this.context.setToken(header);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
