package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.domain.type.TimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * CalendarEventTime
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
@Builder
public record CalendarEventTime(
        /*
        all_day = true 인 경우, start_at = end_at = yyyy-mm-ddT00:00:00Z 으로 설정 되어야 한다.
         */
        @JsonProperty(value = "start_at")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startAt,
        @JsonProperty(value = "end_at")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endAt,
        @Schema(title = "이벤트의 시간대", type = "String", allowableValues = {"Asia/Seoul",}, description = "Local Time Zone")
        @JsonProperty(value = "time_zone")
        TimeZone timeZone,
        @JsonProperty(value = "all_day")
        Boolean allDay,
        // 음력을 기준으로 설정(default : false)
        @JsonProperty(value = "lunar")
        Boolean lunar
) {
    public static CalendarEventTime of(TimeRange timeRange) {
        return CalendarEventTime.builder()
                .startAt(timeRange.beginTime().atDate(LocalDate.now()))
                .endAt(timeRange.endTime().atDate(LocalDate.now()))
                .timeZone(TimeZone.getTimeZone("Asia/Seoul"))
                .allDay(false)
                .lunar(false)
                .build();
    }
}