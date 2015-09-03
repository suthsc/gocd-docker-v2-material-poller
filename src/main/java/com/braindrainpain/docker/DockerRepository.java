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
package com.braindrainpain.docker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;

/**
 * Docker Repository connector.
 *
 * @author Jan De Cooman
 */
public class DockerRepository extends HttpSupport {

    final private static Logger LOG = Logger.getLoggerFor(DockerRepository.class);

    final private RepositoryConfiguration repositoryConfiguration;

    private DockerRepository(final RepositoryConfiguration repositoryConfiguration) {
        this.repositoryConfiguration = repositoryConfiguration;
    }

    public static DockerRepository getInstance(final RepositoryConfiguration repositoryConfiguration) {
        return new DockerRepository(repositoryConfiguration);
    }

    public DockerTag getLatestRevision(final PackageConfiguration packageConfiguration) {
        String tagName = packageConfiguration.get(Constants.TAG).getValue();
        JsonArray jsonTags = this.allTags(packageConfiguration);
        return this.getLatestTag(jsonTags, tagName);
    }

    private DockerTag getLatestTag(final JsonArray tags, final String tagName) {
        DockerTag result = null;
        for (Iterator<JsonElement> iterator = tags.iterator(); iterator.hasNext(); ) {
            String value = iterator.next().getAsString();
            LOG.info("looking for tag named: "+tagName);
            LOG.info("value: " + value);
            if (tagName.equals(value)) {
                result = new DockerTag(tagName, value);
                LOG.info("Found tag: " + result);
            }
        }
        return result;
    }

    /**
     * Call the Docker API.
     * 
     * @param packageConfiguration
     * @return 
     */
    private JsonArray allTags(final PackageConfiguration packageConfiguration) {
        JsonArray result = null;
        HttpClient client = super.getHttpClient();

        LOG.info("allTags is called");

        String repository = MessageFormat.format(DockerAPI.V2.getUrl(),
                repositoryConfiguration.get(Constants.REGISTRY).getValue(),
                packageConfiguration.get(Constants.REPOSITORY).getValue());

        LOG.info("repository: "+repository);
        LOG.info("docker api url: "+DockerAPI.V2.getUrl());
        LOG.info("registry value: "+repositoryConfiguration.get(Constants.REGISTRY).getValue());
        LOG.info("repository value: "+packageConfiguration.get(Constants.REPOSITORY).getValue());

        try {
            GetMethod get = new GetMethod(repository);
            if (client.executeMethod(get) == HttpStatus.SC_OK) {
                String jsonString = get.getResponseBodyAsString();
                LOG.info("RECIEVED: " + jsonString);
                result = (JsonArray) ((JsonObject) new JsonParser().parse(jsonString)).get("tags");
                LOG.info("Build result: "+result);
            }
        } catch (IOException e) {
            // Wrap into a runtime. There is nothing useful to do here
            // when this happens.
            LOG.error("cannot fetch the tags from "+repository);
            throw new RuntimeException("Cannot fetch the tags from " + repository, e);
        }

        return result;
    }

}
