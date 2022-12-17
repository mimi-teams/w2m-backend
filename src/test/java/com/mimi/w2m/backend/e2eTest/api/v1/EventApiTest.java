package com.mimi.w2m.backend.e2eTest.api.v1;

import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.dto.event.ColorDto;
import com.mimi.w2m.backend.dto.event.EventRequestDto;
import com.mimi.w2m.backend.e2eTest.End2EndTest;
import com.mimi.w2m.backend.repository.EventRepository;
import com.mimi.w2m.backend.repository.UserRepository;
import com.mimi.w2m.backend.testFixtures.UserTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
public class EventApiTest extends End2EndTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected EventRepository eventRepository;

    @Test
    void 이벤트_등록() throws Exception {
        // given
        final User user = UserTestFixture.createUser();
        userRepository.save(user);

        final String token = login(user);
        final var requestDto = EventRequestDto.builder()
                .title("아아 테스트")
                .selectableParticipleTimes(Set.of(
                        ParticipleTime.of("MONDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                        ParticipleTime.of("TUESDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                        ParticipleTime.of("THURSDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|")
                ))
                .dDay(null)
                .color(ColorDto.of("#ffffff"))
                .description("테스트입니다람쥐")
                .build();


        //when & then
        mockMvc.perform(
                        post("/v1/events")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.event.id").exists())
                .andExpect(jsonPath("$.data.event.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.data.event.selectableParticipleTimes").exists())
                .andExpect(jsonPath("$.data.event.selectedParticipleTimes").exists())
                .andExpect(jsonPath("$.data.event.color").value(requestDto.getColor().toString()))
                .andExpect(jsonPath("$.data.event.dday").exists())
        ;
    }


}
