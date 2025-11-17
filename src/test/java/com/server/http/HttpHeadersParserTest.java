package com.server.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeadersParserTest {

    @Test
    void testSingleHeader() throws Exception {
        String rawHeaders = "Host: localhost\r\n\r\n";
        InputStream input = new ByteArrayInputStream(rawHeaders.getBytes());
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        parser.parseHeaders(new InputStreamReader(input), request);

        assertEquals("localhost", request.getFirstHeader("Host"));
    }

    @Test
    void testMultipleHeaders() throws Exception {
        String rawHeaders = "Host: localhost\r\nUser-Agent: TestClient\r\n\r\n";
        InputStream input = new ByteArrayInputStream(rawHeaders.getBytes());
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        parser.parseHeaders(new InputStreamReader(input), request);

        assertEquals("localhost", request.getFirstHeader("Host"));
        assertEquals("TestClient", request.getFirstHeader("User-Agent"));
    }

    @Test
    void testHeadersWithSpaces() throws Exception {
        String rawHeaders = "Content-Type: text/html; charset=UTF-8\r\n\r\n";
        InputStream input = new ByteArrayInputStream(rawHeaders.getBytes());
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        parser.parseHeaders(new InputStreamReader(input), request);

        assertEquals("text/html; charset=UTF-8", request.getFirstHeader("Content-Type"));
    }

    @Test
    void testRepeatedHeader() throws Exception {
        String rawHeaders = "Set-Cookie: a=1\r\nSet-Cookie: b=2\r\n\r\n";
        InputStream input = new ByteArrayInputStream(rawHeaders.getBytes());
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        parser.parseHeaders(new InputStreamReader(input), request);

        assertEquals(2, request.getHeaderValues("Set-Cookie").size());
        assertTrue(request.getHeaderValues("Set-Cookie").contains("a=1"));
        assertTrue(request.getHeaderValues("Set-Cookie").contains("b=2"));
    }

    @Test
    void testEmptyHeadersThrows() {
        String rawHeaders = "\r\n";
        InputStream input = new ByteArrayInputStream(rawHeaders.getBytes());
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        assertDoesNotThrow(() -> parser.parseHeaders(new InputStreamReader(input), request));
    }

    @Test
    void testMalformedHeaderThrows() {
        String rawHeaders = "InvalidHeaderWithoutColon\r\n\r\n";
        InputStream input = new ByteArrayInputStream(rawHeaders.getBytes());
        HttpParser parser = new HttpParser();
        HttpRequest request = new HttpRequest();

        assertThrows(HttpParsingException.class, () -> parser.parseHeaders(new InputStreamReader(input), request));
    }
}
