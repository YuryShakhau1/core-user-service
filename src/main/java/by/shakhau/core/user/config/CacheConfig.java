package by.shakhau.core.user.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
    public RedisCacheConfiguration redisCacheConfiguration() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY);

        var serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(entityTtl))
                .disableCachingNullValues()

                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))

                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));
    }

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration redisCacheConfiguration) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withCacheConfiguration(
                        "users",
                        redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(
                        "payment-cards",
                        redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(
                        "user-cards",
                        redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)))
                .build();
    }
}

