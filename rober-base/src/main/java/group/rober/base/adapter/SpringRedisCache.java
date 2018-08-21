package group.rober.base.adapter;


import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;

public class SpringRedisCache extends RedisCache implements ShiroCacheParitalInterface{
    private final byte[] WILD_CARD = new StringRedisSerializer().serialize("*");
    private final boolean usePrefix;
    private final byte[] prefix;
    private final byte[] setOfKnownKeys;
    private final RedisOperations redisOperations;

    public SpringRedisCache(String name, byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                            long expiration) {
        super(name, prefix, redisOperations, expiration);
        this.redisOperations = redisOperations;
        this.usePrefix = prefix != null && prefix.length > 0;
        this.prefix = prefix;
        this.setOfKnownKeys = usePrefix ? new byte[] {} : new StringRedisSerializer().serialize(name + "~keys");
    }


    public SpringRedisCache(String name, byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                            long expiration, boolean allowNullValues) {
        super(name, prefix, redisOperations, expiration, allowNullValues);
        this.redisOperations = redisOperations;
        this.usePrefix = prefix != null && prefix.length > 0;
        this.prefix = prefix;
        this.setOfKnownKeys = usePrefix ? new byte[] {} : new StringRedisSerializer().serialize(name + "~keys");
    }

    public boolean isUsePrefix() {
        return usePrefix;
    }

    @Override
    public int size() {
        if(usePrefix)
            return (Integer) redisOperations.execute(new RedisKeySizeByPrefixCallback());
        else
            return (Integer) redisOperations.execute(new RedisKeySizeByKeysCallback());
    }

    @Override
    public Set keys() {
        if(usePrefix)
            return (Set)redisOperations.execute(new RedisKeysByPrefixCallback());
        else
            return (Set)redisOperations.execute(new RedisKeysByKeysCallback());
    }

    @Override
    public Collection values() {
        @SuppressWarnings({"unchecked"})
        Set<byte[]> keys = (Set<byte[]>)redisOperations.execute(new RedisCallback<Set<byte[]>>() {
            @Override
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                if (usePrefix) {
                    byte[] prefixToUse = Arrays.copyOf(prefix, prefix.length + WILD_CARD.length);
                    System.arraycopy(WILD_CARD, 0, prefixToUse, prefix.length, WILD_CARD.length);
                    return connection.keys(prefixToUse);
                } else {
                    return connection.zRevRangeByScore(setOfKnownKeys, RedisZSetCommands.Range.unbounded());
                }
            }
        });
        if(null == keys || keys.isEmpty()) return Collections.EMPTY_LIST;
        List result = new ArrayList<>(keys.size());
        keys.forEach(key ->
            result.add(redisOperations.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    return redisOperations.getValueSerializer().deserialize(connection.get(key));
                }
            }))
        );
        return result;
    }

    class RedisKeySizeByPrefixCallback implements RedisCallback<Integer> {



        @Override
        public Integer doInRedis(RedisConnection connection) throws DataAccessException {
            byte[] prefixToUse = Arrays.copyOf(prefix, prefix.length + WILD_CARD.length);
            System.arraycopy(WILD_CARD, 0, prefixToUse, prefix.length, WILD_CARD.length);
            Set<byte[]> keys = connection.keys(prefixToUse);
            return keys.size();
        }
    }

    class RedisKeySizeByKeysCallback implements RedisCallback<Integer> {

        @Override
        public Integer doInRedis(RedisConnection connection) throws DataAccessException {
            return connection.zCount(setOfKnownKeys, RedisZSetCommands.Range.unbounded()).intValue();
        }
    }



    class RedisKeysByPrefixCallback implements RedisCallback<Set<String>> {
        @Override
        public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
            byte[] prefixToUse = Arrays.copyOf(prefix, prefix.length + WILD_CARD.length);
            System.arraycopy(WILD_CARD, 0, prefixToUse, prefix.length, WILD_CARD.length);
            Set<byte[]> keys = connection.keys(prefixToUse);
            return deserializeByte2String(keys);
        }
    }

    class RedisKeysByKeysCallback implements RedisCallback<Set<String>> {
        @Override
        public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
            return deserializeByte2String(connection.zRevRangeByScore(setOfKnownKeys, RedisZSetCommands.Range.unbounded()));
        }
    }

    private static Set<String> deserializeByte2String(Set<byte[]> set) {
        if (null == set || set.isEmpty()) return Collections.emptySet();
        Set<String> result = new HashSet<>(set.size());
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        for (byte[] key : set) {
            result.add(stringRedisSerializer.deserialize(key));
        }
        return result;
    }
}
