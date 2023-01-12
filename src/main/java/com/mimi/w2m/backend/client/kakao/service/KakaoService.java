package com.mimi.w2m.backend.client.kakao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mimi.w2m.backend.client.kakao.api.KaKaoApiClient;
import com.mimi.w2m.backend.client.kakao.dto.calendar.KakaoCalendarGetResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.KakaoCalendarPostResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.KakaoCalendarEvent;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.KakaoCalendarEventPostResponse;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarType;
import com.mimi.w2m.backend.config.exception.EntityDuplicatedException;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.domain.Calendar;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.domain.type.PlatformType;
import com.mimi.w2m.backend.dto.calendar.CalendarPostRequest;
import com.mimi.w2m.backend.repository.*;
import com.mimi.w2m.backend.service.EventService;
import com.mimi.w2m.backend.service.UserService;
import com.mimi.w2m.backend.utils.HttpUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * KakaoService
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/09
 **/
@Service
@Transactional(readOnly = true)
public class KakaoService extends EventService {
    private final KaKaoApiClient kakaoApiClient;

    public KakaoService(UserService userService, EventRepository eventRepository, EventSelectableParticipleTimeRepository eventSelectableParticipleTimeRepository, GuestRepository guestRepository, EventParticipantRepository eventParticipantRepository, EventParticipantAbleTimeRepository eventParticipantAbleTimeRepository, CalendarRepository calendarRepository, KaKaoApiClient kakaoApiClient) {
        super(userService, eventRepository, eventSelectableParticipleTimeRepository, guestRepository, eventParticipantRepository, eventParticipantAbleTimeRepository, calendarRepository);
        this.kakaoApiClient = kakaoApiClient;
    }


    public KakaoCalendarGetResponse getKakaoCalendars(String accessToken, KakaoCalendarType filter) {
        return kakaoApiClient.getCalenders(HttpUtils.withBearerToken(accessToken), filter);
    }

    public KakaoCalendarPostResponse createKakaoCalendar(String accessToken, CalendarPostRequest request) {
        return kakaoApiClient
                .createCalendar(HttpUtils.withBearerToken(accessToken), request.name(), request.color(), request.reminder(), request.reminderAllDay());
    }

    @Transactional
    public KakaoCalendarEventPostResponse createKakaoCalendarEvent(String accessToken, String calendarId, User user, Event event, PlatformType platform) {
        if (Objects.isNull(event.getActivityDays()) || event.getActivityDays().isEmpty()
                || Objects.isNull(event.getActivityTimeRange())) {
            throw new InvalidValueException("[KakaoService] Event Activity Time doesn't exist", "이벤트에 설정된 활동 시간이 없습니다");
        }
        if (calendarRepository.findByUserAndEventInPlatform(user, event, PlatformType.KAKAO).isPresent()) {
            throw new EntityDuplicatedException(String.format("[KakaoService] (user=%s, event=%s) already exist", user.getId(), event.getId()), "동일한 이벤트가 존재합니다");
        } else {
            try {
                final var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                final var kakaoEventPostRequest = KakaoCalendarEvent.of(event);
                final var kakaoRequest = mapper.writeValueAsString(kakaoEventPostRequest);
                final var response = kakaoApiClient.createCalendarEvent(HttpUtils.withBearerToken(accessToken), calendarId, kakaoRequest);

                // 반복 일정으로 생성되는 경우, 반환된 ID + '_' + 이벤트 요일로 반복된 이벤트들의 ID 가 설정된다.
                final var calendarEventEarliestDay = event.getActivityDays().stream()
                        .map(dayOfWeek -> LocalDate.now().with(TemporalAdjusters.next(dayOfWeek)))
                        .min(LocalDate::compareTo).get().atTime(event.getActivityTimeRange().beginTime());
                final var calendarEventId = response.id() + "_" + calendarEventEarliestDay.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));

                calendarRepository.save(Calendar.builder()
                        .user(user)
                        .event(event)
                        .platform(platform)
                        .platformCalendarId(calendarId)
                        .platformEventId(calendarEventId)
                        .build());
                return new KakaoCalendarEventPostResponse(calendarEventId);
            } catch (JsonProcessingException e) {
                throw new InvalidValueException(e.getMessage());
            }
        }
    }

    public List<KakaoCalendarEvent> getAllCalendarEvents(String accessToken, UUID userId, String calendarId) {
        final var user = userService.getUser(userId);
        final var calendars = calendarRepository.findAllByUserInPlatformCalendar(user, PlatformType.KAKAO, calendarId);
        if (calendars.isEmpty()) {
            return List.of();
        } else {
            return calendars.stream().map(calendar -> getCalendarEvent(accessToken, calendar.getPlatformEventId())).toList();
        }

    }

    public KakaoCalendarEvent getCalendarEvent(String accessToken, String id) {
        // TODO: 2023/01/12 이용자가 자신의 톡캘린더에서 이벤트를 직접 지우는 경우, API 는 Bad Request 를 반환한다. 이때, 적절한 처리를 할 필요가 있다.
        return kakaoApiClient.getCalendarEvent(HttpUtils.withBearerToken(accessToken), id).event();
    }
}