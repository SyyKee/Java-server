package com.server.core;

import com.server.http.HttpParser;
import com.server.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

            HttpParser parser = new HttpParser();
            HttpRequest request = parser.parseHttpRequest(inputStream);

            LOGGER.info("Parsed HTTP request:");
            LOGGER.info("Method: {}", request.getMethod());
            LOGGER.info("Target: {}", request.getRequestTarget());
            LOGGER.info("Headers: {}", request.getHeaders());
            LOGGER.info("Body: {}", request.getBody());
            String response =
                    "HTTP/1.1 200 OK" + CRLF + //status line -> HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
                            "Content-Length: " + html.getBytes().length + CRLF + // HEADER
                            CRLF +
                            html +
                            CRLF +
                            CRLF;


            outputstream.write(response.getBytes(StandardCharsets.US_ASCII));
            outputstream.flush();


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
