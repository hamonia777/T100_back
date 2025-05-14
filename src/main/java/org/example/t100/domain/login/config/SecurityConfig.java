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

    private final AuthenticationConfiguration authenticationConfiguration;  //스프링 시큐리티의 인증 설정을 관리하는 객체
    private final JwtUtil jwtUtil; // JWT(JSON Web Token)관련 기능을 제공하는 유틸리티 클래스(토큰 생성,검증 등등)
    private final RedisUtil redisUtil; // REdis 데이터베이스 관련 기능을 제공하는 유틸리티 클래스(토큰 저장, 조회,삭제 등)

    //인증 없이 접근이 허용된 URL 목록을 담는 String 배열
    private final String[] allowedUrls = {
            "/", "/reissue", "/login", "/api/signup", "/api/crawl", "/api/OtherCrawl","/api/BandfCrawl","/api/EnterCrawl",
            "/api/signup/findMyPass", "/api/signup/checkNick","/api/signup/checkEmail", "/api/chat","/api/report",
            "/api/LandgCrawl","/api/SportsCrawl","/api/otherChat","/api/bandfChat","/api/enterChat","/api/landgChat",
            "/api/sportsChat","/api/otherReport","/api/bandfReport","/api/enterReport","/api/landgReport",
            "/api/sportsReport","/ChromeTest/test"
    };

    //AuthenticationManage빈을 생성, AuthenticationManager는 실제 인증 과정을 처리하는 인터페이스,AuthenticationConfiguration
    //으로부터 빌더를 통해 생성된다.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    //BCryptPasswordEncoder 빈을 생성, BCryptPasswordEncoder는 비밀번호를 안정하게 암호화하는 데 사용되는
    //스프링 시큐리티의 PasswordEncoder구현체
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    //핵심적인 보안 필터 체인을 정의하는 빈
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CORS 설정 : httpcors()를 사용하여 corsConfigurationSource() 메서드에서 정의한 CORS설정을 적용한다.
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // CSRF 비활성화 : http.csrf().disable()을 통해 CSRF(Cross-Site Request Forgery) 공격 방어 기능을 비활성화한다.
        //일반적으로 RESTfull API 서버의 경우 토큰 기반 인증을 사용하므로 CSRF 보호가 필요하지 않을 수 있다.
        http.csrf(AbstractHttpConfigurer::disable);

        // 폼 로그인 비활성화 : http.formLogin().disable()을 통해 기본 폼 로그인 방식을 비활성화한다. JWT 기반 인증을 사용하므로
        // 폼 로그인은 사용하지 않는다.
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 비활성화 : http.httpBasic().disable() 을 통해 HTTP Basic 인증 방식을 비활성화한다.
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 세션 사용하지 않음 : http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)를 설정하여
        //스프링 시큐리티가 세션을 생성하거나 사용하지 않도록 한다. JWT는 자체적으로 상태를 저정하지 않으므로 세션이 필요 없다.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 경로별 인가 설정 : http.authorizeGttpRequests()를 사용하여 경로별 접근 권한을 설정,
        // api/signUp/** : 패턴의 모든 요청은 인증 없이 접근을 허용한다 (permitAll()).
        // allowedUrls : 배열에 정의된 모든 URL에 대해서도 인증 없이 접근을 허용
        // 그 외의 모든 요청은 인증된 사용자만 접근할 수 있도록 설정(authenticated()).
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/api/signUp/**").permitAll()
                .requestMatchers(allowedUrls).permitAll()
                .anyRequest().authenticated()
        );

        // JWT 로그인 필터 설정 :JwtAuthenticationFilter 를 생성하고 /login URL로 들어오는 요청을 처리하도록 설정
        // 이 필터는 사용자의 아이디와 비밀번호를 받아 JWT를 생성하고 응답헤더에 담아 클라이언트에게 전달하는 역할을 할 것이다.
        // UsernamePasswordAuthenticationFilter 전에 필터를 추가한다.
        JwtAuthenticationFilter loginFilter = new JwtAuthenticationFilter(
                authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/login");

        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);


        // JwtAuthorizationFilter에 allowedUrls 전달 : JwtAuthoriactionFilter를 생성하고 JwtAuthenticationFilter 전에 필터
        // 체인에 추가한다. 이 필터는 클라이언트가 요청 헤더에 담아 보낸 JWT를 검증하고, 유효한 토큰이면 해당 사용자의 인증 정보를
        // 스프링 시큐리티의 SecurityContext에 저장하여 이후 요청에서 인증된 사용자로 처리되도록 합니다. allowedUrls를 전달하여 해당
        // URL에 대해서는 JWT 검증을 건너뛰도록 성정했을 가능성이 있다.
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtil, redisUtil, allowedUrls);
        http.addFilterBefore(jwtAuthorizationFilter, JwtAuthenticationFilter.class);

        // 로그아웃 필터 설정 : /logout URL로 들어오는 로그아웃 요청을 처리하는 설정을 한다.
        //JwtLogoutFilter : Redis에 저장된 해당 사용자의 리프레시 토큰을 삭제하는 등의 로그아웃 처리를 수행하는 필터
        //logoutSuccessHandler : 로그아웃 성공 시 클라이언트에게 "로그아웃 성공" 메시지와 함꼐 HTTP 200 OK 상태 코드를 응답으로 보낸다.
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

        configuration.addAllowedOrigin("http://localhost:3000"); // 허용된 Origin, 프론트엔드 개발 환경 도메인에서의 요청을 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        // 노출할 헤더 추가
        configuration.addExposedHeader("accessToken"); // 클라이언트가 읽을 수 있도록 설정
        configuration.addExposedHeader("accesstoken");
        configuration.addExposedHeader("AccessToken"); // 클라이언트가 응답 헤더에서 accessToken(대소문자 구분 없이) 헤더를
        // 읽을 수 있도록 설정 , JWT를 응답 헤더에 담아 전달하는 경우 클라이언트가 토큰을 획득하기 위해 필요

        //UrlBasedCorsConfigurationSource: URL 패턴에 따라 CORS 설정을 적용할 수 있도록 한다. 여기서는 모든 경로("/**")에 대해
        //위에서 정의한 configuration을 적용한다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
