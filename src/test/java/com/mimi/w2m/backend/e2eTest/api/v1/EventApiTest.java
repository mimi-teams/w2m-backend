package com.mimi.w2m.backend.e2eTest.api.v1;

import com.mimi.w2m.backend.domain.*;
import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.dto.event.ColorDto;
import com.mimi.w2m.backend.dto.event.EventRequestDto;
import com.mimi.w2m.backend.dto.event.SelectableParticipleTimeDto;
import com.mimi.w2m.backend.dto.participant.EventParticipantRequest;
import com.mimi.w2m.backend.dto.participant.guest.GuestLoginRequest;
import com.mimi.w2m.backend.e2eTest.End2EndTest;
import com.mimi.w2m.backend.repository.EventParticipantAbleTimeRepository;
import com.mimi.w2m.backend.repository.EventParticipantRepository;
import com.mimi.w2m.backend.repository.EventRepository;
import com.mimi.w2m.backend.repository.UserRepository;
import com.mimi.w2m.backend.service.GuestService;
import com.mimi.w2m.backend.testFixtures.EventTestFixture;
import com.mimi.w2m.backend.testFixtures.GuestTestFixture;
import com.mimi.w2m.backend.testFixtures.ParticipleTimeFixture;
import com.mimi.w2m.backend.testFixtures.UserTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
public class EventApiTest extends End2EndTest {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected EventParticipantRepository eventParticipantRepository;

    @Autowired
    protected EventParticipantAbleTimeRepository eventParticipantAbleTimeRepository;


    @Autowired
    protected GuestService guestService;

