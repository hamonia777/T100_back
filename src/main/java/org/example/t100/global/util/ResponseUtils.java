package org.example.t100.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ResponseUtils {

    public static <T> ApiResponse<T> ok(T response) {
        return new ApiResponse<>(true, 200, null, response);
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ApiResponse<?> ok(SuccessCode successCode) {
        int statusCode = successCode.getHttpStatus().value();
        String msg = successCode.getDetail();
        return new ApiResponse<>(true, statusCode, msg, null);
    }

    public static <T> ApiResponse<T> ok(HttpServletResponse response, HttpStatus status, T data) {
        response.setStatus(status.value());
        return new ApiResponse<>(true, status.value(), null, data);
    }
    public static ApiResponse<?> error(ErrorCode errorCode) {
        int statusCode = errorCode.getHttpStatus().value();
        String msg = errorCode.getDetail();
        return new ApiResponse<>(false, statusCode, msg, null);
    }

//    public static ApiResponse<?> error(HttpStatus httpStatus, String error) throws IOException {
//        return new ApiResponse<>(false, httpStatus.value(), error, null);
//    }
public static void error(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    // 응답 바디 생성
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("success", false);
    errorResponse.put("status", status.value());
    errorResponse.put("message", message);

    // 응답 바디 작성
    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
}
//    public static void setErrorResponse(HttpServletResponse response, HttpStatus httpStatus, Object body)
//            throws IOException {
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setStatus(httpStatus.value());
//        response.setCharacterEncoding("UTF-8");
//        objectMapper.writeValue(response.getOutputStream(), body);
//    }
//    public static <T> ApiResponse<T> pageOk(Integer size, Integer page, Integer totalCount, Integer totalPages,
//                                            T response) {
//        return new ApiResponse<>(true, 200, null,
//                size, page, totalCount, totalPages, response);
//    }
}