package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.type.KakaoCalendarEventLocation;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * CalendarEventPostRequest
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
@Builder
public record CalendarEventPostRequest(
        @NotNull
        @Size(min = 1, max = 50)
        @JsonProperty(value = "title")
        String title,
        @NotNull
        @Valid
        @JsonProperty(value = "time")
        CalendarEventTime time,
        @Valid
        @JsonProperty(value = "rrule")
        CalendarRRule rRule,
        @Size(max = 5000)
        @JsonProperty(value = "description")
        String description,
        @JsonProperty(value = "location")
        KakaoCalendarEventLocation location,
        @Size(max = 2)
        @JsonProperty(value = "reminders")
        List<Integer> reminders,
        @Valid
        @JsonProperty(value = "color")
        KakaoCalendarColor color
) {
}