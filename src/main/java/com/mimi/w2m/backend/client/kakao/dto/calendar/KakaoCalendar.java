package com.mimi.w2m.backend.client.kakao.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;
import lombok.Builder;

/**
 * KakaoCalendar : 기본 및 서브 캘린더 정보;
 * 캘린더 생성/편집에 사용된 값만 반환된다.
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/08
 **/
@Builder
public record KakaoCalendar(
        /*
        캘린더 ID.
        기본 캘린더 = primary
        서브 캘린더 = subId
         */
        @JsonProperty(value = "id", defaultValue = "primary", required = true)
        String id,
        /*
        캘린더 이름 : 서비스에서 만든 캘린더의 경우 request 에서 필수!
         */
        @JsonProperty(value = "name")
        String name,
        /*
        캘린더 색 : 서비스에서 만든 캘린더의 경우 request 에서 필수!
         */
        @JsonProperty(value = "color")
        KakaoCalendarColor color,
        /*
        특정 시간대의 일정(ex 09:00 - 11:00) 의 기본 알림 시간
         */
        @JsonProperty(value = "reminder")
        Integer reminder,
        /*
        종일 시간대의 일정의 기본 알림 시간
         */
        @JsonProperty(value = "reminder_all_day")
        Integer reminderAllDay
) {
}