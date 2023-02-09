/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.message.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * The class is intended for REST API responses that contain error,
 * warning, or informational messages in the common APIML format.
 * <p>
 * It is preferred to return successful responses without messages if possible.
 * <p>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
public class ApiMessageView {

    /**
     * A list of messages that contain error, warning, and informational content.
     */
    private List<ApiMessage> messages;

}
