# Java HTTP Server From Scratch

A lightweight HTTP server written 100% from scratch in Java — no frameworks, no Tomcat, no Jetty.  
This project demonstrates how an HTTP server works at the lowest level: sockets, threading, parsing, routing, and custom annotations.

## Features Implemented

### ✅ 1. Socket-based HTTP Server
- Uses `ServerSocket` to listen for connections.
- Handles each connection using worker threads (Updated to use a thread pool).
- Explicit parsing of:
  - Request line (method, path, version)
  - Headers
  - Body (Content-Length & Chunked Encoding)

### ✅ 2. Custom HTTP Parser
- Implemented manually:
  - Reads HTTP requests byte-by-byte
  - Detects CRLF patterns
  - Detects malformed headers
- Supports:
  - `GET`, `POST` (kind of 😄)
  - Chunked transfer encoding
  - Content-Length body

### ✅ 3. Routing System
Two routing modes:
1. **Programmatic Routes**
   Example:
   ```java
   Router.addRoute("GET", "/hello", req -> {
       HttpResponse res = new HttpResponse();
       res.setBody("Hello World!");
       return res;
   });
2. **Annotation-Based Routes**
   Example:
   ```java
       @Controller
    public class AnnotationController {
        @Get("/annotation")
        public HttpResponse hello(HttpRequest req) {
            HttpResponse res = new HttpResponse();
            res.setBody("<h1>Annotation says hello!</h1>");
            res.addHeader("Content-Type", "text/html");
            return res;
        }
    }

### ✅ 4. Thread Pool (ExecutorService)
  -Replaces new Thread() with a fixed pool.
  
  -Allows multiple clients to be served simultaneously.
  
 - Prevents thread explosion.
  
 - Implementation:
  
    ```java
    ExecutorService pool = Executors.newFixedThreadPool(20);
      
      while (true) {
          Socket socket = server.accept();
          pool.submit(new HttpConnectionWorker(socket));
      }

### ✅ 5. HTTP Response Builder
  Lightweight representation:

      public class HttpResponse {
          int statusCode = 200;
          Map<String, String> headers;
          String body;
      }


### How the Server Works Internally
1️⃣ Main
Loads configuration

Registers annotated controllers

Starts ServerListenerThread

2️⃣ ServerListenerThread
Accepts new TCP connections

Submits them to the thread pool

3️⃣ HttpConnectionWorker
Reads raw bytes from the socket

Parses them into HttpRequest

Looks for a matching route in Router

Executes handler (lambda or annotated method)

Builds HttpResponse

Converts to raw HTTP string

Sends back to the client

4️⃣ Router
Stores all routes in a map

Supports:

Programmatic route registration

Annotation auto-registration using reflection

5️⃣ ClassScanner
Finds all .class files inside a package

Loads them at runtime

Scans methods for annotations

## Getting Started
### Prerequisites
-Java 11 or higher

### Running the Server
  Clone the repository:

    
    git clone https://github.com/SyyKee/Java-server.git

### License
This project is licensed under the MIT License - see the LICENSE file for details.


### Key Features
- **Routing System**: Two methods of defining routes — programmatically or using annotations.
- **Thread Pool**: Efficient handling of multiple connections.
- **Custom HTTP Parser**: Handle raw HTTP requests and responses.

  

 This structure should give a comprehensive overview and guide on how to get started with your Java HTTP server! Feel free to adjust as necessary based on any specific requirements or changes in the project.

