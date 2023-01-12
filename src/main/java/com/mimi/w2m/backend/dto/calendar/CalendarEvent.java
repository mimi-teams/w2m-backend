package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * CalendarEvent
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
**/
@Builder
public record CalendarEvent(
        @JsonProperty(value = "id", required = true)
        String eventId,
        @JsonProperty(value = "title", required = true)
        String title
) {
}
