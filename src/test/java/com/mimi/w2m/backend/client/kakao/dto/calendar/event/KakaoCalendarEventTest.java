package com.mimi.w2m.backend.client.kakao.dto.calendar.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mimi.w2m.backend.dto.calendar.CalendarRRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KakaoCalendarEventTest
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
class KakaoCalendarEventTest {
    /*
    JsonFormat 으로 지정해야, Mapper 가 원하는 포맷의 날짜로 바꿔준다.
     */
    @Test
    @DisplayName("Serialize & Deserialize 테스트")
    void test() throws JsonProcessingException {
        //given
        final var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        final var event = KakaoCalendarEvent.builder()
                .title("testEvent")
                .time(KakaoCalendarEventTime.builder()
                        .startAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                        .endAt(LocalDateTime.of(2023, 1, 1, 12, 12, 0))
                        .timeZone(TimeZone.getTimeZone("Asia/Seoul"))
                        .build()
                )
                .rRule(new CalendarRRule(CalendarRRule.FreqType.WEEKLY, Set.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)))
                .build();
        //when
        final var jsonEvent = mapper.writeValueAsString(event);
        final var converted = mapper.readValue(jsonEvent, KakaoCalendarEvent.class);
        //then
        assertThat(converted).isEqualTo(event);
    }

    @Test
    @DisplayName("Timezone Test")
    void testTimeZone() {
        //given
        final var zone = TimeZone.getTimeZone("Asia/Seoul");
        final var date = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        //when & then
        assertThat(date.plus(zone.getRawOffset(), ChronoUnit.MILLIS)).isEqualTo(date.plus(+9, ChronoUnit.HOURS));
    }
}