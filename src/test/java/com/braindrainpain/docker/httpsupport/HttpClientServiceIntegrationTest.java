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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Manuel Kasiske
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpClientServiceIntegrationTest {

    private HttpClient httpClient;
    private GetMethod getMethod;
    private HttpClientService httpClientService;
    private DockerApiHttpHandler handler;
    private WebMock webMock;

    @Before
    public void setUp() {
        handler = new DockerApiHttpHandler();
        webMock = new WebMock(handler, 5000);
        httpClient = new HttpClient();
        getMethod = new GetMethod();
        httpClientService = new HttpClientService(httpClient, getMethod);

        webMock.start();
    }

    @After
    public void tearDown() {
        webMock.stop();
    }

    @Test
    public void testRequestDockerApiWithoutContextShouldReturn404() throws IOException {
        try {
            httpClientService.checkConnection("http://localhost:5000/");
            fail();
        } catch (RuntimeException e) {}
        assertEquals("404 page not found", getMethod.getResponseBodyAsString());
        assertEquals(404, getMethod.getStatusCode());
    }

    @Test
    public void testRequestDockerApiWithRightContextShouldReturnJsonResponseWithStatus200() throws IOException {
        httpClientService.checkConnection("http://localhost:5000/v2/pharmacy-service/tags/list");
        assertEquals(DockerApiHttpHandler.DUMMY_JSON_SUCCESS_RESPONSE, getMethod.getResponseBodyAsString());
        assertEquals(200, getMethod.getStatusCode());
    }

}
