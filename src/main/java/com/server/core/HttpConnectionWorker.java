package com.server.core;

import com.server.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class HttpConnectionWorker implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorker.class);


    private Socket socket;

    HttpConnectionWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputstream = null;
        try {
            try {
                inputStream = socket.getInputStream();
            } catch (SocketException e) {
                LOGGER.warn("Client disconnected before processing");
                return;
            }

            outputstream = socket.getOutputStream();

            String html = "<html><head><title>One socket server page</title> </head><body> <h1>Hello this is a one socket server page</h1></body></html>";

            final String CRLF = "\n\r";

            HttpParser parser = new HttpParser();
            HttpRequest request = parser.parseHttpRequest(inputStream);

            RouteHandler handler = Router.getHandler(request.getMethod().name(), request.getRequestTarget());
            HttpResponse response = new HttpResponse();

            LOGGER.info("Parsed HTTP request:");
            LOGGER.info("Method: {}", request.getMethod());
            LOGGER.info("Target: {}", request.getRequestTarget());
            LOGGER.info("Headers: {}", request.getHeaders());
            LOGGER.info("Body: {}", request.getBody());
//            String response =
//                    "HTTP/1.1 200 OK" + CRLF + //status line -> HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
//                            "Content-Length: " + html.getBytes().length + CRLF + // HEADER
//                            CRLF +
//                            html +
//                            CRLF +
//                            CRLF;

            if (handler != null) {
                response = handler.handle(request);
            }else {
                response.setStatusCode(404);
                response.setBody("<h1>404 Not Found</h1>");
                response.addHeader("Content-Type", "text/html");
            }

            StringBuffer raw = new StringBuffer();
            raw.append("HTTP/1.1 ").append(response.getStatusCode()).append(" OK\r\n");
            response.getHeaders().forEach((k,v) -> raw.append(k).append(": ").append(v).append("\r\n"));
            raw.append("Content-Length: ").append(response.getBody().getBytes().length).append("\r\n");
            raw.append("\r\n");
            raw.append(response.getBody());

            outputstream.write(raw.toString().getBytes(StandardCharsets.US_ASCII));
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
