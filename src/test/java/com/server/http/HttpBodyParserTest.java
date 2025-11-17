package com.server.http;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpBodyParserTest {
    @Test
    void testContentLengthBody() throws Exception {
        String rawRequest = "POST / HTTP/1.1\r\nContent-Length: 11\r\n\r\nHello World";
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        parser.parseHeaders(new InputStreamReader(new ByteArrayInputStream("Content-Length: 11\r\n\r\n".getBytes())), request);
        parser.parseBody(new InputStreamReader(new ByteArrayInputStream("Hello World".getBytes())), request);

        assertEquals("Hello World", request.getBody());
    }

    @Test
    void testChunkedBody() throws Exception {
        String chunkedData = "4\r\nWiki\r\n5\r\npedia\r\n0\r\n\r\n";
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();
        request.addHeader("Transfer-Encoding", "chunked");

        parser.parseBody(new InputStreamReader(new ByteArrayInputStream(chunkedData.getBytes())), request);

        assertEquals("Wikipedia", request.getBody());
    }

    @Test
    void testNoBody() throws Exception {
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        parser.parseBody(new InputStreamReader(new ByteArrayInputStream(new byte[0])), request);

        assertEquals("", request.getBody());
    }
}
