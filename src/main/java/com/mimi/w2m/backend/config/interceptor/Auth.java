package com.mimi.w2m.backend.config.interceptor;

import com.mimi.w2m.backend.domain.type.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증이 필요한 API에 붙이면 된다.
 *
 * @since 2022-12-04
 * @author yeh35
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    /**
     * 인증 유저 타입
     */
    Role value() default Role.USER;

}