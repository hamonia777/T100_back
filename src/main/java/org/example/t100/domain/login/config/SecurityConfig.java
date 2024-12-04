package org.example.t100.domain.login.config;

import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor
import org.example.t100.domain.login.jwt.JwtAuthenticationFilter; // JWT 인증 필터
import org.example.t100.domain.login.jwt.JwtAuthorizationFilter; // JWT 인가 필터
import org.example.t100.domain.login.jwt.JwtLogoutFilter; // JWT 로그아웃 필터
import org.example.t100.domain.login.jwt.JwtUtil; // JWT 유틸리티 클래스
import org.example.t100.global.util.RedisUtil; // Redis 유틸리티
import org.example.t100.global.util.ResponseUtils; // 응답 유틸리티
import org.springframework.context.annotation.Bean; // Bean 정의
import org.springframework.context.annotation.Configuration; // Configuration 어노테이션
import org.springframework.http.HttpStatus; // HTTP 상태 코드
import org.springframework.security.authentication.AuthenticationManager; // 인증 관리자
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 인증 설정
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // HTTP 보안 설정
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Spring Security 활성화
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // HTTP 구성
import org.springframework.security.config.http.SessionCreationPolicy; // 세션 생성 정책
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 비밀번호 암호화
import org.springframework.security.web.SecurityFilterChain; // Security 필터 체인
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 인증 필터
import org.springframework.web.cors.CorsConfiguration; // CORS 설정
import org.springframework.web.cors.CorsConfigurationSource; // CORS 설정 소스
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // URL 기반 CORS 설정 소스


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private final String[] allowedUrls = {
            "/", "/reissue", "/login", "/api/signup", "/api/crawl",
            "/api/signup/findMyPass", "/api/signup/checkNick","/api/signup/checkEmail", "/api/chat","/api/report"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 폼 로그인 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 세션 사용하지 않음
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 경로별 인가 설정
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/api/signUp/**").permitAll()
                .requestMatchers(allowedUrls).permitAll()
                .anyRequest().authenticated()
        );

        // JWT 로그인 필터 설정
        JwtAuthenticationFilter loginFilter = new JwtAuthenticationFilter(
                authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/login");

        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // JwtAuthorizationFilter에 allowedUrls 전달
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtil, redisUtil, allowedUrls);
        http.addFilterBefore(jwtAuthorizationFilter, JwtAuthenticationFilter.class);

        // 로그아웃 필터 설정
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(new JwtLogoutFilter(redisUtil, jwtUtil))
                .logoutSuccessHandler((request, response, authentication) ->
                        ResponseUtils.ok(response, HttpStatus.OK, "로그아웃 성공"))
        );

        return http.build();
    }

    // CORS 설정을 SecurityConfig 내에서 직접 정의
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000"); // 허용된 Origin
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        // 노출할 헤더 추가
        configuration.addExposedHeader("accessToken"); // 클라이언트가 읽을 수 있도록 설정
        configuration.addExposedHeader("accesstoken");
        configuration.addExposedHeader("AccessToken");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
