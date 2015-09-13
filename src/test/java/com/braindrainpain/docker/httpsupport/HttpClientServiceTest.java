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
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Manuel Kasiske
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpClientServiceTest {

    @Mock
    private HttpClient httpClient;

    @Spy
    private GetMethod getMethod;

    @InjectMocks
    private HttpClientService httpClientService;

    public static final String URL = "http://www.anyDomain.de";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        httpClientService = new HttpClientService(httpClient, getMethod);
    }

    @Test
    public void testDoGetWithEmptyUrlShouldFail() {
        try {
            httpClientService.doGet("");
            fail("IOException expected if url is empty");
        } catch (IOException e) {
            Assert.assertEquals("cannot connect to url ", e.getMessage());
        }
    }

    @Test
    public void testDoGetIsAnswering() throws Exception {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(200);
        String answer = "spy answered";
        when(getMethod.getResponseBodyAsString()).thenReturn(answer);
        Assert.assertEquals(answer, httpClientService.doGet(URL));
    }

    @Test
    public void testDoGetThrowsExceptionOnStatus404() {
        String answer = "spy answered";
        try {
            when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(404);
            when(getMethod.getResponseBodyAsString()).thenReturn(answer);
            httpClientService.doGet(URL);
            fail("IOException expected if status is 404");
        } catch (IOException e) {
            Assert.assertEquals("cannot connect to url "+URL, e.getMessage());
        }
    }

    @Test
    public void testCheckConnection() throws IOException {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(200);
        httpClientService.checkConnection(URL);
    }

    @Test
    public void testCheckConnectionThrowsExceptionOnStatus404() throws IOException {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(404);
        try {
            httpClientService.checkConnection(URL);
            fail("RuntimeException expected if status is 404");
        } catch (RuntimeException e) {
            Assert.assertEquals("Not ok from: '"+URL+"'", e.getMessage());
        }
    }

    @Test
    public void testCheckConnectionThrowsIOExceptionOnExecuteMethodShouldThrowRuntimeException() throws IOException {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenThrow(new IOException());
        try {
            httpClientService.checkConnection(URL);
            fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("Error connecting to: '"+URL+"'", e.getMessage());
        }
    }

}



