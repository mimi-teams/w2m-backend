package com.mimi.w2m.backend.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.TimeZone;

/**
 * TimeZoneJsonConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
@JsonComponent
public class TimeZoneJsonConverter {
    private final static Logger logger = LoggerFactory.getLogger(TimeZoneJsonConverter.class.getName());

    public static class Serializer extends JsonSerializer<TimeZone> {

        @Override
        public void serialize(TimeZone value, JsonGenerator gen, SerializerProvider serializers) throws InvalidValueException {
            try {
                gen.writeString(value.getID());
            } catch (IOException e) {
                final var msg = "Serialize Failed";
                logger.error(msg);
                throw new InvalidValueException(msg);
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<TimeZone> {

        @Override
        public TimeZone deserialize(JsonParser p, DeserializationContext ctxt) throws InvalidValueException {
            try {
                JsonNode jsonNode = p.getCodec().readTree(p);
                return TimeZone.getTimeZone(jsonNode.asText());
            } catch (IOException e) {
                final var msg = "Deserialize Failed";
                logger.error(msg);
                throw new InvalidValueException(msg);
            }
        }
    }
}