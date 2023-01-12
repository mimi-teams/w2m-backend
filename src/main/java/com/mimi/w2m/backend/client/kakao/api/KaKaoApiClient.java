package com.mimi.w2m.backend.client.kakao.api;

import com.mimi.w2m.backend.client.kakao.config.KaKaoFeignConfig;
import com.mimi.w2m.backend.client.kakao.dto.calendar.KakaoCalendarGetResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.KakaoCalendarPostResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.KakaoCalendarEvent;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.KakaoCalendarEventGetResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.KakaoCalendarEventPostResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.type.KakaoCalendarEventRecurrentUpdateType;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarColor;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarType;
import com.mimi.w2m.backend.client.kakao.dto.user.KakaoUserInfoResponse;
import com.mimi.w2m.backend.config.exception.BadGatewayException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "KakaoApiClient",
        url = "${external.client.kakao.kapi.profile.base-url}",
        configuration = {KaKaoFeignConfig.class})
public interface KaKaoApiClient {

    /**
     * @author yeh35
     * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info">Kakao 공식 문서</a>
     * @since 2022-12-04
     */
    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @PostMapping("${external.client.kakao.kapi.profile.user-info-uri}")
    KakaoUserInfoResponse getUserInfo(
            @RequestHeader("Authorization") String accessToken
    );

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @GetMapping("/v2/api/calendar/calendars")
    KakaoCalendarGetResponse getCalenders(
            @RequestHeader("Authorization") String accessToken,
            /*
            목록을 가져올 캘린더 타입
             */
            @RequestParam(name = "filter", required = false) KakaoCalendarType filter
    );

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @PostMapping("/v2/api/calendar/create/calendar")
    KakaoCalendarPostResponse createCalendar(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "color", required = false) KakaoCalendarColor color,
            @RequestParam(name = "reminder", required = false) Integer reminder,
            @RequestParam(name = "reminder_all_day", required = false) Integer reminderAllDay);

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @PostMapping("/v2/api/calendar/update/calendar")
    KakaoCalendarPostResponse updateCalendar(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "calendar_id") String calendarId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "color", required = false) KakaoCalendarColor color,
            @RequestParam(name = "reminder", required = false) Integer reminder,
            @RequestParam(name = "reminder_all_day", required = false) Integer reminderAllDay);

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @DeleteMapping("/v2/api/calendar/delete/calendar")
    KakaoCalendarPostResponse deleteCalendar(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "calendar_id") String calendarId);

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @PostMapping("/v2/api/calendar/create/event")
    KakaoCalendarEventPostResponse createCalendarEvent(
            @RequestHeader("Authorization") String withBearerToken,
            @RequestParam(name = "calendar_id") String calendarId,
            @RequestParam(name = "event") String kakaoEventPostRequest);

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @GetMapping("/v2/api/calendar/event")
    KakaoCalendarEventGetResponse getCalendarEvent(
            @RequestHeader("Authorization") String withBearerToken,
            @RequestParam(name = "event_id") String eventId
    );

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @PostMapping("/v2/api/calendar/update/event/host")
    KakaoCalendarEventPostResponse updateCalendarEvent(
            @RequestHeader("Authorization") String withBearerToken,
            @RequestParam(name = "event_id") String eventId,
            @RequestParam(name = "recur_update_type") KakaoCalendarEventRecurrentUpdateType updateType,
            @RequestParam(name = "event") KakaoCalendarEvent event
    );

    @Retryable(backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 1000), value = BadGatewayException.class)
    @DeleteMapping("/v2/api/calendar/delete/event")
    void deleteCalendarEvent(
            @RequestHeader("Authorization") String withBearerToken,
            @RequestParam(name = "event_id") String eventId,
            @RequestParam(name = "recur_update_type") KakaoCalendarEventRecurrentUpdateType updateType
    );
}
