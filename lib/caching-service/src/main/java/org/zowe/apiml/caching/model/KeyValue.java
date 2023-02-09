/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.caching.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Data POJO that represents entry in caching service
 */
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class KeyValue implements Serializable {
    private final String key;
    private final String value;
    private String serviceId;
    private final String created;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
        this.serviceId = "";
        this.created = currentTime();
    }

    private static String currentTime() {
        return String.valueOf(new Date().getTime());
    }

    @JsonCreator
    public KeyValue() {
        key = "";
        value = "";
        serviceId = "";
        created = currentTime();
    }
}
