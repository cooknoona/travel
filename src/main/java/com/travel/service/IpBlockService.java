package com.travel.service;

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
    private static final String LOGIN_FAIL_WITHOUT_ID_PREFIX = "login:without:id:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 10;

    public void increaseLoginFailCount(String ipAddress) {
        increaseFailCount(LOGIN_FAIL_PREFIX, ipAddress, BLOCK_DURATION_MINUTES);
    }

    public void increaseLoginFailWithoutUserIdCount(String ipAddress) {
        increaseFailCount(LOGIN_FAIL_WITHOUT_ID_PREFIX, ipAddress, 1);
    }

    private void increaseFailCount(String prefix, String ipAddress, long durationMinutes) {
        String key = prefix + ipAddress;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            ops.increment(key);
        } else {
            ops.set(key, "1", Duration.ofMinutes(durationMinutes));
        }
    }

    public Boolean isBlockedDueToTooManyAttempts(String ipAddress) {
        return isBlocked(LOGIN_FAIL_PREFIX, ipAddress);
    }

    public Boolean isBlockedDueToNonexistentUsers(String ipAddress) {
        return isBlocked(LOGIN_FAIL_WITHOUT_ID_PREFIX, ipAddress);
    }

    private boolean isBlocked(String prefix, String ipAddress) {
        String value = redisTemplate.opsForValue().get(prefix + ipAddress);
        return value != null && Integer.parseInt(value) >= MAX_LOGIN_ATTEMPTS;
    }

    public Boolean isBlocked(String ipAddress) {
        return isBlockedDueToTooManyAttempts(ipAddress) || isBlockedDueToNonexistentUsers(ipAddress);
    }

    public void blockIp(String ipAddress) {
        String ipFailKey = LOGIN_FAIL_PREFIX + ipAddress;
        String ipWithoutIdKey = LOGIN_FAIL_WITHOUT_ID_PREFIX + ipAddress;

        // 차단 토큰을 두 키 모두에 설정
        redisTemplate.opsForValue().set(ipFailKey, String.valueOf(MAX_LOGIN_ATTEMPTS), Duration.ofMinutes(BLOCK_DURATION_MINUTES));
        redisTemplate.opsForValue().set(ipWithoutIdKey, String.valueOf(MAX_LOGIN_ATTEMPTS), Duration.ofMinutes(BLOCK_DURATION_MINUTES));
    }

    public void resetLoginFailCount(String ipAddress) {
        redisTemplate.delete(LOGIN_FAIL_PREFIX + ipAddress);
        redisTemplate.delete(LOGIN_FAIL_WITHOUT_ID_PREFIX + ipAddress);
    }
}
