package com.mimi.w2m.backend.dto.event;

import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.dto.participant.EventParticipantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * EventResponseDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Getter
@Schema(title = "Event 에 대한 반환 정보", description = "이벤트와 관련된 정보를 반환")
public class EventGetResponse implements Serializable {

    @Schema(description = "이벤트")
    private EventDto event;

    @Schema(description = "선택 가능한 시간")
    private Set<ParticipleTime> selectableParticipleTimes;


    @Schema(name = "이벤트 참여자 정보들")
    private List<EventParticipantDto> eventParticipants;

    protected EventGetResponse() {
    }

    public static EventGetResponse of(Event entity, Set<ParticipleTime> selectableParticipleTime, List<EventParticipantDto> eventParticipants) {
        final var responseDto = new EventGetResponse();
        responseDto.event = EventDto.builder()
                .id(entity.getId())
                .hostId(entity.getHost().getId())
                .title(entity.getTitle())
                .dDay(entity.getDDay())
                .color(entity.getColor())
                .description(entity.getDescription())
                .build();
        responseDto.selectableParticipleTimes = selectableParticipleTime;
        responseDto.eventParticipants = eventParticipants;
        return responseDto;
    }
}