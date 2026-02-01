package com.community.core.security.jwt;

import com.community.domain.user.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;

    /**
     * JWT 생성
     * @param userId 사용자 ID
     * @param email 이메일
     * @param role 권한
     * @return JWT
     */
    public String createAccessToken(Long userId, String email, Role role){
        Date now = new Date();
        Date expire = new Date(now.getTime() + jwtProperties.getAccessTokenValidity() * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role",role.name())
                .issuedAt(now)
                .expiration(expire)
                .signWith(getSigningKey())
                .compact();

    }

    /**
     * RefreshToken
     * @param userId 사용자 ID
     * @return JWT
     */
    public String createRefreshToken(Long userId){
        Date now = new Date();
        Date expire = new Date(now.getTime() + jwtProperties.getRefreshTokenValidity() * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type","refresh")
                .issuedAt(now)
                .expiration(expire)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token){
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 사용자 ID 추출
     * @param token JWT
     * @return 사용자 ID
     */
    public boolean validateToken(String token){
        try{
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    /**
     * 토큰 파싱
     * @param token JWT 토큰
     * @return Claims
     */
    public Claims parseClaims(String token){
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * 서명 키 생성
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
