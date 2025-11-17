package com.server.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {

    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass(){
        httpParser = new HttpParser();
    }


    @Test
    void parseHttpRequest() {
        HttpRequest request = null;
        try {
            request = httpParser.parseHttpRequest(
                    generateGETTest()
            );
        } catch (HttpParsingException e) {
            fail();
        }

        assertNotNull(request);
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals("HTTP/1.1", request.getOriginalHttpVersion());
        assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
    }

    @Test
    void parseHttpRequestInvalid() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateInvalidTest()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithLongMethodeName() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateInvalidTestWithLongMethodeName()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithMultipleItemsInRequestLine() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateInvalidTestWithMultipleItemsInRequestLine()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithEmptyRequestLine() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateInvalidTestWithEmptyRequestLine()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithoutLineFeed() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateInvalidTestWithoutLineFeed()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithBadHttpVersion() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadHttpVersionTest()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithUnsupporetedVersionTest() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadHttpUnsupporetedVersionTest()
            );
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED, e.getErrorCode());
        }

    }

    @Test
    void parseHttpRequestInvalidTestWithSupporetedVersionTest() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadHttpSupporetedVersionTest()
            );
            assertNotNull(request);
            assertEquals(request.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
            assertEquals(request.getOriginalHttpVersion(), "HTTP/1.2");
        } catch (HttpParsingException e) {
            fail();
        }

    }


    private InputStream generateGETTest() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                    StandardCharsets.US_ASCII
        ));

        return inputStream;
    }

    private InputStream generateInvalidTest() {
        String rawData = "gET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateInvalidTestWithLongMethodeName() {
        String rawData = "GEEEEEEEEEEEEEEEEEEEEEEEEEEEEET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateInvalidTestWithMultipleItemsInRequestLine() {
        String rawData = "GET / AAAAA HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateInvalidTestWithEmptyRequestLine() {
        String rawData = "\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateInvalidTestWithoutLineFeed() {
        String rawData = "GET / AAAAA HTTP/1.1\r" + //<------ no line feed
                "Host: localhost:8080\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateBadHttpVersionTest() {
        String rawData = "GET / HTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateBadHttpUnsupporetedVersionTest() {
        String rawData = "GET / HTTP/2.2\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }

    private InputStream generateBadHttpSupporetedVersionTest() {
        String rawData = "GET / HTTP/1.2\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en,ar;q=0.5\r\n" +
                "Cookie: Idea-857736a3=9aea5e6b-0428-43bc-9b48-a54f2844c198\r\n" +
                "\r\n";

        InputStream inputStream =  new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                ));

        return inputStream;
    }



}
