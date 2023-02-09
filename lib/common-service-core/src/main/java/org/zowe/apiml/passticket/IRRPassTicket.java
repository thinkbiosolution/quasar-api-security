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
 * Interface covering com.ibm.eserver.zos.racf.IRRPassTicket class
 */
public interface IRRPassTicket {

    /**
     * @param userId User for whom a PassTicket operation is performed.
     * @param applId The Application name for this PassTicket operation. The same Application must be specified when calling the evaluate method.
     * @param passTicket If successful, a PassTicket is returned.
     * @throws IRRPassTicketEvaluationException if an error occurs during the generation of PassTicket. The IRRPassTicketGeneration exception will contain the RACF return codes which identify the problem.
     * @throws java.lang.IllegalStateException when an internal occurs.
     * @throws java.lang.IllegalArgumentException when a NULL or invalid length parameter is passed.
     */
    public void evaluate(String userId, String applId, String passTicket) throws IRRPassTicketEvaluationException;

    /**
     * @param userId User for whom a PassTicket operation is performed.
     * @param applId The Application name for this PassTicket operation. The same Application must be specified when calling the generate method.
     * @return The PassTicket to evaluate for this userid and application
     * @throws IRRPassTicketGenerationException When an error occurs during the evaluation of PassTicket. This can mean that the PassTicket is invalid, expired or has been used previously. This can also indicate that the user does not have the proper RACF authority to perform a PassTicket evaluation, or may indicate an internal error. The IRRPassTicketEvaluation exception will contain the RACF return codes which identify the problem.
     * @throws java.lang.IllegalStateException when an internal error occurs
     * @throws java.lang.IllegalArgumentException when a NULL or invalid length argument is passed P1C- AMR: Removed RETURN Parameter that existed here. This caused a Javadoc error in Java8 compilation. There is no return parameter here.
     */
    public String generate(String userId, String applId) throws IRRPassTicketGenerationException;

}
