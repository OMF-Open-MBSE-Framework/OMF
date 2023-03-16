package com.samares_engineering.omf.omf_public_features.apiserver;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class OMFApiServer extends AbstractHandler {

    static OMFApiServer instance;
    private Map<String, RequestHandler> route;
    private Server server;

    private OMFApiServer(){
        route = new HashMap<>();
    }

    public static OMFApiServer getInstance(){
        if(instance == null)
            instance = new OMFApiServer();
        return instance;
    }



    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestHandler handler = route.get(target.replaceAll("/", ""));
        if (handler != null) {
            handler.handle(target, baseRequest, request, response);
        } else {
           RequestHandler.notFound("", target, baseRequest, request, response);
        }
    }


    public void addRoute(String path, RequestHandler handler){
        route.put(path, handler);
        ColorPrinter.status("[APISERVER] '" + path + "' route has been registered");
    }

    public void removeRoute(String path){
        route.remove(path);
        ColorPrinter.status("[APISERVER] '" + path + "' route has been removed");
    }






    //SERVER MANAGEMENT
    public void startServer(int port) {
        try {
            this.server = new Server(port);
            server.setHandler(instance);
            server.start();

            OMFLogger.getInstance().log("API Server started on port " + port, null, OMFLogLevel.INFO);
            ColorPrinter.status("API Server started on port " + port);

        }catch (Exception e){
            OMFErrorHandler.handleException(e, false);
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, false);
        }
    }






}
