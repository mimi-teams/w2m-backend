package com.mimi.w2m.backend.dto.participant;

import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.Participant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * ParticipantRequestDto
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Getter
@Schema(description = "Participant 를 생성할 때, 전달하는 정보")
public class ParticipantRequestDto implements Serializable {
private                                              String name;
@Schema(description = "password 는 없어도 상관없다") private String password;
private                                              Long   eventId;

@Builder
public ParticipantRequestDto(String name, String password, Long eventId) {
    this.name     = name;
    this.password = password;
    this.eventId  = eventId;
}

protected ParticipantRequestDto() {
}

public Participant to(Event event, String salt, String hashedPw) {
    return Participant.builder().name(name).salt(salt).password(hashedPw).event(event).build();
}
}