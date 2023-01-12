package com.mimi.w2m.backend.client.kakao.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenResponse {

    /**
     * 토큰 타입, bearer로 고정
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 사용자 액세스 토큰 값
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 액세스 토큰과 ID 토큰의 만료 시간(초)
     * 참고: 액세스 토큰과 ID 토큰의 만료 시간은 동일
     */
    @JsonProperty("expires_in")
    private long accessTokenExpiresIn;

    /**
     * 사용자 리프레시 토큰 값
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 리프레시 토큰 만료 시간(초)
     */
    @JsonProperty("refresh_token_expires_in")
    private long refreshTokenExpiresIn;

}
