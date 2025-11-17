package com.server.http;

import java.util.*;

public class HttpRequest extends HttpMessage {

    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion;
    private HttpVersion bestCompatibleHttpVersion;
    private final Map<String, List<String>> headers = Collections.synchronizedMap(new HashMap<>());
    private final StringBuffer body = new StringBuffer();



    HttpRequest(){

    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleHttpVersion;
    }

    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod method: HttpMethod.values()){
            if (methodName.equals(method.name())){
                this.method = method;
                return;
            }
        }
        throw new HttpParsingException(
                HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }

    public void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.isEmpty()){
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget = requestTarget;
    }

    public void setHttpVersion(String originalHttpVersion) throws HttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.bestCompatibleHttpVersion == null){
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    public void addHeader(String name, String value) {
        if (name == null || name.isEmpty()) return;
        if (value == null) value = "";

        String key = name.toLowerCase();

        synchronized (headers) { // ensure thread-safety for the list operations
            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
    }

    public List<String> getHeaderValues(String name) {
        if (name == null) return null;
        return headers.get(name.toLowerCase());
    }

    public String getFirstHeader(String name) {
        List<String> values = getHeaderValues(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void appendToBody(String content) {
        if (content != null) {
            body.append(content); // thread-safe
        }
    }

    public void setBody(String content) {
        synchronized (body) {
            body.setLength(0);
            if (content != null) body.append(content);
        }
    }

    public String getBody() {
        synchronized (body) {
            return body.toString();
        }
    }

    public StringBuffer getBodyBuffer() {
        return body; // if you want direct thread-safe access
    }
}
