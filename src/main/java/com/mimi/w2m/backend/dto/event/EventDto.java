package com.mimi.w2m.backend.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.type.TimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * `Entity`와 1 대 1 매칭되는 Dto
 *
 * @author yeh35
 * @since 2022-12-17
 */
@Getter
@Schema(title = "Event 에 대한 반환 정보")
public class EventDto implements Serializable {

    @Schema(title = "Event 의 ID", type = "Integer")
    @NotNull
    private UUID id;

    @Schema(title = "Event 의 Host ID", type = "Integer", description = "이벤트 생성자의 ID")
    @NotNull
    private UUID hostId;

    @Schema(title = "Event 의 제목", type = "String", minLength = 1, maxLength = 50, description = "이벤트 이름")
    @Size(min = 1, max = 50)
    @NotNull
    private String title;

    @Schema(title = "시간 투표 종료일", description = "이벤트 참여자들이 이벤트 시간을 확정하기 위한 투표를 할 수 있는 마지막 시간을 반환", example = "2022-12-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonProperty("dday")
    @Nullable
    private LocalDateTime dDay;

    @Valid
    private ColorDto color;
    @Schema(type = "String", maxLength = 1000, description = "이벤트 세부 설명", nullable = true)
    @Nullable
    @Size(max = 1000)
    private String description;

    @Schema(description = "이벤트의 활동 요일", nullable = true)
    @Nullable
    private Set<DayOfWeek> activityDays;

    @Schema(description = "이벤트의 활동 시간", nullable = true)
    @Nullable
    private TimeRange activityTimeRange;


    @SuppressWarnings("unused")
    protected EventDto() {
    }

    @SuppressWarnings("unused")
    @Builder
    public EventDto(
            UUID id,
            UUID hostId,
            String title,
            @Nullable LocalDateTime dDay,
            ColorDto color,
            @Nullable String description,
            @Nullable Set<DayOfWeek> activityDays,
            @Nullable TimeRange activityTimeRange
    ) {
        this.id = id;
        this.hostId = hostId;
        this.title = title;
        this.dDay = dDay;
        this.color = color;
        this.description = Objects.requireNonNullElse(description, "");
        this.activityDays = activityDays;
        this.activityTimeRange = activityTimeRange;
    }

    public static EventDto of(Event entity) {
        return EventDto.builder()
                .id(entity.getId())
                .hostId(entity.getHost().getId())
                .title(entity.getTitle())
                .dDay(entity.getDDay())
                .color(entity.getColor())
                .description(entity.getDescription())
                .activityDays(entity.getActivityDays())
                .activityTimeRange(entity.getActivityTimeRange())
                .build();
    }
}
