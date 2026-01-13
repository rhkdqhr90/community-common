package com.community.core.exception;

import com.community.core.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.security.sasl.AuthenticationException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e){
        log.warn("BusinessException: {} - {}", e.getErrorCode().getCode(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(),e.getMessage());

        return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(errorResponse));
    }

    /**
     * 유효성 검증 예외 처리 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e){
        log.warn("Validation exception: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT,e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(errorResponse));
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e){
        log.warn("Type mismatch: {} = {}", e.getName(), e.getValue());

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(errorResponse));
    }

    /**
     * HTTP 메서드 불일치
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.warn("Method not allowed: {}", e.getMethod());

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.METHOD_NOT_ALLOWED);

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(errorResponse));
    }
    /**
     * 인증 예외
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException e) {

        log.warn("Authentication failed: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(errorResponse));
    }
    /**
     * 접근 거부 예외
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException e) {

        log.warn("Access denied: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 기타 모든 예외 (서버 오류)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {

        log.error("Unexpected error occurred", e);

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorResponse));
    }
}
