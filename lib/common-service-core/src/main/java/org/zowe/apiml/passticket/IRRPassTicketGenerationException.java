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

/**
 * Exception on generation of passTicket
 */
public class IRRPassTicketGenerationException extends AbstractIRRPassTicketException {

    private static final long serialVersionUID = -8944250582222779122L;

    public IRRPassTicketGenerationException(int safRc, int racfRc, int racfRsn) {
        super(safRc, racfRc, racfRsn);
    }

    public IRRPassTicketGenerationException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public String getMessage() {
        return getMessage("Error on generation of PassTicket:");
    }

}
