package io.quarkus.benchmark.resource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/plaintext")
public class PlaintextResourceServlet extends HttpServlet {
    private static final String HELLO = "Hello, World!";

    @Override
    protected void doGet(HttpServletRequest reqest, HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().println(HELLO);
    }

}
