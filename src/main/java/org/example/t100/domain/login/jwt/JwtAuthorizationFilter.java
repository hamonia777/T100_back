package org.example.t100.domain.login.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.login.dto.PrincipalDetails;
import org.example.t100.global.util.RedisUtil;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final List<String> excludeUrls;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthorizationFilter(JwtUtil jwtUtil, RedisUtil redisUtil, String[] excludeUrls) {
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
        this.excludeUrls = Arrays.asList(excludeUrls);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 인증이 필요하지 않은 URL이면 필터를 건너뜁니다.
        if (isExcludedUrl(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("[*] Jwt Filter");

        try {
            String accessToken = jwtUtil.resolveAccessToken(request);

            // accessToken 없이 접근할 경우
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 로그아웃 처리된 accessToken
            if ("logout".equals(redisUtil.get(accessToken))) {
                log.info("[*] Logout accessToken");
                filterChain.doFilter(request, response);
                return;
            }

            log.info("[*] Authorization with Token");
            authenticateAccessToken(accessToken);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            ResponseUtils.setErrorResponse(response, HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다.");
            log.warn("[*] case : accessToken Expired");
        } catch (Exception e) {
            ResponseUtils.setErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            log.error("Token validation error: {}", e.getMessage());
        }
    }

    private boolean isExcludedUrl(String requestURI) {
        return excludeUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private void authenticateAccessToken(String accessToken) {
        jwtUtil.validateToken(accessToken);

        PrincipalDetails principalDetails = new PrincipalDetails(
                jwtUtil.getUsername(accessToken),
                null,
                jwtUtil.getRoles(accessToken)
        );

        log.info("[*] Authority Registration");

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
