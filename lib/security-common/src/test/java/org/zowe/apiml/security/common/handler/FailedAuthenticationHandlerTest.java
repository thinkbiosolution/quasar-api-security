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

import org.zowe.apiml.security.common.error.AuthExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.ServletException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class FailedAuthenticationHandlerTest {

    @Test
    void testOnAuthenticationFailure() throws ServletException {
        AuthExceptionHandler authExceptionHandler = mock(AuthExceptionHandler.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        FailedAuthenticationHandler failedAuthenticationHandler = new FailedAuthenticationHandler(authExceptionHandler);

        BadCredentialsException badCredentialsException = new BadCredentialsException("ERROR");
        failedAuthenticationHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, badCredentialsException);

        verify(authExceptionHandler).handleException(
            httpServletRequest,
            httpServletResponse,
            badCredentialsException
        );
    }

}
