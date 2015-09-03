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

import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import junit.framework.TestCase;

/**
 * @author Manuel Kasiske
 */
public class DockerRegistryTest extends TestCase {

    private DockerRegistry dockerRegistry;

    public void testGetInstanceWithPlainUrlShouldDeliverV2ApiExtension() {
        dockerRegistry = DockerRegistry.getInstance("http://www.google.de");

        assertEquals("http://www.google.de/v2/", dockerRegistry.getUrl());
    }

    public void testValidateShouldBeSuccessful() {
        dockerRegistry = DockerRegistry.getInstance("http://www.google.de");
        ValidationResult validationResult = new ValidationResult();
        dockerRegistry.validate(validationResult);

        assertEquals(0, validationResult.getErrors().size());
    }

    public void testValidateWithEmptyUrlShouldFail() {
        dockerRegistry = DockerRegistry.getInstance("");
        ValidationResult validationResult = new ValidationResult();
        dockerRegistry.validate(validationResult);

        assertEquals(1, validationResult.getErrors().size());
    }

    public void testValidateWithUnsupportedProtocolShouldFail() {
        dockerRegistry = DockerRegistry.getInstance("htty://www.google.de");
        ValidationResult validationResult = new ValidationResult();
        dockerRegistry.validate(validationResult);
        assertEquals(1, validationResult.getErrors().size());
    }

    public void testValidateWithWrongUrlShouldFail() {
        dockerRegistry = DockerRegistry.getInstance("google.de");
        ValidationResult validationResult = new ValidationResult();
        dockerRegistry.validate(validationResult);

        assertEquals(1, validationResult.getErrors().size());
    }

    public void testCheckConnectionShouldFailWithWrongRegistryUrl() {
        //TODO: mock delivery.wub
        dockerRegistry = DockerRegistry.getInstance("http://www.google.de");
        try {
            dockerRegistry.checkConnection();
            fail();
        } catch (RuntimeException e) {

        }
    }

    public void testCheckConnectionShouldSucceed() {
        //TODO: mock delivery.wub
        dockerRegistry = DockerRegistry.getInstance("http://delivery.wub:5000");
        try {
            dockerRegistry.checkConnection();
        } catch (RuntimeException e) {
            fail();
        }
    }




}
