/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.message.storage;

import org.zowe.apiml.message.core.MessageType;
import org.zowe.apiml.message.template.MessageTemplate;
import org.zowe.apiml.message.template.MessageTemplates;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MessageTemplateStorageTest {
    private final MessageTemplateStorage messageTemplateStorage = new MessageTemplateStorage();


    @Test
    void testAddMessageTemplates() {
        MessageTemplates messageTemplates = new MessageTemplates(Collections.singletonList(
            new MessageTemplate("key", "number", MessageType.ERROR, "error message")
        ));

        messageTemplateStorage.addMessageTemplates(messageTemplates);

        Optional<MessageTemplate> optionalMessageTemplate = messageTemplateStorage.getMessageTemplate("key");
        assertTrue(optionalMessageTemplate.isPresent(), "Message template is null");
        assertEquals(messageTemplates.getMessages().get(0), optionalMessageTemplate.get(), "Message template is not equal");
    }


    @Test
    void testAddMessageTemplates_whenDuplicatedKeyMessagesArePresent() {
        MessageTemplates messageTemplates = new MessageTemplates(Collections.singletonList(
            new MessageTemplate("key", "number", MessageType.ERROR, "error message")
        ));

        messageTemplateStorage.addMessageTemplates(messageTemplates);

        messageTemplates = new MessageTemplates(Collections.singletonList(
            new MessageTemplate("key", "number", MessageType.ERROR, "error message 2")
        ));

        messageTemplateStorage.addMessageTemplates(messageTemplates);

        Optional<MessageTemplate> optionalMessageTemplate = messageTemplateStorage.getMessageTemplate("key");
        assertTrue(optionalMessageTemplate.isPresent(), "Message template is null");
        assertEquals(messageTemplates.getMessages().get(0).getText(), optionalMessageTemplate.get().getText(), "Message template text is not equal");
    }
}
