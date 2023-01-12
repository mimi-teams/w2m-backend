package com.mimi.w2m.backend.client.kakao.dto.calendar.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.type.KakaoCalendarEventLocation;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.dto.calendar.CalendarEvent;
import com.mimi.w2m.backend.dto.calendar.CalendarEventTime;
import com.mimi.w2m.backend.dto.calendar.CalendarRRule;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * KakaoCalendarEvent
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
@Builder
public record KakaoCalendarEvent(
        @JsonProperty(value = "event_id")
        String eventId,
        @Size(min = 1, max = 50)
        @JsonProperty(value = "title")
        String title,
        @Valid
        @JsonProperty(value = "time")
        KakaoCalendarEventTime time,
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
        @JsonProperty(value = "color")
        KakaoCalendarColor color

) {
    public static KakaoCalendarEvent of(Event event) {
        return KakaoCalendarEvent.builder()
                .title(event.getTitle())
                .time(KakaoCalendarEventTime.of(CalendarEventTime.of(event.getActivityTimeRange())))
                .rRule(CalendarRRule.of(event.getActivityDays()))
                .description(event.getDescription())
                .location(null)
                .reminders(null)
                .color(null)
                .build();
    }

    public CalendarEvent to() {
            return CalendarEvent.builder()
                    .eventId(eventId)
                    .title(title)
                    .build();
    }
}
