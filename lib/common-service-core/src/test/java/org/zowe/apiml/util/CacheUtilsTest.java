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

import net.sf.ehcache.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.zowe.apiml.cache.CompositeKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CacheUtilsTest {

    private CacheUtils underTest;
    private int removeCounter;

    @BeforeEach
    void setUp() {
        underTest = new CacheUtils();
    }

    @Test
    void testEvictSubset() {
        CacheManager cacheManager = mock(CacheManager.class);

        // cache1 is not ehCache
        Cache cache1 = mock(Cache.class);
        when(cacheManager.getCache("cache1")).thenReturn(cache1);
        when(cache1.getNativeCache()).thenReturn(Object.class);

        Cache cache2 = mock(Cache.class);
        when(cacheManager.getCache("cache2")).thenReturn(cache2);
        net.sf.ehcache.Cache ehCache2 = mock(net.sf.ehcache.Cache.class);

        when(cache2.getNativeCache()).thenReturn(ehCache2);
        List<Object> keys = Arrays.asList(
            "abc", // not composite key
            new CompositeKey("test", 5),
            new CompositeKey("next", 10),
            new CompositeKey("next", 15)
        );
        when(ehCache2.getKeys()).thenReturn(keys);

        try {
            underTest.evictSubset(cacheManager, "missing", x -> true);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Unknown cache"));
            assertTrue(e.getMessage().contains("missing"));
        }

        // not EhCache - clean all, dont use keyPredicate
        verify(cache1, never()).clear();
        underTest.evictSubset(cacheManager, "cache1", x -> false);
        verify(cache1, times(1)).clear();

        final Answer<Boolean> answer = invocation -> {
            removeCounter++;
            return true;
        };

        doAnswer(answer).when(ehCache2).remove(any(Serializable.class));
        doAnswer(answer).when(ehCache2).remove((Object) any());

        assertEquals(0, removeCounter);
        // in all cases remove entries without CompositeKey
        underTest.evictSubset(cacheManager, "cache2", x -> false);
        assertEquals(1, removeCounter);
        verify(ehCache2, times(1)).remove(keys.get(0));

        underTest.evictSubset(cacheManager, "cache2", x -> x.equals(0, "test"));
        assertEquals(3, removeCounter);
        verify(ehCache2, times(2)).remove(keys.get(0));
        verify(ehCache2, times(1)).remove(keys.get(1));

        underTest.evictSubset(cacheManager, "cache2", x -> (Integer) x.get(1) > 10);
        assertEquals(5, removeCounter);
        verify(ehCache2, times(3)).remove(keys.get(0));
        verify(ehCache2, times(1)).remove(keys.get(3));
    }

    @Test
    void givenUnknownCacheName_whenGetAllRecords_thenThrowsException() {
        CacheManager cacheManager = mock(CacheManager.class);
        IllegalArgumentException iae = assertThrows(
            IllegalArgumentException.class,
            () -> underTest.getAllRecords(cacheManager, "unknownCacheName")
        );
        assertEquals("Unknown cache unknownCacheName", iae.getMessage());
    }

    @Test
    void givenUnsupportedCacheManager_whenGetAllRecords_thenThrowsException() {
        CacheManager cacheManager = mock(CacheManager.class);
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("knownCacheName")).thenReturn(cache);
        when(cache.getNativeCache()).thenReturn(new Object());
        IllegalArgumentException iae = assertThrows(
            IllegalArgumentException.class,
            () -> underTest.getAllRecords(cacheManager, "knownCacheName")
        );
        assertTrue(iae.getMessage().startsWith("Unsupported type of cache : "));
    }

    private Map<Object, Element> convert(Map<Integer, String> in) {
        Map<Object, Element> out = new HashMap<>();
        for (Map.Entry<Integer, String> entry : in.entrySet()) {
            out.put(entry.getKey(), new Element(entry.getKey(), entry.getValue()));
        }
        return out;
    }

    @Test
    void givenValidCacheManager_whenGetAllRecords_thenReadAllStoredRecords() {
        CacheManager cacheManager = mock(CacheManager.class);
        Cache cache = mock(Cache.class);
        net.sf.ehcache.Cache ehCache = mock(net.sf.ehcache.Cache.class);

        Map<Integer, String> entries = new HashMap<>();
        entries.put(1, "a");
        entries.put(2, "b");
        entries.put(3, "c");
        List<Object> keys = new ArrayList<>(entries.keySet());

        when(cacheManager.getCache("knownCacheName")).thenReturn(cache);
        when(cache.getNativeCache()).thenReturn(ehCache);
        when(ehCache.getKeys()).thenReturn(keys);
        when(ehCache.getAll(keys)).thenReturn(convert(entries));

        Collection<String> values = underTest.getAllRecords(cacheManager, "knownCacheName");
        assertNotNull(values);
        assertEquals(3, values.size());
        assertTrue(values.contains("a"));
        assertTrue(values.contains("b"));
        assertTrue(values.contains("c"));
    }

}
