/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.security.common.error;

import org.zowe.apiml.message.core.MessageService;
import org.zowe.apiml.product.gateway.GatewayNotAvailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exception handler that deals with exceptions related to accessing other services/resources
 */
@Slf4j
@Component
public class ResourceAccessExceptionHandler extends AbstractExceptionHandler {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ResourceAccessExceptionHandler(MessageService messageService, ObjectMapper mapper) {
        super(messageService, mapper);
    }

    /**
     * Entry method that takes care of an exception passed to it
     *
     * @param request  Http request
     * @param response Http response
     * @param ex       Exception to be handled
     * @throws ServletException Fallback exception if exception cannot be handled
     */
    @Override
    public void handleException(HttpServletRequest request, HttpServletResponse response, RuntimeException ex) throws ServletException {
        if (ex instanceof GatewayNotAvailableException) {
            handleGatewayNotAvailable(request, response, ex);
        } else if (ex instanceof ServiceNotAccessibleException) {
            handleServiceNotAccessible(request, response, ex);
        } else {
            throw ex;
        }
    }

    //500
    private void handleGatewayNotAvailable(HttpServletRequest request, HttpServletResponse response, RuntimeException ex) throws ServletException {
        log.debug(ERROR_MESSAGE_500, ex.getMessage());
        writeErrorResponse(ErrorType.GATEWAY_NOT_AVAILABLE.getErrorMessageKey(), HttpStatus.SERVICE_UNAVAILABLE, request, response);
    }

    private void handleServiceNotAccessible(HttpServletRequest request, HttpServletResponse response, RuntimeException ex) throws ServletException {
        log.debug(ERROR_MESSAGE_500, ex.getMessage());
        writeErrorResponse(ErrorType.SERVICE_UNAVAILABLE.getErrorMessageKey(), HttpStatus.SERVICE_UNAVAILABLE, request, response);
    }
}
