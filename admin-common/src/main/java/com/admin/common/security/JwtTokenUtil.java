package com.admin.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtTokenUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expire-time}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}")
    private Long refreshTokenExpireTime;

    private static final String ACCESS_TOKEN_PREFIX = "jwt:access:";
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:";
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    public JwtTokenUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(LoginUser loginUser) {
        Map<String, Object> claims = Map.of(
                "userId", loginUser.getUserId(),
                "username", loginUser.getUsername(),
                "roles", loginUser.getRoles() != null ? loginUser.getRoles() : List.of(),
                "permissions", loginUser.getPermissions() != null ? loginUser.getPermissions() : List.of()
        );

        String token = Jwts.builder()
                .claims(claims)
                .subject(loginUser.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpireTime))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        // 存入 Redis
        redisTemplate.opsForValue().set(
                ACCESS_TOKEN_PREFIX + loginUser.getUserId(),
                token,
                accessTokenExpireTime,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    /**
     * 生成刷新令牌（使用后失效，实现一次性使用）
     */
    public String generateRefreshToken(LoginUser loginUser) {
        String token = Jwts.builder()
                .subject(loginUser.getUsername())
                .claim("userId", loginUser.getUserId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        // 存入 Redis，使用唯一 token 作为 key，实现一次性使用
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + token,
                loginUser.getUserId().toString(),
                refreshTokenExpireTime,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Token 是否在黑名单
     */
    public boolean isInBlacklist(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    /**
     * 将 Token 加入黑名单
     */
    public void addToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "1",
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 从 Token 获取用户信息
     */
    @SuppressWarnings("unchecked")
    public LoginUser getLoginUser(String token) {
        Claims claims = parseToken(token);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(Long.valueOf(claims.get("userId").toString()));
        loginUser.setUsername(claims.getSubject());

        Object roles = claims.get("roles");
        if (roles instanceof List) {
            loginUser.setRoles((List<String>) roles);
        }

        Object permissions = claims.get("permissions");
        if (permissions instanceof List) {
            loginUser.setPermissions((List<String>) permissions);
        }

        return loginUser;
    }

    /**
     * 获取 Token 过期时间（秒）
     */
    public Long getAccessTokenExpireTime() {
        return accessTokenExpireTime / 1000;
    }

    /**
     * 移除用户 Token
     */
    public void removeToken(Long userId) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + userId);
        // 注意：refresh token 的 key 是 token 本身，不是 userId
    }

    /**
     * 验证并消费 Refresh Token（一次性使用，使用后立即删除）
     */
    public boolean validateAndConsumeRefreshToken(String token) {
        // 检查 token 格式和签名
        if (!validateToken(token)) {
            return false;
        }

        // 检查黑名单
        if (isInBlacklist(token)) {
            return false;
        }

        // 检查是否存在（未使用过）
        String storedUserId = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + token);
        if (storedUserId == null) {
            return false;
        }

        // 立即删除，实现一次性使用
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
        return true;
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 移除 Refresh Token
     */
    public void removeRefreshToken(String token) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
    }
}