    @Test
    void ?????????_??????() throws Exception {
        // given
        final User user = UserTestFixture.createUser();
        userRepository.save(user);

        final String token = login(user);
        final var requestDto = EventRequestDto.builder()
                .title("?????? ?????????")
                .selectableParticipleTimes(
                        SelectableParticipleTimeDto.of(
                                Set.of(
                                        ParticipleTime.of("MONDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                                        ParticipleTime.of("TUESDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                                        ParticipleTime.of("THURSDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|")
                                )
                        )
                )
                .dDay(LocalDateTime.now().plusDays(7))
                .color(ColorDto.of("#ffffff"))
                .description("????????????????????????")
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
                .andExpect(jsonPath("$.data.event.color").value(requestDto.getColor().toString()))
                .andExpect(jsonPath("$.data.event.dDay").exists())
                .andExpect(jsonPath("$.data.selectableParticipleTimes").exists())
        ;
    }

    @Test
    void ?????????_??????() throws Exception {
        // given
        final User user = UserTestFixture.createUser();
        userRepository.save(user);

        final Event event = EventTestFixture.createEvent(user);
        eventRepository.save(event);

        //when & then
        mockMvc.perform(
                        get("/v1/events/{id}", event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.event.id").exists())
                .andExpect(jsonPath("$.data.event.title").value(event.getTitle()))
                .andExpect(jsonPath("$.data.event.color").value(event.getColor().toString()))
                .andExpect(jsonPath("$.data.event.dDay").exists())
                .andExpect(jsonPath("$.data.selectableParticipleTimes").exists())
                .andExpect(jsonPath("$.data.eventParticipants").exists())
        ;
    }

    @Test
    void ?????????_??????() throws Exception {
        // given
        final User user = UserTestFixture.createUser();
        userRepository.save(user);

        final String token = login(user);

        final Event event = EventTestFixture.createEvent(user);
        eventRepository.save(event);

        final var requestDto = EventRequestDto.builder()
                .title("????????? ????????? ?????????")
                .selectableParticipleTimes(
                        SelectableParticipleTimeDto.of(
                                Set.of(
                                        ParticipleTime.of("SATURDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                                        ParticipleTime.of("SUNDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|")
                                )
                        )
                )
                .dDay(null)
                .color(ColorDto.of("#ffffff"))
                .description("???????????????????????????")
                .build();


        //when & then
        mockMvc.perform(
                        patch("/v1/events/{id}", event.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.event.id").exists())
                .andExpect(jsonPath("$.data.event.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.data.event.color").value(requestDto.getColor().toString()))
                .andExpect(jsonPath("$.data.event.dDay").exists())
                .andExpect(jsonPath("$.data.selectableParticipleTimes").exists())
        ;
    }

    @Test
    void ?????????_??????() throws Exception {
        // given
        final User user = UserTestFixture.createUser();
        userRepository.save(user);

        final String token = login(user);

        final Event event = EventTestFixture.createEvent(user);
        eventRepository.save(event);

        //when & then
        mockMvc.perform(
                        delete("/v1/events/{id}", event.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void ?????????_??????_?????????_?????????_??????_??????() throws Exception {
        // given
        final User user = UserTestFixture.createUser();
        userRepository.save(user);

        final String token = login(user);

        final Event event = EventTestFixture.createEvent(user);
        eventRepository.save(event);

        final var guestCreateDto = GuestTestFixture.createGuestCreateDto(event);
        Guest guest = guestService.create(guestCreateDto);

        EventParticipant eventParticipant = eventParticipantRepository.save(EventParticipant.ofGuest(event, guest));

        Set<EventParticipantAbleTime> ableTimes = EventParticipantAbleTime.of(eventParticipant, new HashSet<>(ParticipleTimeFixture.createParticipleTime()));
        eventParticipantAbleTimeRepository.saveAll(ableTimes);

        //when & then
        mockMvc.perform(
                        delete("/v1/events/{id}", event.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void ?????????_?????????_?????????_DB???_??????_??????() throws Exception {
        // given
        final User user = userRepository.save(UserTestFixture.createUser());
        final Event event = eventRepository.save(EventTestFixture.createEvent(user));


        final var loginRequest = GuestLoginRequest.builder()
                .name("????????? ?????????")
                .password("1234")
                .build();

        //when & then
        mockMvc.perform(
                        post("/v1/events/{eventId}/guests/login", event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(loginRequest.getName()))
                .andExpect(jsonPath("$.data.token").exists())
        ;
    }

    @Test
    void ?????????_?????????_?????????() throws Exception {
        // given
        final User user = userRepository.save(UserTestFixture.createUser());
        final Event event = eventRepository.save(EventTestFixture.createEvent(user));

        final var guestCreateDto = GuestTestFixture.createGuestCreateDto(event);
        guestService.create(guestCreateDto);

        final var loginRequest = GuestLoginRequest.builder()
                .name(guestCreateDto.getName())
                .password(guestCreateDto.getPassword())
                .build();

        //when & then
        mockMvc.perform(
                        post("/v1/events/{eventId}/guests/login", event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(guestCreateDto.getName()))
                .andExpect(jsonPath("$.data.token").exists())
        ;
    }

    @Test
    void ?????????_?????????_?????????_??????() throws Exception {
        // given
        final User user = userRepository.save(UserTestFixture.createUser());
        final Event event = eventRepository.save(EventTestFixture.createEvent(user));

        final var guestCreateDto = GuestTestFixture.createGuestCreateDto(event);
        String token = loginGuest(guestCreateDto);


        final var request = new EventParticipantRequest(Set.of(
                ParticipleTime.of("MONDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                ParticipleTime.of("TUESDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|"),
                ParticipleTime.of("THURSDAY[T]10:00:00-12:00:00|13:00:00-14:00:00|")
        ));

        //when & then
        mockMvc.perform(
                        post("/v1/events/{eventId}/guests/participate", event.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("???????????? ????????? ?????? ????????? ??????")
    void getAllByHost() throws Exception {
        //given
        final var user = userRepository.save(UserTestFixture.createUser());
        final var events = IntStream.iterate(1, i -> i + 1).limit(10)
                .mapToObj(i -> EventTestFixture.createEvent(user))
                .toList();
        eventRepository.saveAll(events);

        final var token = login(user);

        //when & then
        mockMvc.perform(
                        get("/v1/events/host")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(10)))
        ;
    }
}
