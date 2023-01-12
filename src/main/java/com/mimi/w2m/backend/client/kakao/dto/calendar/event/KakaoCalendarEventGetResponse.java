package com.mimi.w2m.backend.client.kakao.dto.calendar.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * KakaoCalendarEventGetResponse
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
@Builder
public record KakaoCalendarEventGetResponse(
        @JsonProperty(value = "event")
        KakaoCalendarEvent event
) {
}
