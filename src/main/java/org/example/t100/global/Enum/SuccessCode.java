package org.example.t100.global.Enum;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NO_CONTENT;
@Getter
public enum SuccessCode {
    /* 200 OK : 요청이 성공적으로 완료되었다는 의미입니다. */
    SIGNUP_SUCCESS(OK, "회원가입 성공"),
    DUPLICATE_NICK(OK, "닉네임 중복"),
    NOT_DUPLICATE_NICK(OK, "유효한 닉네임"),
    USER_LOGIN_SUCCESS(OK, "로그인 성공"),
    USER_LOGOUT_SUCCESS(OK, "로그아웃 성공"),
    COMMUNITY_SAVE_SUCCESS(OK, "게시물 생성 완료"),
    COMMUNITY_EDIT_SUCCESS(OK, "게시물 수정 완료"),
    COMMUNITY_DELETE_SUCCESS(OK, "게시물 삭제 완료"),
    LIKE_SUCCESS(OK,"좋아요 성공"),
    LIKE_CANCEL(OK,"좋아요 취소"),
    CRAWLING_SUCCESS(OK, "크롤링 성공"),
    MAIL_SUCCESS(OK,"메일 발송 성공"),
    NO_SHOP_SUCCESS(NO_CONTENT, "조회 가능한 가게가 없습니다."),
    REPORT_CREATE_SUCCESS(OK, "보고서 생성 성공");
    

    private final HttpStatus httpStatus;
    private final String detail;

    SuccessCode(HttpStatus httpStatus, String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }
}