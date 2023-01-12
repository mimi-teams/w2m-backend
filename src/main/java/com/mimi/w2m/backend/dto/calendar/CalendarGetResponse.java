package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.KakaoCalendar;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;
import lombok.Builder;

import java.util.List;
/**
 * CalendarPostResponse
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
**/
@Builder
public record CalendarGetResponse(
        @JsonProperty(value = "id")
        String id,
        @JsonProperty(value = "name")
        String name,
        @JsonProperty(value = "color")
        KakaoCalendarColor color,
        @JsonProperty(value = "reminder")
        Integer reminder,
        @JsonProperty(value = "reminder_all_day")
        Integer reminderAllDay,
        @JsonProperty(value = "events")
        List<CalendarEvent> events
) {
    public static CalendarGetResponse of(KakaoCalendar calendar, List<CalendarEvent> events) {
        return CalendarGetResponse.builder()
                .id(calendar.id())
                .name(calendar.name())
                .color(calendar.color())
                .reminder(calendar.reminder())
                .reminderAllDay(calendar.reminderAllDay())
                .events(events)
                .build();
    }
}