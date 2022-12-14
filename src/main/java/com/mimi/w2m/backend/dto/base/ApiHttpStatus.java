package com.mimi.w2m.backend.dto.base;

import org.springframework.http.HttpStatus;

/**
 * 우리 서비스에서 사용 가능한 HTTP Status를 규정한다. <br>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">HTTP status</a>
 *
 * @author yeh35
 * @since 2022-11-05
 */
public enum ApiHttpStatus {

    OK(200),

    BAD_REQUEST(400),
    ILLEGAL_ACCESS(401),
    NOT_FOUND(404),
    DUPLICATED(405),

    ERROR(500),
    ;

    public final int httpStatus;

    ApiHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus toHttpStatus() {
        return HttpStatus.valueOf(httpStatus);
    }
}
