package com.community.core.exception.custom;

import com.community.core.exception.BusinessException;
import com.community.core.exception.ErrorCode;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode,Object... args) {
        super(errorCode,args);
    }
}
