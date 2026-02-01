package com.community.domain.auth.service;

import com.community.core.config.properties.RedisKeyProperties;
import com.community.core.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;
    private final RedisKeyProperties redisKeyProperties;

    private String getKeyPrefix() {
        return redisKeyProperties.getRefreshToken();
    }

    /**
     * RefreshToken 저장
     * @param refreshToken refreshToken
     * @param userId 사용자 ID
     */
    public void saveRefreshToken( Long userId, String refreshToken){
        String key = getKeyPrefix() + userId;
        long ttl = jwtProperties.getRefreshTokenValidity();

        redisTemplate.opsForValue().set(key, refreshToken, ttl, TimeUnit.SECONDS);
        log.info("[Redis] Refresh Token 저장: userId={}, ttl={}초", userId, ttl);
    }

    /**
     * refresh token 조회
     * @param userId 사용자 ID
     * @return refresh token(없으면 null)
     */
    public String getRefreshToken(Long userId){
        String key = getKeyPrefix() + userId;
        String token = redisTemplate.opsForValue().get(key);

        if(token != null){
            log.debug("[Redis] Refresh Token 조회 성공: userId={}", userId);
        } else {
            log.debug("[Redis] Refresh Token 없음: userId={}", userId);
        }

        return token;
    }

    /**
     * Refresh Token 검증 (Redis에 저장된 토큰과 일치하는지)
     * @param refreshToken refreshToken 검증할 Refresh Token
     * @param userId 사용자 ID
     * @return 유효 여부
     */
    public boolean validateRefreshToken(Long userId,String refreshToken){
        String storedToken = getRefreshToken(userId);
        if(storedToken == null){
            log.warn("[Redis] Refresh Token 검증 실패: Redis에 토큰 없음 (userId={})", userId);
            return false;
        }

        boolean isValid = storedToken.equals(refreshToken);

        if(isValid){
            log.info("[Redis] Refresh Token 검증 성공: userId={}", userId);
        } else {
            log.warn("[Redis] Refresh Token 검증 실패: 토큰 불일치 (userId={})", userId);
        }
        return isValid;
    }

    /**
     *  RefreshToken 삭제
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(Long userId){
        String key = getKeyPrefix() + userId;
        Boolean deleted = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(deleted)) {
            log.info("[Redis] Refresh Token 삭제 성공: userId={}", userId);
        } else {
            log.warn("[Redis] Refresh Token 삭제 실패: 키 없음 (userId={})", userId);
        }
    }

    /**
     * TTL 조회
     * @param userId 사용자 ID
     * @return 남은시간(초) 없으면 -2
     */
    public Long gerTtl(Long userId){
        String key = getKeyPrefix() + userId;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        log.debug("[Redis] TTL 조회: userId={}, ttl={}초", userId, ttl);

        return ttl;
    }



}
