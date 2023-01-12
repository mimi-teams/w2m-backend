package com.mimi.w2m.backend.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mimi.w2m.backend.converter.json.RRuleJsonConverter;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Set;

/**
 * RRule
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
/*
Object Mapper 로 직렬화하기 위해 JsonSerialize & Deserialize 를 지정해야 한다
 */
@JsonSerialize(using = RRuleJsonConverter.Serializer.class)
@JsonDeserialize(using = RRuleJsonConverter.Deserializer.class)
public record CalendarRRule(
        @JsonProperty(value = "FREQ")
        FreqType freq,
        @JsonProperty(value = "BYDAY")
        Set<DayOfWeek> byDay
//        @DateTimeFormat(pattern = "yyyyMMdd`T`HHmmss`Z`")
//        @JsonProperty(value = "UNTIL")
//        LocalDateTime until
) {

    public static CalendarRRule of(Set<DayOfWeek> days) {
        return new CalendarRRule(FreqType.WEEKLY, days);
    }

    public enum FreqType {
        WEEKLY("WEEKLY"),
        ;
        @Getter
        private final String value;

        FreqType(String value) {
            this.value = value;
        }
    }
}