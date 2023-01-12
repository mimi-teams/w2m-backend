package com.mimi.w2m.backend.api.v1;

import com.mimi.w2m.backend.client.kakao.dto.calendar.event.KakaoCalendarEvent;
import com.mimi.w2m.backend.client.kakao.dto.calendar.type.KakaoCalendarType;
import com.mimi.w2m.backend.client.kakao.service.KakaoService;
import com.mimi.w2m.backend.config.exception.EntityNotFoundException;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.config.interceptor.Auth;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.EventParticipant;
import com.mimi.w2m.backend.domain.type.PlatformType;
import com.mimi.w2m.backend.domain.type.Role;
import com.mimi.w2m.backend.dto.base.ApiCallResponse;
import com.mimi.w2m.backend.dto.calendar.CalendarEventPostResponse;
import com.mimi.w2m.backend.dto.calendar.CalendarGetResponse;
import com.mimi.w2m.backend.dto.calendar.CalendarPostRequest;
import com.mimi.w2m.backend.dto.calendar.CalendarPostResponse;
import com.mimi.w2m.backend.dto.event.EventActivityTimeDto;
import com.mimi.w2m.backend.dto.event.EventGetResponse;
import com.mimi.w2m.backend.dto.event.EventRequestDto;
import com.mimi.w2m.backend.dto.event.EventResponseDto;
import com.mimi.w2m.backend.dto.participant.EventParticipantDto;
import com.mimi.w2m.backend.dto.participant.EventParticipantRequest;
import com.mimi.w2m.backend.dto.participant.EventParticipantRequestDto;
import com.mimi.w2m.backend.dto.participant.guest.GuestCreateDto;
import com.mimi.w2m.backend.dto.participant.guest.GuestLoginRequest;
import com.mimi.w2m.backend.dto.participant.guest.GuestLoginResponse;
import com.mimi.w2m.backend.dto.participant.guest.GuestOneResponseDto;
import com.mimi.w2m.backend.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * EventApi
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/27
 **/
@Tag(name = "Event Api", description = "Event 와 관련된 Api")
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/v1/events")
@RestController
public class EventApi {
    private final GuestService guestService;
    private final EventParticipantService eventParticipantService;

    private final EventService eventService;
    private final KakaoService kakaoService;
    private final AuthService authService;
    private final Oauth2Service oauth2Service;
    private final UserService userService;

    @Operation(summary = "이벤트 정보 반환", description = "ID에 해당하는 이벤트 정보를 반환한다. 이벤트 참여자만 이용할 수 있다")
    @GetMapping("/{eventId}")
    public @Valid ApiCallResponse<EventGetResponse> get(
            @Parameter(name = "eventId", description = "이벤트의 ID", in = ParameterIn.PATH, required = true)
            @NotNull @Valid @PathVariable("eventId") UUID eventId
    ) {
        final var event = eventService.getEvent(eventId);
        final var eventSelectableParticipleTimes = eventService.getEventSelectableParticipleTimes(eventId);
        final List<EventParticipantDto> eventParticipants = eventParticipantService.getEventParticipants(eventId);

        return ApiCallResponse.ofSuccess(EventGetResponse.of(event, eventSelectableParticipleTimes, eventParticipants));
    }

    @Operation(summary = "[인증]이용자가 생성한 모든 이벤트 반환", description = "이용자가 생성한 모든 이벤트 정보를 반환한다. 해당 이용자만 가능하다.")
    @Auth
    @GetMapping("/host")
    public @Valid ApiCallResponse<List<EventGetResponse>> getAllByHost() {
        final var currentUserInfo = authService.getCurrentUserInfo();
        final var eventGetResponses = eventService.getAllByHost(currentUserInfo.userId()).stream()
                .map((Event event) -> {
                    final var eventSelectableParticipleTimes = eventService.getEventSelectableParticipleTimes(event.getId());
                    final var eventParticipants = eventParticipantService.getEventParticipants(event.getId());
                    return EventGetResponse.of(event, eventSelectableParticipleTimes, eventParticipants);
                })
                .toList();
        return ApiCallResponse.ofSuccess(eventGetResponses);
    }

