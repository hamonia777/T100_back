package org.example.t100.global.Enum;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NO_CONTENT;
@Getter
public enum SuccessCode {
    /* 200 OK : 요청이 성공적으로 완료되었다는 의미입니다. */
    USER_LOGIN_SUCCESS(OK, "로그인 성공"),
    USER_LOGOUT_SUCCESS(OK, "로그아웃 성공"),
    NO_SHOP_SUCCESS(NO_CONTENT, "조회 가능한 가게가 없습니다.");

    private final HttpStatus httpStatus;
    private final String detail;

    SuccessCode(HttpStatus httpStatus, String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}