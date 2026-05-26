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
 * 
 * 改进说明：
 * - 添加完整的异常处理
 * - 增加 Token 参数验证
 * - 防止 NPE 异常
 * - 增加详细的错误日志
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
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured. Please set JWT_SECRET environment variable.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(LoginUser loginUser) {
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new IllegalArgumentException("LoginUser and userId cannot be null");
        }

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
        try {
            redisTemplate.opsForValue().set(
                    ACCESS_TOKEN_PREFIX + loginUser.getUserId(),
                    token,
                    accessTokenExpireTime,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.warn("Failed to store access token in Redis: {}", e.getMessage());
            // 不抛异常，Token 仍然有效（降级处理）
        }

        return token;
    }

    /**
     * 生成刷新令牌（使用后失效，实现一次性使用）
     */
    public String generateRefreshToken(LoginUser loginUser) {
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new IllegalArgumentException("LoginUser and userId cannot be null");
        }

        String token = Jwts.builder()
                .subject(loginUser.getUsername())
                .claim("userId", loginUser.getUserId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        // 存入 Redis，使用唯一 token 作为 key，实现一次性使用
        try {
            redisTemplate.opsForValue().set(
                    REFRESH_TOKEN_PREFIX + token,
                    loginUser.getUserId().toString(),
                    refreshTokenExpireTime,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.warn("Failed to store refresh token in Redis: {}", e.getMessage());
        }

        return token;
    }

    /**
     * 安全解析 Token（处理所有异常情况）
     */
    public Claims parseTokenSafely(String token) {
        if (token == null || token.isEmpty()) {
            throw new JwtException("Token cannot be null or empty");
        }

        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token");
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token");
            throw e;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature");
            throw e;
        } catch (JwtException e) {
            log.warn("JWT parsing failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 解析 Token（推荐使用 parseTokenSafely）
     */
    public Claims parseToken(String token) {
        return parseTokenSafely(token);
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            parseTokenSafely(token);
            return true;
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            return false;
        }
    }

    /**
     * 检查 Token 是否在黑名单
     */
    public boolean isInBlacklist(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        } catch (Exception e) {
            log.warn("Failed to check blacklist: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 将 Token 加入黑名单
     */
    public void addToBlacklist(String token, long expiration) {
        if (token == null || token.isEmpty()) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + token,
                    "1",
                    expiration,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.warn("Failed to add token to blacklist: {}", e.getMessage());
        }
    }

    /**
     * 从 Token 获取用户信息（安全版本）
     */
    @SuppressWarnings("unchecked")
    public LoginUser getLoginUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            Claims claims = parseTokenSafely(token);
            LoginUser loginUser = new LoginUser();

            // 安全提取 userId
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                throw new JwtException("Missing userId in token");
            }
            try {
                loginUser.setUserId(Long.valueOf(userIdObj.toString()));
            } catch (NumberFormatException e) {
                throw new JwtException("Invalid userId format in token", e);
            }

            loginUser.setUsername(claims.getSubject());

            // 安全提取 roles
            Object roles = claims.get("roles");
            if (roles instanceof List) {
                loginUser.setRoles((List<String>) roles);
            }

            // 安全提取 permissions
            Object permissions = claims.get("permissions");
            if (permissions instanceof List) {
                loginUser.setPermissions((List<String>) permissions);
            }

            return loginUser;
        } catch (JwtException e) {
            log.error("Failed to extract user from token: {}", e.getMessage());
            throw e;
        }
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
        if (userId == null) {
            return;
        }
        try {
            redisTemplate.delete(ACCESS_TOKEN_PREFIX + userId);
        } catch (Exception e) {
            log.warn("Failed to remove token: {}", e.getMessage());
        }
    }

    /**
     * 验证并消费 Refresh Token（一次性使用，使用后立即删除）
     */
    public boolean validateAndConsumeRefreshToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
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
        } catch (Exception e) {
            log.warn("Failed to validate refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 中安全获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            Claims claims = parseTokenSafely(token);
            Object userIdObj = claims.get("userId");

            if (userIdObj == null) {
                throw new JwtException("Missing userId in token");
            }

            try {
                return Long.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                throw new JwtException("Invalid userId format in token: " + userIdObj, e);
            }
        } catch (JwtException e) {
            log.error("Failed to extract userId from token", e);
            throw e;
        }
    }

    /**
     * 移除 Refresh Token
     */
    public void removeRefreshToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        try {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
        } catch (Exception e) {
            log.warn("Failed to remove refresh token: {}", e.getMessage());
        }
    }
}
