package com.mimi.w2m.backend.client.kakao.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * KakaoCalendarGetResponse
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/08
 **/
public record KakaoCalendarGetResponse(
        @JsonProperty(value = "calendars")
        List<KakaoCalendar> calendars,
        @JsonProperty(value = "subscribe_calendars")
        List<KakaoSubscribeCalendar> subscribeCalendars
) {

}