    @Operation(summary = "[인증] 새로운 이벤트 등록", description = "새로운 이벤트를 등록한다. 가입한 이용자만 이용할 수 있다")
    @Auth
    @PostMapping("")
    public @Valid ApiCallResponse<EventResponseDto> createEvent(
            @Valid @RequestBody EventRequestDto requestDto
    ) {
        final var currentUserInfo = authService.getCurrentUserInfo();

        final var event = eventService.createEvent(currentUserInfo.userId(), requestDto);
        final var eventSelectableParticipleTimes = eventService.getEventSelectableParticipleTimes(event.getId());

        return ApiCallResponse.ofSuccess(EventResponseDto.of(event, eventSelectableParticipleTimes));
    }

    @Operation(summary = "[인증] 이벤트 정보 수정", description = "ID에 해당하는 이벤트 정보를 수정한다. 이벤트 생성자만 수정 가능한다")
    @Auth
    @PatchMapping(path = "/{eventId}")
    public @Valid ApiCallResponse<EventResponseDto> patch(
            @Parameter(name = "eventId", description = "이벤트의 ID", in = ParameterIn.PATH, required = true)
            @NotNull @Valid @PathVariable("eventId") UUID eventId,
            @Valid @RequestBody EventRequestDto requestDto
    ) {
        final var currentUserInfo = authService.getCurrentUserInfo();
        authService.isHost(currentUserInfo.userId(), eventId);

        final var event = eventService.modifyEvent(eventId, requestDto);
        final var eventSelectableParticipleTimes = eventService.getEventSelectableParticipleTimes(eventId);

        return ApiCallResponse.ofSuccess(EventResponseDto.of(event, eventSelectableParticipleTimes));
    }

    @Operation(summary = "[인증] 이벤트 활동 시간 수정", description = "ID에 해당하는 이벤트의 활동 시간을 설정한다. 이벤트 생성자만 설정 가능하다")
    @Auth
    @PatchMapping(path = "/{eventId}/activity")
    public @Valid ApiCallResponse<EventResponseDto> patchActivity(
            @NotNull @Valid @PathVariable("eventId") UUID eventId,
            @NotNull @RequestBody EventActivityTimeDto requestDto
    ) {
        final var currentUserInfo = authService.getCurrentUserInfo();
        authService.isHost(currentUserInfo.userId(), eventId);

        final var event = eventService.modifyActivity(eventId, requestDto);
        final var eventSelectableParticipleTimes = eventService.getEventSelectableParticipleTimes(eventId);
        return ApiCallResponse.ofSuccess(EventResponseDto.of(event, eventSelectableParticipleTimes));
    }

    @Operation(summary = "[인증]이벤트 삭제",
            description = "ID에 해당하는 이벤트를 삭제한다. 이벤트 생성자만 가능하며, 삭제 시 Event 와 연관된 정보가 모두 삭제된다. 삭제 후 처리는 클라이언트에서 해주세요")
    @Auth
    @DeleteMapping(path = "/{eventId}")
    public ApiCallResponse<?> delete(
            @Parameter(name = "eventId", description = "이벤트의 ID", in = ParameterIn.PATH, required = true)
            @NotNull @Valid @PathVariable("eventId") UUID eventId
    ) {

        final var currentUserInfo = authService.getCurrentUserInfo();
        authService.isHost(currentUserInfo.userId(), eventId);

        eventService.delete(eventId);

        return ApiCallResponse.ofSuccess(null);
    }

    @Operation(summary = "이벤트 게스트 로그인", description = "없는 유저의 경우 자동으로 등록된다.")
    @PostMapping("/{eventId}/guests/login")
    public @Valid ApiCallResponse<GuestLoginResponse> login(
            @Parameter(description = "이벤트의 ID", required = true) @NotNull @Valid @PathVariable UUID eventId,
            @Valid @RequestBody GuestLoginRequest requestBody
    ) {
        if (!guestService.isUsedGuestName(eventId, requestBody.getName())) {
            final var createDto = GuestCreateDto.builder()
                    .eventId(eventId)
                    .name(requestBody.getName())
                    .password(requestBody.getPassword())
                    .build();
            guestService.create(createDto);
        }

        final GuestLoginResponse loginResponse = guestService.login(eventId, requestBody);
        return ApiCallResponse.ofSuccess(loginResponse);
    }

