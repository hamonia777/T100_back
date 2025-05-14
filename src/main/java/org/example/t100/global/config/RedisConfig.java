package org.example.t100.global.config;

//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.swing.*;

@Configuration
public class RedisConfig {

    //Spring Boot의application.properties 파일에서 Redis 서버 정보를 가져옵니다.
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

//    Redis 서버와 연결을 설정하는 RedisConnectionFactory를 생성합니다.
//    Lettuce: 넌블로킹 Redis 클라이언트로, Spring Boot에서 기본으로 사용되는 클라이언트 라이브러리입니다.
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

//
//    RedisTemplate<String, Object>를 통해 Redis에 데이터를 저장하거나 조회할 수 있습니다.
//    Serializer 설정:
//    StringRedisSerializer로 키와 값을 직렬화합니다.
//    문자열 형태로 저장하여, 데이터 일관성을 유지하고 사람이 읽을 수 있는 형식으로 관리할 수 있습니다.
//
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
