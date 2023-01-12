package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CalendarPostRequest
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
@Builder
public record CalendarPostRequest(
        @Size(min = 1, max = 50) @NotNull
        @JsonProperty(value = "name")
        String name,
        @Valid
        @JsonProperty(value = "color")
        KakaoCalendarColor color,
        @JsonProperty(value = "reminder")
        Integer reminder,
        @JsonProperty(value = "reminder_all_day")
        Integer reminderAllDay
) {

}