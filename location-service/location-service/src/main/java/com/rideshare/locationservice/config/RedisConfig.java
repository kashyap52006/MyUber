package com.rideshare.locationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    public RedisTemplate<String , String> redisTemplate(
            RedisConnectionFactory connectionFactory
    ){
        RedisTemplate<String , String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // String serializer make data human readable in Redis cli

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }

}
