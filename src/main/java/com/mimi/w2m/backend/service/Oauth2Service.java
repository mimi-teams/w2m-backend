package com.mimi.w2m.backend.service;

import com.mimi.w2m.backend.client.kakao.api.KaKaoApiClient;
import com.mimi.w2m.backend.client.kakao.api.KaKaoAuthApiClient;
import com.mimi.w2m.backend.client.kakao.dto.token.KakaoTokenRequest;
import com.mimi.w2m.backend.client.kakao.dto.token.KakaoTokenResponse;
import com.mimi.w2m.backend.client.kakao.dto.user.KakaoUserInfoResponse;
import com.mimi.w2m.backend.config.exception.EntityNotFoundException;
import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.domain.type.PlatformType;
import com.mimi.w2m.backend.dto.auth.OAuth2TokenInfo;
import com.mimi.w2m.backend.dto.auth.OAuth2UserInfo;
import com.mimi.w2m.backend.repository.Oauth2Repository;
import com.mimi.w2m.backend.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * OAuth2 관련 처리를 해주는 서비스
 *
 * @author yeh35
 * @since 2022-12-04
 */

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class Oauth2Service {

    private final Logger logger = LoggerFactory.getLogger("Oauth2Service");
    private final KaKaoAuthApiClient kaKaoAuthApiClient;
    private final KaKaoApiClient kaoApiClient;
    private final UserService userService;
    private final Oauth2Repository oauth2Repository;
    @Value("${external.client.kakao.oauth2.profile.base-url}")
    protected String kakaoOauth2BaseUrl;
    @Value("${external.client.kakao.oauth2.profile.authorization-uri}")
    protected String kakaoOauth2AuthorizationUrl;
    @Value("${oauth2.kakao.client-id}")
    protected String kakaoOauth2ClientId;
    @Value("${oauth2.kakao.client-secret}")
    protected String kakaoOauth2ClientSecret;
    @Value("${oauth2.kakao.redirect-url}")
    protected String kakaoOauth2RedirectUri;

    /**
     * OAuth2 로그인 URL
     *
     * @author yeh35
     * @since 2022-12-04
     */
    public String getOauthAuthorizationUrl(PlatformType platformType) {
        switch (platformType) {
            case KAKAO -> {
                return new StringBuilder(200).append(kakaoOauth2BaseUrl)
                        .append(kakaoOauth2AuthorizationUrl)
                        .append("?")
                        .append("client_id=")
                        .append(kakaoOauth2ClientId)
                        .append("&")
                        .append("redirect_uri=")
                        .append(kakaoOauth2RedirectUri)
                        .append("&")
                        .append("response_type=code")
                        .append("&")
                        .append("scope=talk_message,talk_calendar")
                        .toString();
            }
            default -> {
                assert false : "처리되지 않은 플렛폼입니다.";
                return "";
            }
        }
    }

    /**
     * OAuth2 로그인 이후 redirect 처리
     *
     * @return token
     * @author yeh35
     * @since 2022-12-04
     */
    @Transactional
    public User afterAuthorization(
            PlatformType platformType,
            String authorizationCode,
            String requestUrl
    ) {
        final OAuth2TokenInfo tokenInfo = loadToken(platformType, authorizationCode, requestUrl);
        final OAuth2UserInfo oAuth2UserInfo = loadOAuth2UserInfo(tokenInfo);


        //noinspection UnnecessaryLocalVariable
        final User user = userService.getUserByEmail(oAuth2UserInfo.getEmail()).orElseGet(() -> {
            /*
             * 회원가입 처리
             */
            return userService.registerUser(oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail());
        });
        /*
          Oauth2 Token 저장.
          Problem 1 : Delete -> Insert 를 수행할 때, Duplicated Entity Error 가 발생.
          Problem 2 : Delete -> Find -> Insert 를 수행할 때, 정상 동작.
          Reason : Hibernate 에서 The order of operations 가 정의됨(https://docs.jboss.org/hibernate/orm/4.2/javadocs/org/hibernate/event/internal/AbstractFlushingEventListener.html)
                 : 즉, Insert -> Update -> Delete 순서로 실행되며, 중간에 Find 를 수행할 때, 내부적으로 Flush 가 실행되며 정상 동작한 것이었다.
          Resolve : Delete -> Flush -> Insert 로 변경
         */
        var tokens = oauth2Repository.findByUser(user);
        oauth2Repository.deleteAll(tokens);
        oauth2Repository.flush();
        oauth2Repository.save(tokenInfo.to(user));
        return user;
    }

    /**
     * OAuth2 토큰 받아오기
     *
     * @author yeh35
     * @since 2022-12-04
     */
    protected OAuth2TokenInfo loadToken(
            PlatformType platformType,
            String authorizationCode,
            String requestUrl
    ) {
        switch (platformType) {
            case KAKAO -> {
                final var tokenRequest = KakaoTokenRequest.builder()
                        .clientId(kakaoOauth2ClientId)
                        .clientSecret(kakaoOauth2ClientSecret)
                        .redirectUri(requestUrl)
                        .code(authorizationCode)
                        .build();


                KakaoTokenResponse tokenResponse = kaKaoAuthApiClient.getToken(tokenRequest.toMap());
                return OAuth2TokenInfo.of(tokenResponse);
            }
            default -> {
                assert false : "처리되지 않은 플렛폼입니다.";
                return null;
            }
        }
    }

    /**
     * 유저 정보 가져오기
     *
     * @author yeh35
     * @since 2022-12-04
     */
    protected OAuth2UserInfo loadOAuth2UserInfo(OAuth2TokenInfo tokenInfo) {
        switch (tokenInfo.getPlatform()) {
            case KAKAO -> {
                KakaoUserInfoResponse userInfo = kaoApiClient.getUserInfo(HttpUtils.withBearerToken(tokenInfo.getAccessToken()));
                return OAuth2UserInfo.of(userInfo);
            }
            default -> {
                assert false : "처리되지 않은 플렛폼입니다.";
                return null;
            }
        }
    }

    /**
     * Oauth2 Token 가져오기
     *
     * @author teddy
     * @since 2023/01/08
     **/
    public OAuth2TokenInfo getToken(UUID userId) {
        final var user = userService.getUser(userId);
        final var oauth2Token = oauth2Repository.findByUser(user);
        if (oauth2Token.isEmpty()) {
            throw new EntityNotFoundException(String.format("[Oauth2Service] There are no token for %s", user), "이용자의 Oauth Token 이 없습니다.");
        }
        return OAuth2TokenInfo.of(oauth2Token.get(0));
    }
}