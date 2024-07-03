package com.samares_engineering.omf.omf_public_features.apiserver;

import com.nomagic.magicdraw.plugins.Plugin;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_public_features.apiserver.exception.APIServerException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel.INFO;


public class OMFApiServer extends AbstractHandler {

    static OMFApiServer instance;
    private final Plugin plugin;
    private Map<String, RequestHandler> route;
    private Server server;

    private OMFApiServer(Plugin plugin){
        route = new HashMap<>();
        this.plugin = plugin;
    }

    public static OMFApiServer getInstance(OMFPlugin plugin){
        if(instance == null)
            instance = new OMFApiServer((Plugin) plugin);
        return instance;
    }
    public static OMFApiServer getInstance(){
        if(instance == null)
            instance = new OMFApiServer(null);
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
        SysoutColorPrinter.status("[API_SERVER] '" + path + "' route has been registered");
    }

    public void removeRoute(String path){
        route.remove(path);
        SysoutColorPrinter.status("[APIS_ERVER] '" + path + "' route has been removed");
    }





    //*******************************************************************************
    //*************************** SERVER MANAGEMENT *********************************
    //*******************************************************************************
    public void startServer(int port) {
        try {
            this.server = new Server(port);
            server.setHandler(instance);
            server.start();

            new OMFLog().info("API Server started on port " + port).logToUiConsole(INFO).logToSystemConsole(INFO);
            getURI();
        }catch (BindException portAlreadyUsedException){
            throw new OMFCriticalException("Error while starting API server, the port " + port + " is already used." +
                    "You can change the port in the OMF Environment options then restart the server using Advanced Menu " +
                    "-> Restart API Server." +
                    "\nPlease contact the plugin: " + getPluginName() + " provider",  portAlreadyUsedException);
        }catch (Exception e){
            throw new OMFCriticalException("Error while starting API server, this will strongly impact features using API Server." +
                    "\nPlease contact the plugin: " + getPluginName() + " provider", e);
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new OMFCriticalException("Error while stopping API server, this will strongly impact features using API Server." +
                    "\nPlease try to use the dedicated Action in OMF Advanced Menu, and contact the plugin: " + getPluginName() + " provider", e);
        }
    }


    //*******************************************************************************
    //**************************** GETTER/SETTER ***********************************
    //*******************************************************************************

    public int getPort() throws APIServerException {
        if(server == null || !server.isStarted()) throw new APIServerException("API Server is not started", GenericException.ECriticality.ALERT);
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public URI getURI() throws APIServerException {
        if(server == null || !server.isStarted()) throw new APIServerException("API Server is not started", GenericException.ECriticality.ALERT);
        return server.getURI();
    }

    public String getIPAddress() throws APIServerException {
        if(server == null || !server.isStarted()) throw new APIServerException("API Server is not started", GenericException.ECriticality.ALERT);
        return server.getURI().getHost();
    }

    public String getPluginName() {
        return plugin == null? "" : plugin.getDescriptor().getName();
    }

}
