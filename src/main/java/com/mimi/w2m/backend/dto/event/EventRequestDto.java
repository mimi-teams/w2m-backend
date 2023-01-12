package com.mimi.w2m.backend.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * EventRequestDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Getter
@Schema(title = "Event 에 대한 요청 정보", description = "이벤트 생성이나 업데이트에 필요한 정보를 받음", requiredProperties = {"title", "color"})
public class EventRequestDto implements Serializable {
    @Schema(title = "Event 의 제목", type = "String", minLength = 1, maxLength = 50, description = "이벤트 이름")
    @Size(min = 1, max = 50)
    @NotNull
    private String title;

    @Schema(title = "참여자들이 선택 가능한 시간", description = "이벤트 참여자들이 선택할 수 있는 시간의 범위를 지정")
    @NotNull
    @Valid
    private SelectableParticipleTimeDto selectableParticipleTimes;

    // JsonProperty = dDay 가 현재 동작하지 않는다.(dday 로 변환됨) 일단 dday 로 바꿔서 넣기!
    @Schema(title = "시간 투표 종료일", description = "이벤트 참여자들이 이벤트 시간을 확정하기 위한 투표를 할 수 있는 마지막 시간을 지정",
            example = "2022-12-01T00:00:00")
//    @JsonProperty("dDay")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // JUnit Test 에서 Mock 으로 Api 를 호출할 때, JsonFormat 으로 지정해야 LocalDateTime 값이 저장된다.
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Nullable
    private LocalDateTime dDay;

    @Valid
    private ColorDto color;

    @Schema(type = "String", maxLength = 1000, description = "이벤트 세부 설명", nullable = true)
    @Nullable
    @Size(max = 1000)
    private String description;

    @SuppressWarnings("unused")
    protected EventRequestDto() {
    }

    @SuppressWarnings("unused")
    @Builder
    public EventRequestDto(String title,
                           @Nullable SelectableParticipleTimeDto selectableParticipleTimes,
                           @Nullable LocalDateTime dDay, ColorDto color,
                           @Nullable String description) {
        this.title = title;
        this.selectableParticipleTimes = selectableParticipleTimes;
        this.dDay = dDay;
        this.color = color;
        this.description = description;
    }

    public Event to(User user) {
        return Event.builder()
                .title(title)
                .host(user)
                .dDay(dDay)
                .color(color)
                .description(description)
                .build();
    }

}