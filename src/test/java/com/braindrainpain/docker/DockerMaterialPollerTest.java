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

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import junit.framework.TestCase;

import java.util.Date;

import static com.thoughtworks.go.plugin.api.config.Property.*;

/**
 * @author Manuel Kasiske
 */
public class DockerMaterialPollerTest extends TestCase {

    private DockerMaterialPoller dockerMaterialPoller = new DockerMaterialPoller();

    public void testRepositoryConfigurationShouldCheckedSuccessfully() {
        RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();

        assertTrue(dockerMaterialPoller.checkConnectionToRepository(repositoryConfiguration).isSuccessful());
    }

    public void testPackageConfigurationShouldCheckedSuccessfully() {
        RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
        PackageConfiguration packageConfiguration = getPackageConfiguration();

        assertTrue(dockerMaterialPoller.checkConnectionToPackage(packageConfiguration, repositoryConfiguration).isSuccessful());
    }

    public void testGetLatestRevisionShouldBeReturnLatest() {
        RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
        PackageConfiguration packageConfiguration = getPackageConfiguration();

        assertEquals("latest", dockerMaterialPoller.getLatestRevision(packageConfiguration, repositoryConfiguration).getRevision());
    }

    public void testGetLatestRevisionWithWrongRepositoryShouldFailWithNullPointerException() {
        RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
        PackageConfiguration packageConfiguration = getPackageConfiguration();
        packageConfiguration.get(Constants.REPOSITORY).withDefault("pharmacy-ser");

        try {
            dockerMaterialPoller.getLatestRevision(packageConfiguration, repositoryConfiguration).getRevision();
            fail();
        } catch (NullPointerException e) {

        }
    }


    public void testLatestModificationSince() {
        RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
        PackageConfiguration packageConfiguration = getPackageConfiguration();
        PackageRevision packageRevision = new PackageRevision("x", new Date(), "docker");

        PackageRevision latest = dockerMaterialPoller.latestModificationSince(packageConfiguration, repositoryConfiguration, packageRevision);
        assertNotNull(latest);
    }

    private PackageConfiguration getPackageConfiguration() {
        PackageConfiguration packageConfiguration = new PackageConfiguration();
        packageConfiguration.add(new PackageMaterialProperty(Constants.REPOSITORY).
                with(DISPLAY_NAME, "Image").with(DISPLAY_ORDER, 0));
        packageConfiguration.add(new PackageMaterialProperty(Constants.TAG, "latest").
                with(REQUIRED, false).with(DISPLAY_NAME, "Tag").with(DISPLAY_ORDER, 1));
        packageConfiguration.get(Constants.REPOSITORY).withDefault("pharmacy-service");
        return packageConfiguration;
    }

    private RepositoryConfiguration getRepositoryConfiguration() {
        RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
        repositoryConfiguration.add(new PackageMaterialProperty(Constants.REGISTRY).
                with(DISPLAY_NAME, "Registry URL").with(DISPLAY_ORDER, 0));
        //TODO: mock delivery.wub
        repositoryConfiguration.get(Constants.REGISTRY).withDefault("http://delivery.wub:5000");
        return repositoryConfiguration;
    }


}
