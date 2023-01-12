package com.mimi.w2m.backend.client.kakao.dto.calendar.type;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * KakaoCalendarEventBanner : 공개나 구독 일정 상단에 표시되는 배너
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
public record KakaoCalendarBanner(
        @JsonProperty(value = "pc_image_url")
        String pcImageUrl,
        @JsonProperty(value = "mobile_image_url")
        String mobileImageUrl,
        @JsonProperty(value = "bg_color")
        KakaoCalendarColor color, //일정 메시지에 여백이 있을 경우, 채워지는 배경 색상
        @JsonProperty(value = "link")
        KakaoCalendarBannerLink link

) {
}
