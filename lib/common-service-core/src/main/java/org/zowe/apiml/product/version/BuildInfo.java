/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.product.version;

import org.zowe.apiml.message.log.ApimlLogger;
import org.zowe.apiml.message.yaml.YamlMessageServiceInstance;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class BuildInfo {

    String buildProperties = "META-INF/build-info.properties";
    String gitProperties = "META-INF/git.properties";

    public BuildInfo(String buildProperties, String gitProperties) {
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
    }

    public BuildInfo() {
    }

    private ApimlLogger apimlLog = ApimlLogger.of(this.getClass(), YamlMessageServiceInstance.getInstance());

    public void logBuildInfo() {
        BuildInfoDetails buildInfo = getBuildInfoDetails();
        log.info("Service {} version {} #{} on {} by {} commit {}", buildInfo.getArtifact(), buildInfo.getVersion(), buildInfo.getNumber(),
            buildInfo.getTime(), buildInfo.getMachine(), buildInfo.getCommitId());
    }

    public BuildInfoDetails getBuildInfoDetails() {
        Properties build = getProperties(buildProperties);
        Properties git = getProperties(gitProperties);
        return new BuildInfoDetails(build, git);
    }

    private Properties getProperties(String path) {
        // Create the Properties
        Properties props = new Properties();

        // Create the input streams
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                apimlLog.log("org.zowe.apiml.common.buildInfoPropertiesNotFound", path);
                return props;
            }

            props.load(input);
        } catch (IOException ioe) {
            apimlLog.log("org.zowe.apiml.common.buildInfoPropertiesIOError", path, ioe.toString());
        }

        return props;
    }
}
