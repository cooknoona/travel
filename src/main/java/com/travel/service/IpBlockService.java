package com.travel.service;

import com.travel.constant.LogDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** More than 5 times failing of logging in with one's IP address, All the access to go via Login API will
 *  store into redis and block the IP address */
@Service
@RequiredArgsConstructor
public class IpBlockService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String LOGIN_FAIL_WITHOUT_ID_PREFIX = "login:fail:without:id:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 10;

    public void increaseLoginFailCount(String ipAddress) {
        increaseFailCount(LOGIN_FAIL_PREFIX + ipAddress, BLOCK_DURATION_MINUTES);
    }

    public void increaseLoginFailWithoutUserIdCount(String ipAddress) {
        increaseFailCount(LOGIN_FAIL_WITHOUT_ID_PREFIX + ipAddress, 1);
    }

    private void increaseFailCount(String key, long durationMinutes) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            ops.increment(key);
        } else {
            ops.set(key, "1", Duration.ofMinutes(durationMinutes));
        }
    }

    private boolean isBlockedByKey(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null && Integer.parseInt(value) >= MAX_LOGIN_ATTEMPTS;
    }

    public LogDetail getBlockReason(String ipAddress) {
        boolean tooManyAttempts = isBlockedByKey(LOGIN_FAIL_PREFIX + ipAddress);
        boolean invalidUserAttempts = isBlockedByKey(LOGIN_FAIL_WITHOUT_ID_PREFIX + ipAddress);

        if (tooManyAttempts) return LogDetail.TOO_MANY_ATTEMPTS;
        if (invalidUserAttempts) return LogDetail.NON_EXISTENT_USER_ATTEMPTS;
        return LogDetail.NONE;
    }

    public void blockIp(String ipAddress) {
        redisTemplate.opsForValue().set(LOGIN_FAIL_PREFIX + ipAddress, String.valueOf(MAX_LOGIN_ATTEMPTS), Duration.ofMinutes(BLOCK_DURATION_MINUTES));
        redisTemplate.opsForValue().set(LOGIN_FAIL_WITHOUT_ID_PREFIX + ipAddress, String.valueOf(MAX_LOGIN_ATTEMPTS), Duration.ofMinutes(BLOCK_DURATION_MINUTES));
    }

    public void resetLoginFailCount(String ipAddress) {
        redisTemplate.delete(LOGIN_FAIL_PREFIX + ipAddress);
        redisTemplate.delete(LOGIN_FAIL_WITHOUT_ID_PREFIX + ipAddress);
    }
}

