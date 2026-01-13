package com.community.core.exception.custom;

import com.community.core.exception.BusinessException;
import com.community.core.exception.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN,message);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
