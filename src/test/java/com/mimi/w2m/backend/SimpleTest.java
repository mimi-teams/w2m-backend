package com.mimi.w2m.backend;

import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

public class SimpleTest {

    @Test
    void testKeyGenerator() {
        SecureRandom secRandom = new SecureRandom();

        byte[] key = new byte[32];
        secRandom.nextBytes(key);
        System.out.println(new String(Base64Utils.encode(key)));
    }

    @Test
    void testDateTime() {
        final var dateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        final var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        System.out.println(dateTime.format(formatter));
        final var nextMonday = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println(nextMonday.format(formatter));
    }

    @Test
    void testDateTime2() {
        final var days = Set.of(DayOfWeek.MONDAY);
        final var calendarEventEarliestDay = days.stream()
                .map(dayOfWeek -> LocalDate.now().with(TemporalAdjusters.next(dayOfWeek)))
                .min(LocalDate::compareTo).get().atTime(LocalTime.of(1, 0, 0));
        System.out.println(calendarEventEarliestDay.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")));
    }
}
