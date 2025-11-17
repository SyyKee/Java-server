package com.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);


    private Socket socket;

    HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputstream = null;
        try {
             inputStream = socket.getInputStream();
             outputstream = socket.getOutputStream();

            String html = "<html><head><title>One socket server page</title> </head><body> <h1>Hello this is a one socket server page</h1></body></html>";

            final String CRLF = "\n\r";

            String response =
                    "HTTP/1.1 200 OK" + CRLF + //status line -> HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
                            "Content-Length: " + html.getBytes().length + CRLF + // HEADER
                            CRLF +
                            html +
                            CRLF +
                            CRLF;


            outputstream.write(response.getBytes());


            LOGGER.info("proccess end");
            } catch(Exception e){
                LOGGER.error("Communication issues",e);
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            if (outputstream != null) {
                try {
                    outputstream.close();
                } catch (IOException e) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }

        }

    }
}
