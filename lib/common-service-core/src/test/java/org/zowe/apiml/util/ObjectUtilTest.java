/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ObjectUtilTest {

    @Test
    void testRequireNotNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ObjectUtil.requireNotNull(null, "Parameter can't be null"));
        assertEquals("Parameter can't be null", exception.getMessage());
    }

    @Test
    void testRequireNotEmpty() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> ObjectUtil.requireNotEmpty(null, "Parameter can't be empty"));
        assertEquals("Parameter can't be empty", e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> ObjectUtil.requireNotEmpty("", "Parameter can't be empty"));
        assertEquals("Parameter can't be empty", e.getMessage());

        ObjectUtil.requireNotEmpty(" ", "Parameter can't be empty");
    }

    private ThreadLocal<Map<String, String>> threadContext = new ThreadLocal();

    @Test
    void testSetApiMlContextMap_Ok() {
        Map map = ObjectUtil.getThreadContextMap(threadContext);
        assertNotNull(map);

        map.put("test.property", "test.value");
        map = ObjectUtil.getThreadContextMap(threadContext);
        assertEquals("test.value", map.get("test.property"));
    }


    @Test
    void testMapMerge_FULL() {
        Map<String, Object> defaultConfigPropertiesMap = getMap1();
        Map<String, Object> additionalConfigPropertiesMap = getMap2();

        Map<String, Object> map3 = ObjectUtil.mergeConfigurations(defaultConfigPropertiesMap, additionalConfigPropertiesMap);

        assertNotNull(map3);
        assertEquals("../keystore/localhost/localhost.keystore.p12", ((Map)map3.get("ssl")).get("trustStore"));
        assertEquals("password2", ((Map)map3.get("ssl")).get("trustStorePassword"));

        map3 = ObjectUtil.mergeConfigurations(defaultConfigPropertiesMap, null);
        assertNotNull(map3);
        assertEquals(map3, defaultConfigPropertiesMap);

        map3 = ObjectUtil.mergeConfigurations(null, additionalConfigPropertiesMap);

        assertNotNull(map3);
        assertEquals("../keystore/localhost/localhost.keystore.p12", ((Map)map3.get("ssl")).get("trustStore"));
        assertEquals("password2", ((Map)map3.get("ssl")).get("trustStorePassword"));
    }

    @Test
    void testMapMerge_PART_serviceid_keystore_truststore() {

        Map<String, Object> defaultConfigPropertiesMap = getMap1();
        Map<String, Object> additionalConfigPropertiesMap = getMap2();

        Map<String, Object> map3 = ObjectUtil.mergeConfigurations(defaultConfigPropertiesMap, additionalConfigPropertiesMap);

        assertNotNull(map3);
        assertEquals("hellozowe", (map3.get("serviceId")));
        assertEquals("hello-zowe", ((Map)((Map)map3.get("catalog")).get("tile")).get("id"));
        assertEquals("../keystore/localhost/localhost.keystore.p12", ((Map)map3.get("ssl")).get("keyStore"));
        assertEquals("password2", ((Map)map3.get("ssl")).get("keyStorePassword"));
        assertEquals("../keystore/localhost/localhost.keystore.p12", ((Map)map3.get("ssl")).get("trustStore"));
        assertEquals("password2", ((Map)map3.get("ssl")).get("trustStorePassword"));
    }

    private Map<String, Object> getMap2() {
        Map<String, Object> map2 = getMap1();

        map2.put("serviceId", "hellozowe");
        map2.put("title", "Hello PJE REST API");
        map2.put("description", "POC for using PLain Java Enabler");
        map2.put("baseUrl", "http://localhost:8080/hellopje");
        map2.put("serviceIpAddress", "192.168.0.1");

        List discoveryServiceUrls = new ArrayList();
        discoveryServiceUrls.add("http://localhost:10011/eureka");
        map2.put("discoveryServiceUrls", discoveryServiceUrls);

        ((Map<String, String>)((Map<String, Object>)map2.get("catalog")).get("tile")).put("id", "hello-zowe");
        ((Map<String, String>)((Map<String, Object>)map2.get("catalog")).get("tile")).put("title", "Hello PJE REST API");
        ((Map<String, String>)((Map<String, Object>)map2.get("catalog")).get("tile")).put("description", "Proof of Concept application to demonstrate exposing a REST API with Plain Java Enabler");
        ((Map<String, String>)((Map<String, Object>)map2.get("catalog")).get("tile")).put("version", "1.0.1");

        ((Map<String, String>)map2.get("ssl")).put("keyStore", "../keystore/localhost/localhost.keystore.p12");
        ((Map<String, String>)map2.get("ssl")).put("keyStorePassword", "password2");
        ((Map<String, String>)map2.get("ssl")).put("trustStore", "../keystore/localhost/localhost.keystore.p12");
        ((Map<String, String>)map2.get("ssl")).put("trustStorePassword", "password2");
        ((Map<String, String>)map2.get("ssl")).put("ciphers", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384");
        return map2;
    }

    private Map<String, Object> getMap1() {
        Map<String, Object> map1 = new HashMap();

        map1.put("serviceId", "service");
        map1.put("title", "HelloWorld Spring REST API");
        map1.put("description", "POC for exposing a Spring REST API");
        map1.put("baseUrl", "http://localhost:10021/hellospring");
        map1.put("serviceIpAddress", "127.0.0.1");

        map1.put("homePageRelativeUrl", "/");
        map1.put("statusPageRelativeUrl", "/application/info");
        map1.put("healthCheckRelativeUrl", "/application/health");

        List discoveryServiceUrls = new ArrayList();
        discoveryServiceUrls.add("http://eureka:password@localhost:10011/eureka");
        discoveryServiceUrls.add("http://eureka:password@localhost:10011/eureka1");
        map1.put("discoveryServiceUrls", discoveryServiceUrls);

        List<Map<String, String>> routes = new ArrayList<>();
        map1.put("routes", routes);
        Map<String, String> route = new HashMap();
        routes.add(route);
        route.put("gatewayUrl", "api/v1");
        route.put("serviceUrl", "/hellospring/api/v1");

        route = new HashMap();
        routes.add(route);
        route.put("gatewayUrl", "api/v1/api-doc");
        route.put("serviceUrl", "/hellospring/api-doc");

        List<Map<String, String>> apiInfoList = new ArrayList<>();
        map1.put("apiInfo", apiInfoList);
        Map<String, String> apiINfoMap = new HashMap();
        apiInfoList.add(apiINfoMap);
        apiINfoMap.put("apiId", "org.zowe.hellospring");
        apiINfoMap.put("gatewayUrl", "api/v1");
        apiINfoMap.put("swaggerUrl", "http://localhost:10021/hellospring/api-doc");

        Map<String, Object> catalogMap = new HashMap<>();
        map1.put("catalog", catalogMap);

        Map<String, String> tileMap = new HashMap<>();
        catalogMap.put("tile", tileMap);
        tileMap.put("id", "helloworld-spring");
        tileMap.put("title", "HelloWorld Spring REST API");
        tileMap.put("description", "Proof of Concept application to demonstrate exposing a REST API in the MFaaS ecosystem");
        tileMap.put("version", "1.0.0");

        Map<String, String> sslMap = new HashMap<>();
        map1.put("ssl", sslMap);
        sslMap.put("enabled", "true");
        sslMap.put("protocol", "TLSv1.2");
        sslMap.put("keyAlias", "localhost");
        sslMap.put("keyPassword", "password");
        sslMap.put("keyStore", "keystore/localhost/localhost.keystore.p12");
        sslMap.put("keyStorePassword", "password");
        sslMap.put("keyStoreType", "PKCS12");
        sslMap.put("trustStore", "keystore/localhost/localhost.truststore.p12");
        sslMap.put("trustStorePassword", "password");
        sslMap.put("trustStoreType", "PKCS12");

        return map1;
    }

    @Test
    void testMapMerge_PART_serviceid_ciphers() {
        Map<String, Object> defaultConfigPropertiesMap = getMap1();
        Map<String, Object> additionalConfigPropertiesMap = getMap2();

        Map<String, Object> map3 = ObjectUtil.mergeConfigurations(defaultConfigPropertiesMap, additionalConfigPropertiesMap);

        assertNotNull(map3);
        assertEquals("hellozowe", (map3.get("serviceId")));
        assertEquals("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", ((Map)map3.get("ssl")).get("ciphers"));
        assertEquals("../keystore/localhost/localhost.keystore.p12", ((Map)map3.get("ssl")).get("keyStore"));
        assertEquals("password2", ((Map)map3.get("ssl")).get("keyStorePassword"));
        assertEquals("../keystore/localhost/localhost.keystore.p12", ((Map)map3.get("ssl")).get("trustStore"));
        assertEquals("password2", ((Map)map3.get("ssl")).get("trustStorePassword"));
    }

    private void testMethod1(String a, int b, Object c) {
    }

    @Test
    void testGetMethodIdentifier() throws NoSuchMethodException {
        assertEquals("testGetMethodIdentifier()", ObjectUtil.getMethodIdentifier(ObjectUtilTest.class.getDeclaredMethod("testGetMethodIdentifier")));
        assertEquals("testMethod1(class java.lang.String,int,class java.lang.Object)", ObjectUtil.getMethodIdentifier(ObjectUtilTest.class.getDeclaredMethod("testMethod1", String.class, int.class, Object.class)));
    }

}
