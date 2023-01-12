package com.mimi.w2m.backend.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import javax.validation.Valid;

/**
 * EventActivityTimeDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
@Schema(title = "이벤트의 활동 시간", description = "이벤트의 확정된 활동 시간 정보")
public record EventActivityTimeDto(
        @Schema(title = "이벤트의 선택된 시간", description = "확정된 이벤트 시간을 지정")
        @Nullable
        @Valid
        SelectableParticipleTimeDto activityTime
) {
}
