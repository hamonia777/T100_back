package org.example.t100.domain.login.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.login.entity.RefreshEntity;
import org.example.t100.domain.login.jwt.JWTUtil;
import org.example.t100.domain.login.repository.RefreshRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    // 해당 컨트롤러는 JWT를 받고, 검증하고, 새로운 JWT를 발급해야 해서 아래 클래스를 주입 받아야 한다.
    private final JWTUtil jwtUtil;

    private final RefreshRepository refreshRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> resissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired Check
        try{
            jwtUtil.isExpired(refresh);
        }catch(ExpiredJwtException e){

            //response status code
            return new ResponseEntity<>("access token expired", HttpStatus.BAD_REQUEST);
        }

        //refresh 인지 확인 ( 발급시 페이로드에 명시 )
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            //response statuse code

            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장 되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 6000000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        //response
//        response.setHeader("access", newAccess);
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh",newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshEntity(String username, String refresh, long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();

        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }


}