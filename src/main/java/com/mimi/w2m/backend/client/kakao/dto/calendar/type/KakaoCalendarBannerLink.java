package com.mimi.w2m.backend.client.kakao.dto.calendar.type;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * KakaoCalendarBannerLink : 배너와 연결된 서비스 페이지 URL
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
public record KakaoCalendarBannerLink(
        @JsonProperty(value = "web_url")
        String webUrl,
        @JsonProperty(value = "mobile_web_url")
        String mobileWebUrl
) {
}
