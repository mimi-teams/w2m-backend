package com.mimi.w2m.backend.converter.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SetDayOfWeekConverterTest
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
class SetDayOfWeekConverterTest {
    @Test
    @DisplayName("SetDayOfWeek -> String Test")
    void testToDb() {
        //given
        final var val = Set.of(DayOfWeek.values());
        final var converter = new SetDayOfWeekConverter();
        final var expected = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY,".split(",");
        //when & then
        assertThat(converter.convertToDatabaseColumn(val).split(",")).containsOnly(expected);
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo(null);
        assertThat(converter.convertToDatabaseColumn(Set.of())).isEqualTo(null);
    }

    @Test
    @DisplayName("String -> SetDayOfWeek Test")
    void testToObj() {
        //given
        final var val = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY,";
        final var converter = new SetDayOfWeekConverter();
        final var expected = Set.of(DayOfWeek.values());

        //when&then
        assertThat(converter.convertToEntityAttribute(val)).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(converter.convertToEntityAttribute("")).isEqualTo(null);
        assertThat(converter.convertToEntityAttribute(null)).isEqualTo(null);
    }
}