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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.thoughtworks.go.plugin.api.logging.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author Manuel Kasiske
 */
public class WebMock {

    private final Logger LOG = Logger.getLoggerFor(HttpClientService.class);
    private HttpServer server;

    public WebMock(final DockerApiHttpHandler handler, final Integer port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port),0);
        } catch (IOException e) {
            LOG.error("can't create http server");
        }
        HttpContext context = server.createContext("/", handler);

//        context.getFilters().add(new ParameterFilter());
        server.setExecutor(Executors.newCachedThreadPool());
        LOG.info("Server created");


    }

    public void start() {
        server.start();
        LOG.info("Server started");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            LOG.info("Server stopped");
        }
    }
}
