package org.example.t100.domain.Auth.exception;

import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Exception.GlobalException;

public class UserNotFoundException extends GlobalException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
