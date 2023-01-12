package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.KakaoCalendarPostResponse;
import lombok.Builder;

/**
 * CalendarPostResponse
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/09
 **/
@Builder
public record CalendarPostResponse(
        @JsonProperty(value = "id")
        String id
) {
    public static CalendarPostResponse of(KakaoCalendarPostResponse calendar) {
        return CalendarPostResponse.builder()
                .id(calendar.id())
                .build();
    }
}