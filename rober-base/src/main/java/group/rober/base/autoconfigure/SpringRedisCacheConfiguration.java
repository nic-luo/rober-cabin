package group.rober.base.autoconfigure;

import group.rober.base.adapter.SpringRedisCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Profile("prod")
//@ConditionalOnMissingBean(name = "cacheManager")
@ConditionalOnClass(name = {"org.springframework.data.redis.cache.RedisCacheManager"})
@ConditionalOnProperty(prefix = "group.rober.base", name = "cache", havingValue = "Redis")
@Configuration
public class SpringRedisCacheConfiguration {


    @Bean
    public RedisTemplate<Object, Object> cacheRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public org.springframework.cache.CacheManager cacheManager(@Qualifier("cacheRedisTemplate") RedisTemplate redisTemplate) {
        return new SpringRedisCacheManager(redisTemplate);
    }
}
