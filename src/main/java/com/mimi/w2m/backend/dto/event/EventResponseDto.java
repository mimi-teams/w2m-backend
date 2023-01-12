package com.mimi.w2m.backend.dto.event;

import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.type.ParticipleTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

/**
 * EventResponseDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Getter
@Schema(title = "Event 에 대한 반환 정보", description = "이벤트와 관련된 정보를 반환",
        requiredProperties = {"id", "hostId", "title", "dDay", "selectableParticipleTime", "selectedParticipleTime",
                "color", "description"})
public class EventResponseDto implements Serializable {

    @Schema(description = "이벤트")
    private EventDto event;

    @Schema(description = "선택 가능한 시간")
    private SelectableParticipleTimeDto selectableParticipleTimes;

    protected EventResponseDto() {
    }

    public static EventResponseDto of(Event entity, Set<ParticipleTime> selectableParticipleTime) {
        final var responseDto = new EventResponseDto();
        responseDto.event = EventDto.of(entity);
        responseDto.selectableParticipleTimes = SelectableParticipleTimeDto.of(selectableParticipleTime);

        return responseDto;
    }
}
