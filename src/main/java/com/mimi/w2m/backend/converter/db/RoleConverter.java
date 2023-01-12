package com.mimi.w2m.backend.converter.db;

import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.domain.type.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

/**
 * RoleConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/
@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return Objects.isNull(attribute) ? Role.GUEST.getKey() : attribute.getKey();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) throws InvalidValueException {
        if (Objects.isNull(dbData)) {
            throw new InvalidValueException("Null role");
        } else if (dbData.equals(Role.GUEST.getKey())) {
            return Role.GUEST;
        } else if (dbData.equals(Role.USER.getKey())) {
            return Role.USER;
        } else {
            final var msg = String.format("Invalid Role String : %s", dbData);
            throw new InvalidValueException(msg);
        }
    }
}