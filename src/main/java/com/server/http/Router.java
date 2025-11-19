package com.server.http;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private static final Map<String, RouteHandler> routes = new HashMap();

    public static void addRoute(String method, String path, RouteHandler handler){
        routes.put(method.toUpperCase() + " " + path, handler);
    }

    public static RouteHandler getHandler(String method, String path){
        return routes.get(method.toUpperCase() + " " + path);
    }
}
