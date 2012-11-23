package service;

import play.cache.Cache;

public class CacheService {

    private final static CacheService instance = new CacheService();

    public static CacheService getInstance() {
        return instance;
    }

    public <T> void updateOrPut(String key, Object newVal, Class<T> clazz, String expiration) {
        T exVal = Cache.get(key, clazz);
        if (exVal == null) {
            Cache.add(key, newVal, expiration);
        } else {
            Cache.replace(key, newVal, expiration);
        }
    }
}
