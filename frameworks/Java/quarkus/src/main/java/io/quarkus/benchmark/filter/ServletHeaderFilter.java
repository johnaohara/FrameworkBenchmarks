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

    private static final String CONTENT_TYPE_HEADER = io.undertow.util.Headers.CONTENT_TYPE.toString();
    private static final String CONTENT_TYPE = "text/plain; charset=utf-8";
    private static final String SERVER_TYPE = "Server";
    private static final String SERVER = "Quarkus";

    private static final String HELLO = "Hello, World!";


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse myResponse = (HttpServletResponse) res;
        MyResponseRequestWrapper responseWrapper = new MyResponseRequestWrapper(myResponse);
        responseWrapper.addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        responseWrapper.addHeader(SERVER_TYPE, SERVER);
        responseWrapper.setStatus(200);

        responseWrapper.getOutputStream().println(HELLO);
        responseWrapper.getOutputStream().close();

    }

    class MyResponseRequestWrapper extends HttpServletResponseWrapper {
        public MyResponseRequestWrapper(HttpServletResponse response) {
            super(response);
        }
    }

}