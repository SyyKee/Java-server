package com.server;

import com.server.core.ServerListenerThread;
import com.server.http.HttpResponse;
import com.server.http.Router;
import config.Configuration;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Router.addRoute("GET", "/", request -> {
            HttpResponse res = new HttpResponse();
            res.setBody("<h1>Welcome Home</h1>");
            res.addHeader("Content-Type", "text/html");
            return res;
        });

        Router.addRoute("GET", "/hello", request -> {
            HttpResponse res = new HttpResponse();
            res.setBody("<h1>Welcome to hello endpoint</h1>");
            res.addHeader("Content-Type", "text/html");
            return res;
        });

        Router.addRoute("POST", "/login", request -> {
            HttpResponse res = new HttpResponse();
            res.setBody("Login received: " + request.getBody());
            return res;
        });


        LOGGER.info("Starting server...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Server started at {}", conf.getPort());
        LOGGER.info("Server started at {}", conf.getWebroot());


        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot());
            serverListenerThread.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
