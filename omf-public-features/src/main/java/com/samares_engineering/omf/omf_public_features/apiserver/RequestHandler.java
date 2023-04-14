package com.samares_engineering.omf.omf_public_features.apiserver;

import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class RequestHandler{
    public abstract void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response);

    //404
    public static Object notFound(String message, String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        baseRequest.setHandled(true);
        try {
            response.getWriter().println("<h1>404 - Page Not Found</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public void successAnswer(String message, String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(message);
        baseRequest.setHandled(true);
    }
}