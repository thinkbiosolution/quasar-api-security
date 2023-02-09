/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.caching.service.vsam.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.zowe.apiml.caching.config.GeneralConfig;
import org.zowe.apiml.zfile.ZFileConstants;

/**
 * Main configuration hub for VSAM based storage
 *
 * We should not need more than 32 bytes for key length.
 * Following props can be overriden from command line.
 */
@Configuration
@Data
@ToString
@RequiredArgsConstructor
public class VsamConfig {
    private final GeneralConfig generalConfig;

    @Value("${caching.storage.vsam.name:}")
    private String fileName;
    @Value("${caching.storage.vsam.keyLength:128}")
    private int keyLength;
    @Value("${caching.storage.vsam.recordLength:4096}")
    private int recordLength;
    @Value("${caching.storage.vsam.encoding:" + ZFileConstants.DEFAULT_EBCDIC_CODE_PAGE + "}")
    private String encoding;

    public enum VsamOptions {
        READ("rb,type=record"),
        WRITE("ab+,type=record");

        @Getter
        private final String optionsString;

        VsamOptions(String optionsString) {
            this.optionsString = optionsString;
        }
    }
}
