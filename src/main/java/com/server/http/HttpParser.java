package com.server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class HttpParser {
    private final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    // seperate and crlf hex values
    private static final int SP = 0x20; //32
    private static final int CR = 0x0D; //13
    private static final int LF = 0x0A; //10

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

        HttpRequest request = new HttpRequest();
        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            parseHeaders(reader, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        parseBody(reader, request);

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean requestTargetParsed = false;


        int _byte;
        while((_byte = reader.read()) >=0 ){
            if (_byte == CR){
                _byte = reader.read();
                if (_byte == LF){
                    LOGGER.debug("Request line VERSION to process: {}", processingDataBuffer.toString());
                    if (!methodParsed || !requestTargetParsed){
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    try {
                        request.setHttpVersion(processingDataBuffer.toString());
                    } catch (HttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    return;
                }else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            if (_byte == SP){
                // PROCESS PREVIOUS DATA
                if (!methodParsed){
                    LOGGER.debug("Request line METHOD to process: {}", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed){
                    LOGGER.debug("Request line REQ TARGET to process: {}", processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

                processingDataBuffer.delete(0, processingDataBuffer.length());

            }else {
                processingDataBuffer.append((char)_byte);
                if (!methodParsed){
                    if(processingDataBuffer.length()> HttpMethod.MAX_LENGTH){
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }

            }
        }
    }

    void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuffer lineBuffer = new StringBuffer(); // thread-safe
        boolean lastWasCR = false;  // tracks CR
        int headersCount = 0;
        final int MAX_HEADERS = 100;           // safety limit
        final int MAX_HEADER_LINE = 8192;      // max bytes per header line

        int _byte;
        while ((_byte = reader.read()) >= 0) {

            // limit header line size to prevent attacks
            if (lineBuffer.length() > MAX_HEADER_LINE) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_431_REQUEST_HEADER_FIELDS_TOO_LARGE);
            }

            if (_byte == CR) {
                lastWasCR = true;
                continue;
            } else if (_byte == LF && lastWasCR) { // CRLF detected
                lastWasCR = false;

                String line = lineBuffer.toString();
                lineBuffer.setLength(0); // reset buffer

                // empty line = end of headers
                if (line.isEmpty()) {
                    return;
                }

                // process the header line
                processHeaderLine(line, request);

                headersCount++;
                if (headersCount > MAX_HEADERS) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_431_REQUEST_HEADER_FIELDS_TOO_LARGE);
                }

            } else {
                lastWasCR = false;
                lineBuffer.append((char) _byte);
            }
        }

        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }


    void parseBody(InputStreamReader reader, HttpRequest request) throws HttpParsingException {
        String contentLengthHeader = request.getFirstHeader("Content-Length");
        String transferEncoding = request.getFirstHeader("Transfer-Encoding");

        try {
            if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {
                parseChunkedBody(reader, request);
            } else if (contentLengthHeader != null) {
                int contentLength;
                try {
                    contentLength = Integer.parseInt(contentLengthHeader);
                } catch (NumberFormatException e) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

                StringBuffer bodyBuffer = new StringBuffer(contentLength);
                char[] buffer = new char[1024];
                int remaining = contentLength;
                while (remaining > 0) {
                    int read = reader.read(buffer, 0, Math.min(buffer.length, remaining));
                    if (read == -1) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    bodyBuffer.append(buffer, 0, read);
                    remaining -= read;
                }

                request.setBody(bodyBuffer.toString());

            } else {
                request.setBody("");
            }
        } catch (IOException e) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
    }


    private void processHeaderLine(String line, HttpRequest request) throws HttpParsingException {
        int colonIndex = line.indexOf(':');
        if (colonIndex <= 0) { // no colon or empty name
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        String name = line.substring(0, colonIndex).trim();
        String value = line.substring(colonIndex + 1).trim();

        if (name.isEmpty()) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        request.addHeader(name, value); // HttpRequest must support adding headers
    }

    private void parseChunkedBody(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuffer bodyBuffer = new StringBuffer();
        while (true) {
            StringBuffer sizeLine = new StringBuffer();
            int _byte;
            boolean lastWasCR = false;

            while ((_byte = reader.read()) >= 0) {
                if (_byte == CR) {
                    lastWasCR = true;
                } else if (_byte == LF && lastWasCR) {
                    break; // end of chunk size line
                } else {
                    lastWasCR = false;
                    sizeLine.append((char)_byte);
                }
            }

            if (sizeLine.length() == 0) break;

            int chunkSize;
            try {
                chunkSize = Integer.parseInt(sizeLine.toString().trim(), 16);
            } catch (NumberFormatException e) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            if (chunkSize == 0) break; // last chunk

            char[] chunkData = new char[chunkSize];
            int read = reader.read(chunkData);
            if (read != chunkSize) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            bodyBuffer.append(chunkData);

            // skip CRLF after chunk
            reader.read(); // CR
            reader.read(); // LF
        }

        request.setBody(bodyBuffer.toString());
    }


}
