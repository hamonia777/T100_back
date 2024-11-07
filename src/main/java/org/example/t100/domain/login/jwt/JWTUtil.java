package org.example.t100.domain.login.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.SignatureAlgorithm;

@Component

public class JWTUtil {


    private SecretKey secretKey; //JWT 토큰 객체 키를 저장할 시크릿 키

    public JWTUtil(@Value("${spring.jwtsecretkey}") String secret) { // 올바른 키 이름으로 수정
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("category", String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 수정된 부분
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))  // setExpiration으로 수정
                .signWith(secretKey)
                .compact();
    }

}