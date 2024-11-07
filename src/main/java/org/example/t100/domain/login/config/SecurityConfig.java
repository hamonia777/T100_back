package org.example.t100.domain.login.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.login.jwt.CustomLogoutFilter;
import org.example.t100.domain.login.jwt.JWTFilter;
import org.example.t100.domain.login.jwt.JWTUtil;
import org.example.t100.domain.login.jwt.LoginFilter;
import org.example.t100.domain.login.repository.RefreshRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));


                        return null;
                    }
                }));


        //csrf disable

        http
                .csrf((auth) -> auth.disable())
                //기존의 Session 방식은 Session이 항상 고정되어 있기 때문에 csrf를 구현이 필수 였는데
                // JWT는 Session을 Stateless 방식으로 하기 때문에 csrf 구현이 필수가 아니게 되었다.

                .formLogin((auth)->auth.disable()) //http formLogin 방식 disable
                .httpBasic((auth) -> auth.disable());  //http basic 인증 방식 disable

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/login", "/", "/api/signup").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest().authenticated() //이외의 요청에는 인증이 필요하다 ( 로그인 )
                );
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // 세션 설정하는 부분 : JWT 시 무상태로 설정하는 게 제일 중요하다 .
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil,refreshRepository), UsernamePasswordAuthenticationFilter.class);
        //addFilterAt() , addFilterAfter(), addFilterBefore() 여러가지가 있는데
        // 지금 구현하는 예제에서는 unsernamePasswordAuthenticationFilter를 대체해서 사용할 것이기 때문에
        // 딱 해당 위치에 대체하는 addFilterAt을 쓴다
        // 첫 번째 파라메터에는 직접 만든 필터, 두번 째 파라메터엔 대체할 위치인 필터 클래스를 적는다

        // 로그인 필터는 생성자주입으로 AuthenticationManager 객체를 주입받아서 여기에서도 직접 주입을 해줘야 한다.
        // 빈으로 메서드 생성을 하는데 매니저 또한 컨피규레이션을 주입 받아야 해서 그 부분은 생성자 주입을 해준다
        return http.build();

    }
}

