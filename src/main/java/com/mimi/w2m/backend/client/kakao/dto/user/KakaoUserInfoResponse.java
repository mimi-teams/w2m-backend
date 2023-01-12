package com.mimi.w2m.backend.client.kakao.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoUserInfoResponse {

    /**
     * 회원번호
     */
    @JsonProperty("id")
    private long socialId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    /**
     * 카카오계정 정보
     *
     * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount">KakaoAccount</a>
     */
    @Getter
    public static class KakaoAccount {

        @JsonProperty("profile")
        private KakaoAccountProfile profile;

        @JsonProperty("is_email_valid")
        private Boolean is_email_valid;

        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;

        @JsonProperty("email")
        private String email;
    }

    /**
     * 카카오톡 유저 프로필
     */
    @Getter
    public static class KakaoAccountProfile {
        private String nickname;
    }
}
