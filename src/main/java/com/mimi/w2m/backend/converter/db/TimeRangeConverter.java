package com.mimi.w2m.backend.converter.db;

import com.mimi.w2m.backend.domain.type.TimeRange;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

/**
 * TimeRangeConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
@Converter
public class TimeRangeConverter implements AttributeConverter<TimeRange, String> {
    @Override
    public String convertToDatabaseColumn(TimeRange attribute) {
        return Objects.isNull(attribute) ? null : attribute.toString();
    }

    @Override
    public TimeRange convertToEntityAttribute(String dbData) {
        return Objects.isNull(dbData) || dbData.isEmpty() ? null : TimeRange.of(dbData);
    }
}