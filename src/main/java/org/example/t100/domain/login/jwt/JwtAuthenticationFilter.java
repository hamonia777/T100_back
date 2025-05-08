package org.example.t100.domain.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.login.dto.JwtDto;
import org.example.t100.domain.login.dto.LoginRequestDto;
import org.example.t100.domain.login.dto.PrincipalDetails;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
// 로그인 경로 및 토큰 생성
@Slf4j
//@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 로그인 시도 중
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error of request body.");
        }

        // 유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password());

        // 인증 토큰 생성 및 반환
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        log.info("[*] Login Success! - Login with " + principalDetails.getUsername());
        JwtDto jwtDto = new JwtDto(
                jwtUtil.createJwtAccessToken(principalDetails),
                jwtUtil.createJwtRefreshToken(principalDetails)
        );
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json; charset=UTF-8"); // 인코딩 설정
        response.setCharacterEncoding("UTF-8"); // 추가 인코딩 설정
        response.addHeader("accessToken", jwtDto.accessToken());
        log.info("Access Token: " + jwtDto.accessToken());
        log.info("Refresh Token: " + jwtDto.refreshToken());

        // 쿠키로 토큰 내려주기 위해 추가 한 부분
        // ✅ 쿠키로 accessToken 내려주기

        //공백을 포함해서 오류가 발생하기 때문에 인코딩을 해줘서 해결하기 위해 추가한 코드
//        String encodedAccessToken = URLEncoder.encode(jwtDto.accessToken(), StandardCharsets.UTF_8);
//        log.info("encodedAccessToken: + "+encodedAccessToken);
//        Cookie cookie = new Cookie("accessToken", encodedAccessToken);
//        cookie.setHttpOnly(true); // JS에서 접근 불가
//        cookie.setSecure(true);   // HTTPS 전용
//        cookie.setPath("/");
//        cookie.setMaxAge(60 * 60); // 1시간
//
//        response.addCookie(cookie);
        //여기까지
        //서버에서 전달해주기 때문에 클라이언트에서 따로 저장,전송을 하지 않아도 괜찮음.
        //클라이언트에서 fetch할 때 ,credentials: "include" 이 부분을 꼭 넣어야 쿠키를 같이 서버에 전송함.
        //쿠키를 서버에서 내려주기 때문에, 다른 곳에서는 따로 보내주지 않음.
        //바디에서는 successCode만 보내주면 됨. 만약 쿠리로 따로 보내주지 않으면
        //바디에서 보내주면 됨.
        //쿠키로 하는 방식이 아닌 헤더에서 받는 방식으로 처음에 설계가 되어 있어서
        //다시 헤더를 사용하는 방식으로 변경 주석 처리 - 0428


        // 헤더에 토큰을 보내주려고 추가 한 부분
        // 토큰을 Authorization 헤더에 추가
        //response.addHeader("Authorization", "Bearer " + jwtDto.accessToken());
        // 또는 body에도 토큰을 같이 내려주기
        String body = objectMapper.writeValueAsString(ResponseUtils.ok(jwtDto));
        //response.getWriter().write(body);
        // 여기까지


//        String Body = objectMapper.writeValueAsString(ApiResponse.onSuccess(jwtDto));
//        String Body = objectMapper.writeValueAsString(ResponseUtils.ok(SuccessCode.USER_LOGIN_SUCCESS));
//        ResponseUtils.ok(response, HttpStatus.CREATED, jwtDto);
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("body", Body);
        //response.getWriter().write(Body);
        response.getWriter().write(body);
//        ResponseUtils.ok(SuccessCode.USER_LOGIN_SUCCESS);
//        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }
}
