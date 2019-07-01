package io.quarkus.benchmark.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@WebFilter("/*")
public class ServletHeaderFilter extends HttpFilter {

    private static final String SERVER_TYPE = "Server";
    private static final String SERVER = "Quarkus";


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse myResponse = (HttpServletResponse) res;
        MyResponseRequestWrapper responseWrapper = new MyResponseRequestWrapper(myResponse);
        responseWrapper.addHeader(SERVER_TYPE, SERVER);

        chain.doFilter(req, res);
    }

    class MyResponseRequestWrapper extends HttpServletResponseWrapper {
        public MyResponseRequestWrapper(HttpServletResponse response) {
            super(response);
        }
    }

}