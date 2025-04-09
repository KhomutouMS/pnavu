package com.example.fakemaleru.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheUtil {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final int maxCacheSize;

    CacheUtil() {
        maxCacheSize = 100;
    }

    public <T> T get(String key, Class<T> type) {
        Object value = cache.get(key);
        if (value != null) {
            log.info("FIND DATA BY KEY: {}", key);
            return type.cast(value);
        }
        log.info("NOT FIND DATA BY KEY: {}", key);
        return null;
    }

    public void put(String key, Object value) {
        if (cache.size() >= maxCacheSize) {
            log.info("CACHE IS FULL. NEED TO CLEAR");
            this.clear();
        }
        cache.put(key, value);
        log.info("ADDED TO CACHE: {}", key);
    }

    public void delete(String key) {
        cache.remove(key);
        log.info("DELETED FROM CACHE: {}", key);
    }

    public void clear() {
        cache.clear();
        log.info("CACHE CLEARED");
    }
}
