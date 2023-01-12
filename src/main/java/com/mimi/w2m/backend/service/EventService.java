package com.mimi.w2m.backend.service;

import com.mimi.w2m.backend.config.exception.EntityNotFoundException;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.EventParticipant;
import com.mimi.w2m.backend.domain.EventSelectableParticipleTime;
import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.domain.type.TimeRange;
import com.mimi.w2m.backend.dto.event.EventActivityTimeDto;
import com.mimi.w2m.backend.dto.event.EventRequestDto;
import com.mimi.w2m.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 이벤트 처리를 책임지는 서비스
 *
 * @author yeh35
 * @since 2022-10-31
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    protected final Logger logger = LoggerFactory.getLogger(EventService.class.getName());
    protected final UserService userService;
    protected final EventRepository eventRepository;
    protected final EventSelectableParticipleTimeRepository eventSelectableParticipleTimeRepository;
    protected final GuestRepository guestRepository;
    protected final EventParticipantRepository eventParticipantRepository;
    protected final EventParticipantAbleTimeRepository eventParticipantAbleTimeRepository;
    protected final CalendarRepository calendarRepository;

    /**
     * 이벤트 생성(Host 는 EventParticipant 에 추가된다)
     *
     * @author yeh35
     * @since 2022-10-31
     */
    @Transactional
    public Event createEvent(UUID hostId, EventRequestDto requestDto) throws EntityNotFoundException {
        final var host = userService.getUser(hostId);
        final var event = eventRepository.save(requestDto.to(host));
        eventParticipantRepository.save(EventParticipant.ofUser(event, host));

        final var selectableParticipleTimes = EventSelectableParticipleTime.of(event, requestDto.getSelectableParticipleTimes().toParticipleTimeSet());
        eventSelectableParticipleTimeRepository.saveAll(selectableParticipleTimes);

        return event;
    }

    /**
     * 이벤트 수정 : host 는 항상 이벤트의 정보를 수정할 수 있다(D-day, title, description, time 등등)
     *
     * @author yeh35
     * @since 2022-10-31
     */
    @Transactional
    public Event modifyEvent(UUID eventId, EventRequestDto requestDto) throws EntityNotFoundException {
        var event = getEvent(eventId);
        event.update(
                requestDto.getTitle(),
                requestDto.getDDay(),
                requestDto.getColor(),
                requestDto.getDescription()
        );

        eventSelectableParticipleTimeRepository.deleteByEvent(event);
        eventSelectableParticipleTimeRepository.saveAll(EventSelectableParticipleTime.of(event, requestDto.getSelectableParticipleTimes().toParticipleTimeSet()));

        return event;
    }

    /**
     * @author yeh35
     * @since 2022-11-05
     */
    public Event getEvent(UUID id) throws EntityNotFoundException {
        return eventRepository.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException(String.format("[EventService] Entity Not Found(id=%s)", id));
        });
    }

    /**
     * @author yeh35
     * @since 2022-11-05
     */
    public Set<ParticipleTime> getEventSelectableParticipleTimes(UUID eventId) throws EntityNotFoundException {
        final var event = getEvent(eventId);

        return eventSelectableParticipleTimeRepository.findByEvent(event)
                .stream()
                .map(EventSelectableParticipleTime::toParticipleTime)
                .collect(Collectors.toSet());

    }


    /**
     * 이벤트 삭제하기,
     *
     * @author teddy
     * @since 2022/11/27
     **/
    @Transactional
    public void delete(UUID eventId) throws EntityNotFoundException {
        final var event = getEvent(eventId);

        calendarRepository.deleteByEvent(event);
        eventParticipantAbleTimeRepository.deleteByEventParticipantList(eventParticipantRepository.findAllInEvent(event));
        eventParticipantRepository.deleteByEvent(event);
        guestRepository.deleteByEvent(event);
        eventSelectableParticipleTimeRepository.deleteByEvent(event);
        eventRepository.delete(event);
    }

    /**
     * 이벤트 조회, 삭제된 이벤트는 조회되지 않는다
     *
     * @author yeh35
     * @since 2022-10-31
     */
    public List<Event> getAllByTitle(String title) throws EntityNotFoundException {
        final var events = eventRepository.findAllByTitle(title)
                .stream()
                .filter(event -> Objects.isNull(event.getDeletedAt()) || event.getDeletedAt()
                        .isAfter(
                                LocalDateTime.now()))
                .toList();
        if (events.isEmpty()) {
            final var msg = String.format("[EventService] Entity Not Found(title=%s)", title);
            throw new EntityNotFoundException(msg);
        } else {
            return events;
        }
    }

    /**
     * 삭제된 이벤트도 모두 조회된다
     *
     * @author teddy
     * @since 2022/11/21
     **/
    public List<Event> getAllByHost(UUID hostId) throws EntityNotFoundException {
        final var user = userService.getUser(hostId);
        final var events = eventRepository.findAllByHost(user);
        if (events.isEmpty()) {
            final var msg = String.format("[EventService] Entity Not Found(host=%s)", hostId);
            throw new EntityNotFoundException(msg);
        } else {
            return events;
        }
    }

    @Transactional
    public void deleteAll(List<Event> events) {
        eventRepository.deleteAll(events);
    }

    @Transactional
    public Event modifyActivity(UUID eventId, EventActivityTimeDto requestDto) throws InvalidValueException {
        final var event = getEvent(eventId);
        if (Objects.isNull(requestDto.activityTime())) {
            throw new InvalidValueException("[EventService] Empty activityTime", "선택된 활동 시간이 없습니다");
        }
        if (Objects.isNull(requestDto.activityTime().getSelectedDayOfWeeks())) {
            throw new InvalidValueException("[EventService] Empty selected days", "선택된 요일이 없습니다");
        }
        final var days = requestDto.activityTime().getSelectedDayOfWeeks();
        final var timeRange = new TimeRange(requestDto.activityTime().getBeginTime(), requestDto.activityTime().getEndTime());

        //Selectable Range 와 비교
        final var selectableRange = eventSelectableParticipleTimeRepository.findByEvent(event);
        boolean isValid = false;
        for (final var day : days) {
            isValid = false;
            for (final var selectable : selectableRange) {
                if (day.equals(selectable.getWeek())) {
                    for (final var time :
                            selectable.getTimeRanges()) {
                        if (timeRange.beginTime().isAfter(time.beginTime()) &&
                                timeRange.beginTime().isBefore(time.endTime()) &&
                                timeRange.endTime().isAfter(time.beginTime()) &&
                                timeRange.endTime().isBefore(time.endTime())) {
                            isValid = true;
                            break;
                        }
                    }
                }
                if (isValid) {
                    break;
                }
            }
        }
        if (!isValid) {
            throw new InvalidValueException("[EventService] Activity Range doesn't match with Selectable", "설정 가능한 시간 범위를 초과합니다");
        } else {
            return event.setActivityTime(days, TimeRange.fixOrder(timeRange));
        }
    }
}
