package com.server.controllers;

import com.server.annotations.Controller;
import com.server.annotations.Get;
import com.server.http.HttpRequest;
import com.server.http.HttpResponse;

@Controller
public class AnnotationController {
    @Get("/annotation")
    public HttpResponse hello(HttpRequest req) {
        HttpResponse res = new HttpResponse();
        res.setBody("<h1>Annotation says hello </h1>");
        res.addHeader("Content-Type", "text/html");
        return  res;
    }

}
