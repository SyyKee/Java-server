package com.server.http;

import com.server.annotations.Controller;
import com.server.annotations.Get;
import com.server.annotations.Post;
import com.server.exception.HttpConfigurationException;
import utils.ClassScanner;

import java.lang.reflect.Method;
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

    public static void registerAnnotatedControllers(String basePackage) {
        try {
            for (Class<?> cls : ClassScanner.getClasses(basePackage)) {

                if (!cls.isAnnotationPresent(Controller.class))
                    continue;

                Object controllerInstance = cls.getConstructor().newInstance();

                for (Method m : cls.getDeclaredMethods()) {

                    if (m.isAnnotationPresent(Get.class)) {
                        String path = m.getAnnotation(Get.class).value();

                        addRoute("GET", path, req -> {
                            try {
                                return (HttpResponse) m.invoke(controllerInstance, req);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                    }

                    if (m.isAnnotationPresent(Post.class)) {
                        String path = m.getAnnotation(Post.class).value();

                        addRoute("POST", path, req -> {
                            try {
                                return (HttpResponse) m.invoke(controllerInstance, req);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to register annotated controllers", e);
        }
    }
}
