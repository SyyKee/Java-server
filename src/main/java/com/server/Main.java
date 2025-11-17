package com.server;

import com.server.core.ServerListenerThread;
import config.Configuration;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

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
