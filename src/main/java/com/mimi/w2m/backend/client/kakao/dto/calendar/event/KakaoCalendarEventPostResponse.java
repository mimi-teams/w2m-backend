package com.mimi.w2m.backend.client.kakao.dto.calendar.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.dto.calendar.CalendarEventPostResponse;

/**
 * KakaoCalendarEventPostResponse
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
public record KakaoCalendarEventPostResponse(
        @JsonProperty(value = "event_id")
        String id
) {
    public CalendarEventPostResponse to(String calendarId) {
        return CalendarEventPostResponse.builder()
                .eventId(id)
                .calendarId(calendarId)
                .build();
    }
}