package com.travel.service;

import com.travel.entity.User;
import com.travel.exception.client.ResourceNotFoundException;
import com.travel.repository.UserRepository;
import com.travel.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** To save Refresh Tokens in Redis, Also check blacklist and delete refresh token when users logout */
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + userId, refreshToken,
                Duration.ofDays(7));
    }

    public boolean hasValidRefreshToken(Long userId, String refreshToken) {
        String stored = (String) redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);
        return refreshToken.equals(stored);
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_PREFIX + userId);
    }

    public void blacklistAccessToken(String accessToken) {
        long expiration = jwtUtility.getRemainingExpiration(accessToken);
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "blacklisted", Duration.ofMillis(expiration));
    }

    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
    }

    public User getUserFromId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
