package com.mimi.w2m.backend.client.kakao.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * KakaoCalendarPostResponse
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/09
 **/
public record KakaoCalendarPostResponse(
        @JsonProperty(value = "calendar_id", required = true) String id
) {
}