package com.server.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode= 200;
    private String body;
    private Map<String, String> headers = new HashMap<>();

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
}
