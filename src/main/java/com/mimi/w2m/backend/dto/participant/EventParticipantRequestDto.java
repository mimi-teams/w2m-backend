package com.mimi.w2m.backend.dto.participant;

import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.domain.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * EventParticipantRequestDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Getter
@Schema(title = "Event 참여자에 대한 요청 정보", description = "참여자 생성이나 업데이트에 필요한 정보를 받음",
        requiredProperties = {"ableDaysAndTimes", "eventId", "ownerId", "ownerType"})
public class EventParticipantRequestDto implements Serializable {
    @Schema(title = "Event 의 ID", type = "Integer")
    @NotNull
    private UUID eventId;

    @Schema(title = "참가자의 실제 ID")
    @NotNull
    private UUID ownerId;

    @Schema(title = "참가자의 실제 유형")
    @NotNull
    private Role ownerType;

    @Schema(title = "참여자가 선택한 시간", description = "참여자가 선택한 시간 정보를 받음(null = 모든 선택 가능한 시간이 가능하다고 가정)")
    @Nullable
    @Valid
    private Set<ParticipleTime> ableDaysAndTimes;

    @SuppressWarnings("unused")
    protected EventParticipantRequestDto() {
    }

    @SuppressWarnings("unused")
    @Builder
    public EventParticipantRequestDto(UUID eventId, UUID ownerId, Role ownerType,
                                      @Nullable Set<ParticipleTime> ableDaysAndTimes) {
        this.eventId = eventId;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.ableDaysAndTimes = ableDaysAndTimes;
    }
}