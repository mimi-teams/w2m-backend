package com.mimi.w2m.backend.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * BlockedJwt : 로그아웃된 Jwt Token 을 Blacklisting
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/12/28
 **/
@Entity
@Getter
@Table(name = "blocked_jwt")
public class BlockedJwt extends BaseTimeEntity {
    @Id
    @Column(name = "jwt_token", unique = true, nullable = false, updatable = false)
    String token;

    @Column(name = "expired_at", nullable = false)
    LocalDateTime expiredDate = LocalDateTime.of(1000, 1, 1, 0, 0, 0);

    @Builder
    public BlockedJwt(String token, LocalDateTime expiredDate) {
        this.token = token;
        this.expiredDate = expiredDate;
    }

    protected BlockedJwt() {
        
    }
}