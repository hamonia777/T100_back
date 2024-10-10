package org.example.t100.global.Exception;

import lombok.Getter;
import org.example.t100.global.Enum.ErrorCode;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }
}