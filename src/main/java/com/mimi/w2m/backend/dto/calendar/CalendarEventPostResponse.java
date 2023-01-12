package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * CalendarEventPostResponse
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/

@Builder
public record CalendarEventPostResponse(
        @JsonProperty("calendar_id")
        String calendarId,
        @JsonProperty("event_id")
        String eventId
) {
}