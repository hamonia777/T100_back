package org.example.t100.global.Exception;

import lombok.extern.slf4j.Slf4j;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "global exception handler")
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 일반적인 클라이언트의 잘못된 요청 시
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleException(IllegalArgumentException e) {
        log.error("일반적인 클라이언트의 잘못된 요청 시 : " + e.getMessage());
        return ResponseUtils.error(ErrorCode.BADREQUEST);
    }
}