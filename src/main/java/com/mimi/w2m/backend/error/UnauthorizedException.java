package com.mimi.w2m.backend.error;

import com.mimi.w2m.backend.dto.ApiResultCode;

/**
 * 인증이 안된 경우
 *
 * @author yeh35
 * @since 2022-11-05
 */
@SuppressWarnings("unused")
public class UnauthorizedException extends BusinessException {

public UnauthorizedException(String message) {
    super(ApiResultCode.NOT_LOGIN, message);
}

public UnauthorizedException(String message, String messageToClient) {
    super(ApiResultCode.NOT_LOGIN, message, messageToClient);
}

public UnauthorizedException(String message, String messageToClient, Throwable cause) {
    super(ApiResultCode.NOT_LOGIN, message, messageToClient, cause);
}
}
