package org.example.t100.domain.login.config;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.login.jwt.JwtAuthenticationFilter;
import org.example.t100.domain.login.jwt.JwtAuthorizationFilter;
import org.example.t100.domain.login.jwt.JwtLogoutFilter;
import org.example.t100.domain.login.jwt.JwtUtil;
import org.example.t100.global.util.RedisUtil;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    // 인증이 필요하지 않은 URL 목록
    private final String[] allowedUrls = {"/", "/reissue", "/login", "/api/signup"};

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
        http
                .cors(cors -> cors
                        .configurationSource(CorsConfig.apiConfigurationSource()));

        // CSRF 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable);

        // 폼 로그인 비활성화
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 비활성화
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 세션 사용하지 않음
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 경로별 인가 설정
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGE")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers(allowedUrls).permitAll()
                                .anyRequest().permitAll()
                );

        // JWT 로그인 필터 설정
        JwtAuthenticationFilter loginFilter = new JwtAuthenticationFilter(
                authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/login");

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // JwtAuthorizationFilter에 allowedUrls 전달
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtil, redisUtil, allowedUrls);
        http
                .addFilterBefore(jwtAuthorizationFilter, JwtAuthenticationFilter.class);

        // 로그아웃 필터 설정
        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(new JwtLogoutFilter(redisUtil, jwtUtil))
                        .logoutSuccessHandler((request, response, authentication) ->
                                ResponseUtils.ok(
                                        response,
                                        HttpStatus.OK,
                                        "로그아웃 성공"
                                )
                        )
                );

        return http.build();
    }
}
