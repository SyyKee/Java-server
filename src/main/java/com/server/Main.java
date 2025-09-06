package com.server;

import config.Configuration;
import config.ConfigurationManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Using port " + conf.getPort());
        System.out.println("Using webRoot " + conf.getWebroot());

    }
}
