package by.shakhau.core.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    private final int entityTtl;

    public CacheConfig(@Value("${spring.data.redis.entity-ttl}") int entityTtl) {
        this.entityTtl = entityTtl;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(entityTtl))
                .disableCachingNullValues()

                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))

                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheConfiguration cacheConfiguration) {
        return (builder) -> builder
                .withCacheConfiguration("users", cacheConfiguration.entryTtl(Duration.ofMinutes(entityTtl)))
                .withCacheConfiguration("payment-cards", cacheConfiguration.entryTtl(Duration.ofMinutes(entityTtl)))
                .withCacheConfiguration("user-cards", cacheConfiguration.entryTtl(Duration.ofMinutes(entityTtl)));
    }
}
