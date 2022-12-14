package com.mimi.w2m.backend.service;

import com.mimi.w2m.backend.config.exception.EntityNotFoundException;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.domain.*;
import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.domain.type.Role;
import com.mimi.w2m.backend.domain.type.TimeRange;
import com.mimi.w2m.backend.dto.participant.EventParticipantDto;
import com.mimi.w2m.backend.dto.participant.EventParticipantRequestDto;
import com.mimi.w2m.backend.repository.EventParticipantAbleTimeRepository;
import com.mimi.w2m.backend.repository.EventParticipantRepository;
import com.mimi.w2m.backend.repository.vo.EventParticipantQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 각 참여자의 이벤트 참여 시간을 담당하는 서비스
 *
 * @author yeh35
 * @since 2022-11-01
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipantService {
    private final UserService userService;
    private final GuestService guestService;
    private final EventService eventService;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventParticipantAbleTimeRepository eventParticipantAbleTimeRepository;

    /**
     * 이벤트에 참가하기
     * 기존에 참여한 기록이 있는 경우 전부 Delete & Insert 한다.
     *
     * @author yeh35
     * @since 2022-11-01
     */
    @Transactional
    public EventParticipant participate(EventParticipantRequestDto requestDto) {
        final var event = eventService.getEvent(requestDto.getEventId());

        //기존에 참여한 기록이 있는지 확인
        final Optional<EventParticipant> byEventAndUserOrGuest = eventParticipantRepository.findByEventAndUserOrGuest(event, requestDto.getOwnerId(), requestDto.getOwnerId());
        if (byEventAndUserOrGuest.isPresent()) { // 존재하는 경우 기존 내용 삭제
            final EventParticipant participant = byEventAndUserOrGuest.get();
            eventParticipantAbleTimeRepository.deleteByEventParticipant(participant);
            eventParticipantRepository.deleteByEventParticipant(participant);
        }

        // 이벤트 참여 저장
        final EventParticipant eventParticipant;
        switch (requestDto.getOwnerType()) {
            case USER -> {
                final User user = userService.getUser(requestDto.getOwnerId());
                eventParticipant = EventParticipant.ofUser(event, user);
            }
            case GUEST -> {
                final Guest guest = guestService.getGuest(requestDto.getOwnerId());
                eventParticipant = EventParticipant.ofGuest(event, guest);
            }
            default -> throw new InvalidValueException("정의되지 않은 유저 타입입니다. type = " + requestDto.getOwnerType(), "정의되지 않은 유저 타입입니다.");
        }
        eventParticipantRepository.save(eventParticipant);

        // 이벤트 참여 가능한 시간 요일 단위로 뭉치기
        final var weekAbleDaysAndTimeMap = new HashMap<DayOfWeek, Set<TimeRange>>();
        for (final ParticipleTime participleTime : requestDto.getAbleDaysAndTimes()) {
            final DayOfWeek week = participleTime.getWeek();

            if (!weekAbleDaysAndTimeMap.containsKey(week)) {
                weekAbleDaysAndTimeMap.put(week, new HashSet<>(16));
            }

            final Set<TimeRange> ableDaysAndTimeSet = weekAbleDaysAndTimeMap.get(week);
            ableDaysAndTimeSet.addAll(participleTime.getRanges());
        }

        // 이벤트 참여 가능한 시간 저장
        final var ableTimeList = new ArrayList<EventParticipantAbleTime>(weekAbleDaysAndTimeMap.size());
        for (DayOfWeek week : weekAbleDaysAndTimeMap.keySet()) {
            final var ableTime = weekAbleDaysAndTimeMap.get(week);
            ableTimeList.add(EventParticipantAbleTime.ofDayOfWeek(eventParticipant, week, ableTime));
        }
        eventParticipantAbleTimeRepository.saveAll(ableTimeList);

        return eventParticipant;
    }

    public EventParticipant get(UUID id) throws EntityNotFoundException {
        final var participant = eventParticipantRepository.findById(id);
        if (participant.isPresent()) {
            return participant.get();
        } else {
            final var msg = String.format("[EventParticipantService] Entity Not Found(id=%s)", id);
            throw new EntityNotFoundException(msg);
        }
    }

    /**
     * 이벤트 참여자 반환
     *
     * @author yeh35
     * @since 2022-11-01
     */
    public EventParticipant get(UUID eventId, UUID roleId, Role role) throws EntityNotFoundException {
        final var event = eventService.getEvent(eventId);
        final var msg = String.format("[EventParticipantService] Entity Not Found(event=%s, id=%s, role=%s)",
                eventId, roleId, role);
        return switch (role) {
            case USER -> eventParticipantRepository.findByUserInEvent(userService.getUser(roleId), event)
                    .orElseThrow(() -> new EntityNotFoundException(msg));

            case GUEST -> eventParticipantRepository.findByGuestInEvent(guestService.getGuest(roleId), event)
                    .orElseThrow(() -> new EntityNotFoundException(msg));

        };
    }

    @Transactional
    public void deleteAboutEvent(UUID eventId) {
        final Event event = eventService.getEvent(eventId);

        final List<EventParticipant> participantList = eventParticipantRepository.findAllInEvent(event);
        eventParticipantAbleTimeRepository.deleteByEventParticipantList(participantList);
        eventParticipantRepository.deleteAll(participantList);
    }


    /**
     * 특정 이벤트 참여 가능한 모든 시간 조회(등록된 모든 것)
     *
     * @author yeh35
     * @since 2022-10-31
     */
    @SuppressWarnings("DuplicatedCode")
    public List<EventParticipantDto> getEventParticipants(UUID eventId) {
        final var event = eventService.getEvent(eventId);
        final var participants = eventParticipantRepository.findAllInEvent(event);
        final var eventParticipantDtoMap = new HashMap<UUID, List<EventParticipantQueryVo>>(participants.size());

        // 참여자 정보 가져오기
        final List<EventParticipantQueryVo> userQueryVos = eventParticipantAbleTimeRepository.findByEvent(event);
        for (final var participantQueryVo : userQueryVos) {
            final UUID key = participantQueryVo.getId();

            if (!eventParticipantDtoMap.containsKey(key)) {
                eventParticipantDtoMap.put(key, new ArrayList<>(userQueryVos.size() / participants.size()));
            }

            final var eventParticipantDtoList = eventParticipantDtoMap.get(key);
            eventParticipantDtoList.add(participantQueryVo);
        }

        //데이터 형식 맞추기
        final var resultList = new ArrayList<EventParticipantDto>(participants.size());
        for (final UUID participantId : eventParticipantDtoMap.keySet()) {
            final List<EventParticipantQueryVo> queryVos = eventParticipantDtoMap.get(participantId);

            final String name;
            if (queryVos.get(0).getUserName() != null) {
                name = queryVos.get(0).getUserName();
            } else {
                name = queryVos.get(0).getGuestName();
            }

            final Set<ParticipleTime> participleTimeSet = queryVos.stream().map(it -> it.getParticipantAbleTime().toParticipleTime()).collect(Collectors.toSet());


            final var participantDto = EventParticipantDto.builder()
                    .id(participantId)
                    .name(name)
                    .ableDaysAndTimes(participleTimeSet)
                    .build();

            resultList.add(participantDto);
        }

        return resultList;
    }


//    /**
//     * TODO 나중에 알고리즘이 필요한 경우 다시 사용하자..
//     * eventParticipleTime 에 저장된 각 참여자의 가능한 시간의 공통 부분을 계산하여 dayOfWeeks, begin & end time 에 저장한다. 모두가 가능한 시간을 찾으므로, 전원 일치
//     * 알고리즘으로 수행한다. 또한, 선택하지 않은 참여자는 무시한다
//     *
//     * @author teddy
//     * @since 2022/11/20
//     **/
//    @Transactional
//    public Event calculateSharedTime(Long eventId) throws EntityNotFoundException, InvalidValueException {
//        final var formatter = new Formatter();
//        final var converter = new SetParticipleTimeConverter();
//        final var event = eventService.get(eventId);
//        final var participants = getAll(eventId);
//
//        final var selectableDaysAndTimesMap = converter.convertToMap(event.getSelectableDaysAndTimes());
//        final var selectedDaysAndTimesMap = new HashMap<DayOfWeek, Set<TimeRange>>();
//
//        participants.forEach(p -> {
//            final var ableDaysAndTimesMap = converter.convertToMap(p.getAbleDaysAndTimes());
//            if (!verify(selectableDaysAndTimesMap, ableDaysAndTimesMap)) {
//                final var msg = formatter.format(
//                                "[EventParticipantService] Out Of Selectable Range(event=%d, " +
//                                        "participant=%d)", eventId,
//                                p.getId())
//                        .toString();
//                throw new InvalidValueException(msg);
//            }
//            ableDaysAndTimesMap.forEach((d, t) -> {
//                if (selectedDaysAndTimesMap.containsKey(d)) {
//                    // 해당 요일에 가능한 누군가 있다면, 선택될 수 있다.
//                    final var selectedTimeRange = selectedDaysAndTimesMap.get(d);
//                    final var updatedTimeRange = findSharedRange(t, selectedTimeRange);
//                    selectedDaysAndTimesMap.put(d, updatedTimeRange);
//                } else if (selectedDaysAndTimesMap.isEmpty()) {
//                    // 처음에는 그냥 넣을 수 있다
//                    selectedDaysAndTimesMap.put(d, t);
//                }
//                // 처음이 아니거나, 앞서 해당 요일을 선택한 참여자가 없다면, 전원 일치가 불가능하므로 무시한다
//            });
//        });
//        event.setSelectedDaysAndTimes(converter.convertToSet(selectedDaysAndTimesMap));
//        return event;
//    }
//
//    private Set<TimeRange> findSharedRange(Set<TimeRange> firstRanges, Set<TimeRange> secondRanges) {
//        final var out = new HashSet<TimeRange>();
//        final var orderedFirstRanges = firstRanges.stream()
//                .sorted()
//                .toList();
//        final var orderedSecondRanges = secondRanges.stream()
//                .sorted()
//                .toList();
//        // Union Each
//        final var unionOrderedFirstRanges = unionRanges(orderedFirstRanges);
//        final var unionOrderedSecondRanges = unionRanges(orderedSecondRanges);
//
//        // Intersect Each
//        return intersectRanges(unionOrderedFirstRanges, unionOrderedSecondRanges);
//    }
//
//    private List<TimeRange> unionRanges(List<TimeRange> orderedRanges) {
//        final var unionOrderedRanges = new LinkedList<TimeRange>();
//
//        orderedRanges.forEach(range -> {
//            if (unionOrderedRanges.isEmpty()) {
//                unionOrderedRanges.addLast(range);
//            } else {
//                final var last = unionOrderedRanges.removeLast();
//                final var union = TimeRange.Operator.union(range, last);
//                unionOrderedRanges.addLast(union.getFirst());
//                if (!Objects.equals(union.getSecond(), TimeRange.Operator.EMPTY)) {
//                    unionOrderedRanges.addLast(union.getSecond());
//                }
//            }
//        });
//        return unionOrderedRanges;
//    }
//
//    private Set<TimeRange> intersectRanges(List<TimeRange> firstRanges, List<TimeRange> secondRanges) {
//        final var intersectionRanges = new HashSet<TimeRange>();
//        for (var first : firstRanges) {
//            for (var second : secondRanges) {
//                final var intersection = TimeRange.Operator.intersection(first, second);
//                if (!Objects.equals(intersection.getFirst(), TimeRange.Operator.EMPTY)) {
//                    intersectionRanges.add(intersection.getFirst());
//                }
//                if (!Objects.equals(intersection.getSecond(), TimeRange.Operator.EMPTY)) {
//                    intersectionRanges.add(intersection.getSecond());
//                }
//            }
//        }
//        return intersectionRanges;
//    }

//    private boolean verify(Map<DayOfWeek, Set<TimeRange>> selectable, Map<DayOfWeek, Set<TimeRange>> selected) {
//        for (var day : selected.keySet()) {
//            if (!selectable.containsKey(day)) {
//                return false;
//            }
//            final var selectableTimeRanges = selectable.get(day);
//
//            final var selectedTimeRanges = selected.get(day);
//            for (var selectedRange : selectedTimeRanges) {
//                var isIncluded = false;
//                for (var selectableRange : selectableTimeRanges) {
//                    final var intersectedRange = TimeRange.Operator.intersection(selectedRange, selectableRange)
//                            .getFirst();
//                    if (!Objects.equals(intersectedRange, selectedRange)) {
//                        isIncluded = true;
//                        break;
//                    }
//                }
//                if (!isIncluded) {
//                    return false;
//                }
//            }
//
//        }
//        return true;
//    }
}
