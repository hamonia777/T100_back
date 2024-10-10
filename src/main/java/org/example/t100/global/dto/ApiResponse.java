package org.example.t100.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final int statusCode;
    private final String msg;
//    private Integer size;
//    private Integer page;
//    private Integer totalCount;
//    private Integer totalPages;
    private final T data;

    public ApiResponse(boolean success, int statusCode, String msg, T data) {
        this.success = success;
        this.statusCode = statusCode;
        this.msg = msg;
        this.data = data;
    }

//    public ApiResponse(boolean success, int statusCode, String msg,
//                       Integer size, Integer page, Integer totalCount, Integer totalPages, T data) {
//        this.success = success;
//        this.statusCode = statusCode;
//        this.msg = msg;
//        this.size = size;
//        this.page = page;
//        this.totalCount = totalCount;
//        this.totalPages = totalPages;
//        this.data = data;
//    }
}