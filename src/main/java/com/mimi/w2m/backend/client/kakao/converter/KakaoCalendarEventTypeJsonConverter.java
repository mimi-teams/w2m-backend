package com.mimi.w2m.backend.client.kakao.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.mimi.w2m.backend.client.kakao.dto.calendar.event.type.KakaoCalendarEventType;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * KakaoCalendarEventTypeJsonConverter
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
@JsonComponent
public class KakaoCalendarEventTypeJsonConverter {
    public static class Serializer extends JsonSerializer<KakaoCalendarEventType> {

        @Override
        public void serialize(KakaoCalendarEventType value, JsonGenerator gen, SerializerProvider serializers) throws InvalidValueException {
            try {
                gen.writeString(value.name());
            } catch (IOException e) {
                throw new InvalidValueException("Serializer Failed");
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<KakaoCalendarEventType> {

        @Override
        public KakaoCalendarEventType deserialize(JsonParser p, DeserializationContext ctxt) throws InvalidValueException {
            try {
                JsonNode node = p.getCodec().readTree(p);
                return KakaoCalendarEventType.valueOf(node.asText());
            } catch (IOException e) {
                throw new InvalidValueException("Deserializer Failed");
            }
        }
    }
}