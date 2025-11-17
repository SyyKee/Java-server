package com.server.http;

public enum HttpStatusCode {
//    CLIENT ERRORS
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_401_METHOD_NOT_ALLOWED(401, "Method Not Allowed"),
    CLIENT_ERROR_414_BAD_REQUEST(424, "URI Too Long"),

    //SERVER ERRORS
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    CLIENT_ERROR_431_REQUEST_HEADER_FIELDS_TOO_LARGE(431,"Header Fields Too Large"),;

    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int statusCode, String message) {
        this.STATUS_CODE = statusCode;
        this.MESSAGE = message;
    }
}
