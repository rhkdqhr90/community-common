package com.community.core.exception;

import lombok.Getter;

/**
 * 비즈니스 예외의 기본 클래스
 * 모든 도메인 예외는 이 클래스 상속
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = new Object[]{};
    }

    protected BusinessException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(),args));
        this.errorCode = errorCode;
        this.args = args;
    }
    protected BusinessException(ErrorCode errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[]{};
    }
    public int getStatus() {
        return errorCode.getStatus();
    }

}
