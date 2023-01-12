package com.mimi.w2m.backend.converter.db;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SetDayOfWeekConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
@Converter
public class SetDayOfWeekConverter implements AttributeConverter<Set<DayOfWeek>, String> {
    @Override
    public String convertToDatabaseColumn(Set<DayOfWeek> attribute) {
        if (Objects.isNull(attribute) || attribute.isEmpty()) {
            return null;
        } else {
            final var builder = new StringBuilder();
            attribute.forEach(day -> builder.append(day.name()).append(','));
            return builder.toString();
        }
    }

    @Override
    public Set<DayOfWeek> convertToEntityAttribute(String dbData) {
        if(Objects.isNull(dbData) || dbData.isEmpty()) {
            return null;
        } else {
            return Arrays.stream(dbData.split(",")).map(DayOfWeek::valueOf).collect(Collectors.toSet());
        }
    }
}