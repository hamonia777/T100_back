package org.example.t100.global.util.ApiResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.t100.global.util.Exception.CustomException;
import org.example.t100.global.util.Exception.Dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public record ApiResponse<T>(
        @JsonIgnore
        HttpStatus httpStatus,
        boolean success,
        @Nullable T data,
        @Nullable ExceptionDto error
) {

    public static <T> ApiResponse<T> ok(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> ApiResponse<T> created(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.CREATED, true, data, null);
    }

    public static <T> ApiResponse<T> fail(final CustomException e) {
        return new ApiResponse<>(e.getErrorCode().getHttpStatus(), false, null, ExceptionDto.of(e.getErrorCode()));
    }
}