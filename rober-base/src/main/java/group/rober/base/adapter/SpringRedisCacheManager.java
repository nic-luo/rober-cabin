package group.rober.base.adapter;


import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

import java.util.Collection;

public class SpringRedisCacheManager extends RedisCacheManager{
    private final boolean allowCacheNullValue;

    public SpringRedisCacheManager(RedisOperations redisOperations) {
        super(redisOperations);
        this.setUsePrefix(true);
        this.allowCacheNullValue = false;
    }

    public SpringRedisCacheManager(RedisOperations redisOperations, Collection<String> cacheNames) {
        super(redisOperations, cacheNames);
        this.setUsePrefix(true);
        this.allowCacheNullValue = false;
    }

    public SpringRedisCacheManager(RedisOperations redisOperations, Collection<String> cacheNames, boolean cacheNullValues) {
        super(redisOperations, cacheNames, cacheNullValues);
        this.setUsePrefix(true);
        this.allowCacheNullValue = cacheNullValues;
    }

    @Override
    public SpringRedisCache createCache(String cacheName) {
        long expiration = computeExpiration(cacheName);
        return new SpringRedisCache(cacheName, (isUsePrefix() ? getCachePrefix().prefix(cacheName) : null),
                getRedisOperations(), expiration, allowCacheNullValue);
    }
}
