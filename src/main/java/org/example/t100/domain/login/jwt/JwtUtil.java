package org.example.t100.domain.login.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.login.dto.JwtDto;
import org.example.t100.domain.login.dto.PrincipalDetails;
import org.example.t100.global.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.time.Duration;


import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final RedisUtil redisUtil;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            RedisUtil redis) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName());
        accessExpMs = access;
        refreshExpMs = refresh;
        redisUtil = redis;
    }

    // JWT 토큰을 입력으로 받아 토큰의 페이로드에서 사용자 이름(Username)을 추출
    public String getUsername(String token) throws SignatureException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰을 입력으로 받아 토큰의 페이로드에서 사용자 역할(Roles)을 추출
    public String getRoles(String token) throws SignatureException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // JWT 토큰의 페이로드에서 만료 시간을 검색, 밀리초 단위의 Long 값으로 반환
    public long getExpTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    // Token 발급
    public String tokenProvider(PrincipalDetails principalDetails, Instant expiration) {
        Instant issuedAt = Instant.now();
        String authorities = principalDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setHeaderParam("typ", "JWT") // 헤더에 `typ` 필드 추가
                .setSubject(principalDetails.getUsername())
                .claim("role", authorities)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    // principalDetails 객체에 대해 새로운 JWT 액세스 토큰을 생성
    public String createJwtAccessToken(PrincipalDetails principalDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(principalDetails, expiration);
    }

    // principalDetails 객체에 대해 새로운 JWT 리프레시 토큰을 생성
    public String createJwtRefreshToken(PrincipalDetails principalDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(principalDetails, expiration);

        // Redis에 저장
        redisUtil.save(
                principalDetails.getUsername(),
                refreshToken,
                refreshExpMs,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    // 제공된 리프레시 토큰을 기반으로 JwtDto 쌍을 다시 발급
    public JwtDto reissueToken(String refreshToken) throws SignatureException {
        isRefreshToken(refreshToken);

        // refreshToken에서 user 정보 뽑아서 새로 재 발금 (발급 시간, 유효 시간(reset)만 달리됨)
        PrincipalDetails userDetails = new PrincipalDetails(
                getUsername(refreshToken),
                null,
                getRoles(refreshToken)
        );
        log.info("[*] Token Reissue");

        return new JwtDto(
                createJwtAccessToken(userDetails),
                createJwtRefreshToken(userDetails)
        );
    }

    // HTTP 요청의 'Authorization' 헤더에서 JWT 액세스 토큰을 검색
    public String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("[*] No Token in request");
            return null;
        }
        log.info("[*] Token exists");

        return authorization.split(" ")[1];
    }

    // 리프레시 토큰의 유효성을 검사 (Redis에서 확인)
    public void isRefreshToken(String refreshToken) {
        String username = getUsername(refreshToken);
        String redisRefreshToken = redisUtil.get(username).toString();
        if (!refreshToken.equals(redisRefreshToken)) {
            log.warn("[*] case: Invalid refreshToken");
            throw new NoSuchElementException("Redis에 " + username + "에 해당하는 키가 없습니다.");
        }

        // refreshToken 유효성 검사
        validateToken(refreshToken);
    }

    public void validateToken(String token) {
        try {
            // 토큰을 먼저 파싱하여 만료 시간을 가져옴
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis() - (3 * 60 * 1000)); // 3분의 시계 오차 고려

            log.info("[*] Authorization with Token");
            if (expiration.before(now)) {
                log.info("만료된 JWT 토큰입니다.");
            }
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
    }
}
