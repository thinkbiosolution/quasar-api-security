/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.security.common.handler;

import org.springframework.context.annotation.Primary;
import org.zowe.apiml.security.common.error.AuthExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication error handler
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FailedAuthenticationHandler implements AuthenticationFailureHandler {
    private final AuthExceptionHandler handler;

    /**
     * Handles authentication failure by printing a debug message and passes control to {@link AuthExceptionHandler}
     *
     * @param request   the http request
     * @param response  the http response
     * @param exception to be checked
     * @throws ServletException when the response cannot be written
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException {
        log.debug("Unauthorized access to '{}' endpoint", request.getRequestURI());
        handler.handleException(request, response, exception);
    }
}