    @Operation(summary = "[인증] 게스트의 이벤트 참여", description = "다시 등록하고 싶으면 이 API를 다시 호출하면 된다. Delete & Insert")
    @Auth(Role.GUEST)
    @PostMapping("/{eventId}/guests/participate")
    public @Valid ApiCallResponse<GuestOneResponseDto> guestParticipate(
            @Parameter(description = "이벤트의 ID", required = true) @NotNull @Valid @PathVariable UUID eventId,
            @Valid @RequestBody EventParticipantRequest requestDto
    ) {
        final var currentUserInfo = authService.getCurrentUserInfo();

        final var eventParticipantRequest = EventParticipantRequestDto.builder()
                .eventId(eventId)
                .ownerId(currentUserInfo.userId())
                .ownerType(currentUserInfo.role())
                .ableDaysAndTimes(requestDto.getAbleDaysAndTimes())
                .build();

        final EventParticipant participate = eventParticipantService.participate(eventParticipantRequest);

        return ApiCallResponse.ofSuccess(null);
    }

    @Operation(summary = "[인증] 유저의 이벤트 참여", description = "다시 등록하고 싶으면 이 API를 다시 호출하면 된다. Delete & Insert")
    @Auth(Role.USER)
    @PostMapping("/{eventId}/user/participate")
    public @Valid ApiCallResponse<GuestOneResponseDto> userParticipate(
            @Parameter(description = "이벤트의 ID", required = true) @NotNull @Valid @PathVariable UUID eventId,
            @Valid @RequestBody EventParticipantRequest requestDto
    ) {
        final var currentUserInfo = authService.getCurrentUserInfo();

        final var eventParticipantRequest = EventParticipantRequestDto.builder()
                .eventId(eventId)
                .ownerId(currentUserInfo.userId())
                .ownerType(currentUserInfo.role())
                .ableDaysAndTimes(requestDto.getAbleDaysAndTimes())
                .build();

        eventParticipantService.participate(eventParticipantRequest);

        return ApiCallResponse.ofSuccess(null);
    }

    /**
     * 외부 캘린더와 이벤트 연동. 유저(카카오)로 로그인을 해야 한다.
     *
     * @author teddy
     * @since 2023/01/10
     **/
    @Operation(summary = "[인증] 외부 캘린더 조회", description = "외부 캘린더 정보를 조회한다.")
    @Auth(Role.USER)
    @GetMapping("/calendar")
    public @Valid ApiCallResponse<List<CalendarGetResponse>> getCalendars(
            @Parameter(name = "platform", description = "외부 플랫폼. 이용자 정보에 등록되어야 한다.", in = ParameterIn.QUERY, required = true)
            @Valid @NotNull @RequestParam(value = "platform") PlatformType platform
    ) {
        final var currentUser = authService.getCurrentUserInfo();
        final var oAuth2TokenInfo = oauth2Service.getToken(currentUser.userId());
        List<CalendarGetResponse> calendars = List.of();
        switch (platform) {
            case KAKAO -> {
                final var kakaoCalendars = kakaoService.getKakaoCalendars(oAuth2TokenInfo.getAccessToken(), KakaoCalendarType.USER);
                calendars = kakaoCalendars.calendars().stream()
                        .map(kakaoCalendar -> {
                            final var events = kakaoService.getAllCalendarEvents(oAuth2TokenInfo.getAccessToken(), currentUser.userId(), kakaoCalendar.id())
                                    .stream().map(KakaoCalendarEvent::to).toList();
                            return CalendarGetResponse.of(kakaoCalendar, events);
                        }).toList();
            }
            default -> {
                throw new InvalidValueException(String.format("Undefined platform : %s", platform.name()), "유효하지 않은 플랫폼입니다.");
            }
        }
        return ApiCallResponse.ofSuccess(calendars);
    }

