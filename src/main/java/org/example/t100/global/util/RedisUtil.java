package org.example.t100.global.util;

import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    //RedisConfig에서 생성한 RedisTemplate을 주입받아 사용합니다.
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


//    Redis의 String 타입으로 값을 저장합니다.
//        키(key), 값(val), 만료 시간(time), 시간 단위(timeUnit)을 설정하여 저장합니다.
    public void save(String key, Object val, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, val, time, timeUnit);
    }

    //지정된 키가 Redis에 존재하는지 확인하여 true 또는 false를 반환합니다.
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    //지정된 키로부터 값을 조회하여 반환합니다.
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //지정된 키를 삭제하고 성공 여부를 반환합니다.
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}