package com.mimi.w2m.backend.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.domain.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Objects;

/**
 * RoleJsonConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/12/01
 **/
@JsonComponent
public class RoleJsonConverter {
    private final static Logger logger = LoggerFactory.getLogger(RoleJsonConverter.class.getName());

    public static class Serializer extends JsonSerializer<Role> {
        @Override
        public void serialize(Role value, JsonGenerator gen, SerializerProvider serializers)
                throws InvalidValueException {
            try {
                gen.writeString(value.getKey());
            } catch (IOException e) {
                final var msg = "Serialize Failed";
                logger.error(msg);
                throw new InvalidValueException(msg);
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<Role> {
        @Override
        public Role deserialize(JsonParser p, DeserializationContext ctxt) throws InvalidValueException {
            try {
                JsonNode jsonNode = p.getCodec()
                        .readTree(p);
                final var roleStr = jsonNode.asText();
                if (Objects.equals(roleStr, Role.USER.getKey())) {
                    return Role.USER;
                } else if (Objects.equals(roleStr, Role.GUEST.getKey())) {
                    return Role.GUEST;
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                final var msg = "Deserialize Failed";
                logger.error(msg);
                throw new InvalidValueException(msg);
            }
        }
    }
}