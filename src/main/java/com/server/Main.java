package com.server;

import config.Configuration;
import config.ConfigurationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Using port " + conf.getPort());
        System.out.println("Using webRoot " + conf.getWebroot());


        try {
            ServerSocket serverSocket = new ServerSocket(conf.getPort());
            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputstream = socket.getOutputStream();

            String html = "<html><head><title>One socket server page</title> </head><body> <h1>Hello this is a one socket server page</h1></body></html>";

            final String CRLF = "\n\r";

            String response =
                    "HTTP/1.1 200 OK" + CRLF + //status line -> HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
                    "Content-Length: " + html.getBytes().length + CRLF + // HEADER
                        CRLF +
                        html +
                        CRLF +
                        CRLF;

            System.out.println(response);

            outputstream.write(response.getBytes());

            inputStream.close();
            outputstream.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