    @Operation(summary = "[인증] 한 외부 캘린더 조회", description = "ID 와 일치하는 캘린더 정보를 조회한다.")
    @Auth(Role.USER)
    @GetMapping("/calendar/{calendarId}")
    public @Valid ApiCallResponse<CalendarGetResponse> getCalendar(
            @Parameter(name = "platform", description = "외부 플랫폼. 이용자 정보에 등록되어야 한다.", in = ParameterIn.QUERY, required = true)
            @Valid @NotNull @RequestParam(value = "platform") PlatformType platform,
            @Parameter(name = "calendarId", description = "플랫폼의 캘린더 ID", in = ParameterIn.PATH, required = true)
            @Valid @NotNull @PathVariable String calendarId
    ) {
        final var currentUser = authService.getCurrentUserInfo();
        final var oAuth2TokenInfo = oauth2Service.getToken(currentUser.userId());
        CalendarGetResponse calendar = null;
        switch (platform) {
            case KAKAO -> {
                final var kakaoCalendars = kakaoService.getKakaoCalendars(oAuth2TokenInfo.getAccessToken(), KakaoCalendarType.USER);
                final var kakaoCalendar = kakaoCalendars.calendars().stream()
                        .filter(c -> c.id().equals(calendarId)).findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Calendar Not Found[%s : %s]", platform.name(), calendarId), "캘린더가 없습니다."));
                final var events = kakaoService.getAllCalendarEvents(oAuth2TokenInfo.getAccessToken(), currentUser.userId(), calendarId)
                        .stream().map(KakaoCalendarEvent::to).toList();
                calendar = CalendarGetResponse.of(kakaoCalendar, events);
            }
            default -> {
                throw new InvalidValueException(String.format("Undefined platform : %s", platform.name()), "유효하지 않은 플랫폼입니다.");
            }
        }
        return ApiCallResponse.ofSuccess(calendar);
    }

    @Operation(summary = "[인증] 외부 캘린더 생성", description = "Meezle 에서 생성된 이벤트를 등록하기 위한 외부 캘린더를 생성한다. 이름이 동일한 캘린더가 있는 경우, 오류를 반환한다.")
    @Auth
    @PostMapping("/calendar")
    public @Valid ApiCallResponse<CalendarPostResponse> createCalendar(
            @Parameter(name = "platform", description = "외부 플랫폼. 이용자 정보에 등록되어야 한다.", in = ParameterIn.QUERY, required = true)
            @Valid @NotNull @RequestParam(value = "platform") PlatformType platform,
            @Valid @RequestBody CalendarPostRequest body
    ) {
        final var currentUser = authService.getCurrentUserInfo();
        final var oAuth2TokenInfo = oauth2Service.getToken(currentUser.userId());
        CalendarPostResponse calendar = null;
        switch (platform) {
            case KAKAO -> {
                final var kakaoCalendar = kakaoService.createKakaoCalendar(oAuth2TokenInfo.getAccessToken(), body);
                calendar = CalendarPostResponse.of(kakaoCalendar);
            }
            default -> {
                throw new InvalidValueException(String.format("Undefined platform : %s", platform.name()), "유효하지 않은 플랫폼입니다.");
            }
        }
        return ApiCallResponse.ofSuccess(calendar);
    }

    @Operation(summary = "[인증] 외부 캘린더에 이벤트 등록", description = "ID에 해당하는 이벤트를 외부 플랫폼 캘린더에 등록한다. 이벤트 시간이 설정 되어있어야 한다. 이벤트 참여자 중, 가입자만 이용할 수 있다")
    @Auth
    @PostMapping(path = "/{eventId}/calendar/{calendarId}")
    public @Valid ApiCallResponse<CalendarEventPostResponse> createCalendarEvent(
            @Parameter(name = "platform", description = "외부 플랫폼. 이용자 정보에 등록되어야 한다.", in = ParameterIn.QUERY, required = true)
            @Valid @NotNull @RequestParam(value = "platform") PlatformType platform,
            @Parameter(name = "calendarId", description = "플랫폼의 캘린더 ID", in = ParameterIn.PATH, required = true)
            @Valid @NotNull @PathVariable String calendarId,
            @Valid @NotNull @PathVariable UUID eventId
    ) {
        final var currentUser = authService.getCurrentUserInfo();
        authService.isInEvent(currentUser, eventId);

        final var user = userService.getUser(currentUser.userId());
        final var event = eventService.getEvent(eventId);
        final var oAuth2TokenInfo = oauth2Service.getToken(currentUser.userId());
        CalendarEventPostResponse response = null;
        switch (platform) {
            case KAKAO ->
                    response = kakaoService.createKakaoCalendarEvent(oAuth2TokenInfo.getAccessToken(), calendarId, user, event, platform).to(calendarId);
            default -> {
                throw new InvalidValueException(String.format("Undefined platform : %s", platform.name()), "유효하지 않은 플랫폼입니다.");
            }
        }
        return ApiCallResponse.ofSuccess(response);
    }
}