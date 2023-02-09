/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.passticket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IRRPassTicketGenerationExceptionTest {

    @Test
    void testInit() {
        IRRPassTicketGenerationException exception = new IRRPassTicketGenerationException(8, 16, 32);
        assertEquals(8, exception.getSafRc());
        assertEquals(16, exception.getRacfRc());
        assertEquals(32, exception.getRacfRsn());
        assertEquals(AbstractIRRPassTicketException.ErrorCode.ERR_8_16_32, exception.getErrorCode());
        assertEquals("Error on generation of PassTicket: " + AbstractIRRPassTicketException.ErrorCode.ERR_8_16_32.getMessage(), exception.getMessage());

        IRRPassTicketGenerationException exception2 = new IRRPassTicketGenerationException(AbstractIRRPassTicketException.ErrorCode.ERR_8_12_8);
        assertEquals(8, exception2.getSafRc());
        assertEquals(12, exception2.getRacfRc());
        assertEquals(8, exception2.getRacfRsn());
        assertEquals(AbstractIRRPassTicketException.ErrorCode.ERR_8_12_8, exception2.getErrorCode());
    }

}
