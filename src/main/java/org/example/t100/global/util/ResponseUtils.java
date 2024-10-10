package org.example.t100.global.util;

import lombok.extern.slf4j.Slf4j;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Slf4j
public class ResponseUtils {

    public static <T> ApiResponse<T> ok(T response) {
        return new ApiResponse<>(true, 200, null, response);
    }

    public static ApiResponse<?> ok(SuccessCode successCode) {
        int statusCode = successCode.getHttpStatus().value();
        String msg = successCode.getDetail();
        return new ApiResponse<>(true, statusCode, msg, null);
    }

    public static ApiResponse<?> error(ErrorCode errorCode) {
        int statusCode = errorCode.getHttpStatus().value();
        String msg = errorCode.getDetail();
        return new ApiResponse<>(false, statusCode, msg, null);
    }

    public static ApiResponse<?> error(HttpStatus httpStatus, String error) {
        return new ApiResponse<>(false, httpStatus.value(), error, null);
    }

//    public static <T> ApiResponse<T> pageOk(Integer size, Integer page, Integer totalCount, Integer totalPages,
//                                            T response) {
//        return new ApiResponse<>(true, 200, null,
//                size, page, totalCount, totalPages, response);
//    }
}