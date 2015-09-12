package com.braindrainpain.docker.httpsupport;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import java.io.IOException;

/**
 * @author Manuel Kasiske
 */
public class HttpClientService {

    private final Logger LOG = Logger.getLoggerFor(HttpClientService.class);
    private final HttpClient httpClient;
    private final GetMethod getMethod;

    public HttpClientService() {
        httpClient = new HttpClient();
        getMethod = new GetMethod();
    }

    protected HttpClientService(HttpClient httpClient, GetMethod getMethod) {
        this.httpClient = httpClient;
        this.getMethod = getMethod;
    }

    public String doGet(String url) throws IOException {
        URI uri = new URI(url, false);
        this.getMethod.setURI(uri);
        final int status = httpClient.executeMethod(getMethod);
        if (status != HttpStatus.SC_OK) {
            LOG.error("cannot connect to url " + url);
            throw new IOException("cannot connect to url " + url);
        }
        return getMethod.getResponseBodyAsString();
    }

    public void checkConnection(String url) {
        LOG.debug("Checking: '" + url + "'");
        try {
            URI uri = new URI(url, false);
            this.getMethod.setURI(uri);
            getMethod.setFollowRedirects(false);
            int status = httpClient.executeMethod(getMethod);
            if (status != HttpStatus.SC_OK) {
                LOG.error("Not ok from: '" + url + "'");
                LOG.error("status: "+status);
                throw new RuntimeException("Not ok from: '" + url + "'");
            }
        } catch (IOException e) {
            LOG.error("Error connecting to: '" + url + "'");
            throw new RuntimeException("Error connecting to: '" + url + "'");
        }
    }

}
