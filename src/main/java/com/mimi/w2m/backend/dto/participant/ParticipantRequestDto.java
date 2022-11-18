package com.mimi.w2m.backend.dto.participant;

import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.Participant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ParticipantRequestDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Data
@Schema(description = "Participant를 생성할 때, 전달하는 정보")
public class ParticipantRequestDto {
private final String name;
@Schema(description = "password는 없어도 상관없다")
private final String password;
private final Long   eventId;

public Participant to(Event event) {
    return Participant.builder()
                      .name(name)
                      .password(password)
                      .event(event)
                      .build();
}
}