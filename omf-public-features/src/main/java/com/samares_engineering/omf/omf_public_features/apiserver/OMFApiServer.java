package com.samares_engineering.omf.omf_public_features.apiserver;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import com.samares_engineering.omf.omf_public_features.apiserver.exception.APIServerException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
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



    //*******************************************************************************
    //************************** ROOTING MANAGEMENT *********************************
    //*******************************************************************************

    public void addRoute(String path, RequestHandler handler){
        route.put(path, handler);
        ColorPrinter.status("[API_SERVER] '" + path + "' route has been registered");
    }

    public void removeRoute(String path){
        route.remove(path);
        ColorPrinter.status("[APIS_ERVER] '" + path + "' route has been removed");
    }





    //*******************************************************************************
    //*************************** SERVER MANAGEMENT *********************************
    //*******************************************************************************
    public void startServer(int port) {
        try {
            this.server = new Server(port);
            server.setHandler(instance);
            server.start();

            OMFLogger.getInstance().log("API Server started on port " + port, null, OMFLogLevel.INFO);
            ColorPrinter.status("API Server started on port " + port);

        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("Error while starting API server, this will strongly impact features using API Server." +
                    "\nPlease contact the plugin: " + " provider", e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            OMFErrorHandler.handleException(new FeatureException("Error while stopping API server, this will strongly impact features using API Server." +
                    "\nPlease try to use the dedicated Action in OMF Advanced Menu, and contact the plugin: " + " provider", e, GenericException.ECriticality.CRITICAL), false);
        }
    }


    //*******************************************************************************
    //**************************** GETTER/SETTER ***********************************
    //*******************************************************************************

    public int getPort() throws APIServerException {
        if(server != null || !server.isStarted()) throw new APIServerException("API Server is not started", GenericException.ECriticality.ALERT);
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public String getURL(){
        try {
            return "http://" + getIPAdress() + getPort();
        } catch (APIServerException e) {
            return "http://localhost:0";
        }
    }

    public String getIPAdress() throws APIServerException {
        if(server != null || !server.isStarted()) throw new APIServerException("API Server is not started", GenericException.ECriticality.ALERT);
        return  ((ServerConnector) server.getConnectors()[0]).getHost();
    }


}
