package com.community.core.common.dto;

import com.community.core.exception.ErrorResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 래퍼
 */
@Getter
@Builder
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;
    private final ErrorResponse error;

    //성공 응답 데이터
    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    //성공 응답(데이터 없음)
    public static  ApiResponse<Void> success(){
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    //성공 응답 메세지
    public static ApiResponse<Void> success(String message){
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
    }

    // 에러 응답
    public static ApiResponse<Void> error(ErrorResponse errorResponse) {
        return ApiResponse.<Void>builder()
                .success(false)
                .error(errorResponse)
                .build();
    }
}
