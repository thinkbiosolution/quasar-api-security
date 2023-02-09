/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.product.logging;

import org.zowe.apiml.message.log.ApimlLogger;
import org.zowe.apiml.product.logging.annotations.InjectApimlLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@Import(ApimlLogInjectorTest.TestConfig.class)
class ApimlLogInjectorTest {

    @Autowired
    private TestComponent testComponent;

    @Test
    void testInject() {
        assertNotNull(testComponent.apimlLogger);
        assertNull(testComponent.apimlLogger2);
    }

    private static class TestComponent {

        @InjectApimlLogger
        private ApimlLogger apimlLogger;

        private ApimlLogger apimlLogger2;
    }

    @SpringBootConfiguration
    static class TestConfig {

        @Bean
        public ApimlLogInjector apimlLogInjector() {
            return new ApimlLogInjector();
        }

        @Bean
        public TestComponent testComponent() {
            return new ApimlLogInjectorTest.TestComponent();
        }

    }
}
