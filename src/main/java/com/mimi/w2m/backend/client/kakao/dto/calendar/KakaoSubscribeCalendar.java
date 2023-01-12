package com.mimi.w2m.backend.client.kakao.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;

/**
 * KakaoSubscribeCalendar : 구독 캘린더 정보(카카오톡 채널과 연결되어야 한다)
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/08
 **/
public record KakaoSubscribeCalendar(
        /*
        캘린더 ID
         */
        @JsonProperty(value = "id", required = true)
        String id,
        /*
        캘린더 이름 : 서비스에서 만든 캘린더의 경우, requqest 에서 필수
         */
        @JsonProperty(value = "name")
        String name,
        /*
        캘린더 색상 : 서비스에서 만든 캘린더의 경우, request 에서 필수
         */
        @JsonProperty(value = "color")
        KakaoCalendarColor color,
        /*
        종일 일정이 아닌 캘린더 알림
         */
        @JsonProperty(value = "reminder")
        Integer reminder,
        /*
        종일 일정인 캘린더 알림
         */
        @JsonProperty(value = "reminder_all_day")
        Integer reminderAllDay,
        /*
        채널에서 설정한 구독 캘린더 설명
         */
        @JsonProperty(value = "description")
        String description,
        /*
        구독 캘린더의 프로필 이미지 URL
         */
        @JsonProperty(value = "profile_image_url")
        String profileImageUrl,
        /*
        구독 캘린더의 말풍선 썸네일 URL
         */
        @JsonProperty(value = "thumbnail_url")
        String thumbnailUrl
) {

}