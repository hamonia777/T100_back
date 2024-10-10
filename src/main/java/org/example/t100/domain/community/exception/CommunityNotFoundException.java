package org.example.t100.domain.community.exception;

import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Exception.GlobalException;

public class CommunityNotFoundException extends GlobalException {
    public CommunityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
