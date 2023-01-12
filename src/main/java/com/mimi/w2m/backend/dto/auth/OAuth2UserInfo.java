package com.mimi.w2m.backend.dto.auth;

import com.mimi.w2m.backend.client.kakao.dto.user.KakaoUserInfoResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * 유저 정보
 */
@Builder
@Getter
public class OAuth2UserInfo {

    private final String name;
    private final String email;

    public static OAuth2UserInfo of(KakaoUserInfoResponse info) {
        return OAuth2UserInfo.builder()
                .name(info.getKakaoAccount().getProfile().getNickname())
                .email(info.getKakaoAccount().getEmail())
                .build();
    }
}