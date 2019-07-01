package io.quarkus.benchmark.resource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/plaintext")
public class PlaintextResourceServlet extends HttpServlet {
    private static final String HELLO = "hello, world!";
    private static final String CONTENT_TYPE_HEADER = io.undertow.util.Headers.CONTENT_TYPE.toString();
    private static final String CONTENT_TYPE = "text/plain; charset=utf-8";

    @Override
    protected void doGet(HttpServletRequest reqest, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        response.getWriter().println(HELLO);
    }

}
