package com.community.core.exception.custom;

import com.community.core.exception.BusinessException;
import com.community.core.exception.ErrorCode;

public class BadRequestException extends BusinessException {
    public BadRequestException(ErrorCode errorCode,Object... args) {
        super(errorCode,args);
    }
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST,message);
    }
}
