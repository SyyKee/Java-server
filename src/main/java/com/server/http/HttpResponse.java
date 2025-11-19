package com.server.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode= 200;
    private String body;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse() {}

    public HttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;

    }
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public static HttpResponse ok(String body) {
        HttpResponse r = new HttpResponse(200, body);
        r.addHeader("Content-Type", "text/plain");
        return r;
    }

    public static HttpResponse html(String html) {
        HttpResponse r = new HttpResponse(200, html);
        r.addHeader("Content-Type", "text/html; charset=utf-8");
        return r;
    }

    public static HttpResponse notFound(String msg) {
        HttpResponse r = new HttpResponse(404, msg);
        r.addHeader("Content-Type", "text/plain");
        return r;
    }

    public static HttpResponse error(String msg) {
        HttpResponse r = new HttpResponse(500, msg);
        r.addHeader("Content-Type", "text/plain");
        return r;
    }
}
