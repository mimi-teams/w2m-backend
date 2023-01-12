package com.mimi.w2m.backend.client.kakao.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public class KakaoTokenRequest {

    /**
     * authorization_code 로 고정
     */
    @JsonProperty("grant_type")
    private final String grantType = "authorization_code";

    /**
     * 앱 REST API 키
     * [내 애플리케이션] > [앱 키]에서 확인 가능
     */
    @JsonProperty("client_id")
    private final String clientId;

    /**
     * 인가 코드가 리다이렉트된 URI
     */
    @JsonProperty("redirect_uri")
    private final String redirectUri;

    /**
     * 인가 코드 받기 요청으로 얻은 인가 코드
     */
    @JsonProperty("code")
    private final String code;

    /**
     * 토큰 발급 시, 보안을 강화하기 위해 추가 확인하는 코드
     * [내 애플리케이션] > [보안]에서 설정 가능
     * ON 상태인 경우 필수 설정해야 함
     */
    @JsonProperty("client_secret")
    private final String clientSecret;


    /**
     * Fegin에서 Object는 Content Type이 application/x-www-form-urlencoded가 될 수 없다.
     *
     * @author yeh35
     * @since 2022-12-04
     */
    public Map<String, ?> toMap() {
        final var map = new HashMap<String, String>(6);
        map.put("grant_type", grantType);
        map.put("client_id", clientId);
        map.put("redirect_uri", redirectUri);
        map.put("code", code);
        map.put("client_secret", clientSecret);

        return map;
    }
}
