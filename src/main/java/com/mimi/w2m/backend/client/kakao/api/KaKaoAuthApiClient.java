package com.mimi.w2m.backend.client.kakao.api;

import com.mimi.w2m.backend.client.kakao.config.KaKaoFeignConfig;
import com.mimi.w2m.backend.client.kakao.dto.token.KakaoTokenResponse;
import com.mimi.w2m.backend.config.exception.BadGatewayException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "KakaoAuthApiClient",
        url = "${external.client.kakao.oauth2.profile.base-url}",
        configuration = {KaKaoFeignConfig.class})
public interface KaKaoAuthApiClient {

    /**
     * @author yeh35
     * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token">Kakao 공식 문서</a>
     * @since 2022-12-04
     */
    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @PostMapping(path = "${external.client.kakao.oauth2.profile.token-uri}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponse getToken(@RequestBody Map<String, ?> form);

}
