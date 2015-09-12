/*
The MIT License (MIT)

Copyright (c) 2014 Jan De Cooman

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.braindrainpain.docker.httpsupport;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Manuel Kasiske
 */
public class DockerApiHttpHandler implements HttpHandler{


    protected final static String DUMMY_JSON_SUCCESS_RESPONSE = "{\"name\":\"pharmacy-service\",\"tags\":[\"327\",\"347\",\"latest\"]}";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if(httpExchange.getRequestURI().toString().equals("/v2/pharmacy-service/tags/list")) {
            sendMessage(httpExchange, DUMMY_JSON_SUCCESS_RESPONSE);
        } else {
            sendError(httpExchange, "404");
        }
    }

    /**
     * Sends the given message as JSON-response
     * @param exchange content-type und server header are set
     * @param message message that is sent
     */
    private void sendMessage(HttpExchange exchange, String message) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.getResponseHeaders().add("Server", "Mock Server");
        exchange.sendResponseHeaders(200, message.length());
        sendData(message, exchange.getResponseBody());
        exchange.close();
    }

    private void sendError(HttpExchange exchange, String message) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.getResponseHeaders().add("Server", "Mock Server");
        exchange.sendResponseHeaders(404, message.length());
        sendData(message, exchange.getResponseBody());
        exchange.close();
    }

    /**
     * Sends the given data using the given output stream and closes the stream.
     * @param data data that is transferred
     * @param os output stream that is used for the data transfer
     */
    private void sendData(String data, OutputStream os) throws IOException {
        os.write(data.getBytes("UTF-8") );
        os.flush();
        os.close();
    }
}
