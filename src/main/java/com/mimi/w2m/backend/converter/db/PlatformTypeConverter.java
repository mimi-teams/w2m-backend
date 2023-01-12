package com.mimi.w2m.backend.converter.db;

import com.mimi.w2m.backend.domain.type.PlatformType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

/**
 * PlatformTypeConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/08
 **/
@Converter
public class PlatformTypeConverter implements AttributeConverter<PlatformType, String> {
    @Override
    public String convertToDatabaseColumn(PlatformType attribute) {
        return Objects.isNull(attribute) ? "" : attribute.name();
    }

    @Override
    public PlatformType convertToEntityAttribute(String dbData) {

        return Objects.isNull(dbData) || dbData.isEmpty() ? null : PlatformType.valueOf(dbData);
    }
}