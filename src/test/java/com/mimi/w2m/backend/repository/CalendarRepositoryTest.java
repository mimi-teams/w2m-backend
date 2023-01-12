package com.mimi.w2m.backend.repository;

import com.mimi.w2m.backend.config.db.SpringDbConfig;
import com.mimi.w2m.backend.domain.Calendar;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.domain.type.PlatformType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(profiles = "test")
@DataJpaTest
@Import(SpringDbConfig.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CalendarRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CalendarRepository calendarRepository;

    /**
     * Entity Converter 는 반드시 Null 처리를 해줘야 한다
     *
     * @author teddy
     * @since 2023/01/11
     **/
    @Test
    @DisplayName("findAllByUserAndEvent Test")
    void test() {
        //given
        final var user = userRepository.save(User.builder()
                .name("test")
                .email("test@meezle.org")
                .build());
        final var event1 = eventRepository.save(Event.builder()
                .host(user)
                .title("testEvent")
                .build());
        final var event2 = eventRepository.save(Event.builder()
                .host(user)
                .title("testEvent")
                .build());
        //when
        final var calendar1 = calendarRepository.save(Calendar.builder()
                .user(user)
                .event(event1)
                .platform(PlatformType.KAKAO)
                .platformCalendarId("calender_id")
                .platformEventId("event_id")
                .build());
        final var calendar2 = calendarRepository.save(Calendar.builder()
                .user(user)
                .event(event2)
                .platform(PlatformType.KAKAO)
                .platformCalendarId("calender_id")
                .platformEventId("event_id")
                .build());

        //then
        assertThat(calendarRepository.findByUserAndEventInPlatform(user, event1, PlatformType.KAKAO)).contains(calendar1);
        assertThat(calendarRepository.findAll()).contains(calendar1, calendar2);
    }
}